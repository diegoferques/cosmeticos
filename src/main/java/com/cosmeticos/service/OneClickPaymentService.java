package com.cosmeticos.service;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.commons.SuperpayFormaPagamento;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.*;
import com.cosmeticos.payment.superpay.ws.oneclick.DadosCadastroPagamentoOneClickWS;
import com.cosmeticos.payment.superpay.ws.oneclick.ResultadoPagamentoWS;
import com.cosmeticos.repository.PaymentRepository;
import com.cosmeticos.validation.OrderValidationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Created by matto on 08/08/2017.
 */
@Slf4j
@Service
public class OneClickPaymentService implements Charger{

    @Autowired
    private SuperpayOneClickClient oneClickClient;

    @Autowired
    private SuperpayCompletoClient completoClient;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * Feito pq em dev, usando h2, o id de payment, usado como numeroTransacao, se repete toda vez q a aplicacao eh iniciada.
     */
    @Value("${superpay.numerotransacao.fake:false}")
    private Boolean fakeNumeroTransacao;

    //@Value("${superpay.mock.reserve.response}")
    private Integer mockReserveResponse;

    //@Value("${superpay.mock.capture.response}")
    private Integer mockCaptureResponse;

    @Override
    public ChargeResponse<Object> addCard(ChargeRequest<Payment> chargeRequest) {

        Order order = chargeRequest.getBody().getOrder();

        String emailComprador = order
                .getIdCustomer()
                .getUser()
                .getEmail();

        Payment payment = chargeRequest.getBody();

        CreditCard creditCard = payment.getCreditCard();

        String nomeTitularCartaoCredito = creditCard.getOwnerName();
        String numeroCartaoCredito = creditCard.getNumber();
        String dataValidadeCartao = creditCard.getExpirationDate();

        SuperpayFormaPagamento formaPagamento = SuperpayFormaPagamento.valueOf(creditCard.getVendor());

        String result = oneClickClient.addCard(dataValidadeCartao,
                emailComprador,
                formaPagamento,
                nomeTitularCartaoCredito,
                numeroCartaoCredito);

        ChargeResponse<Object> chargeResponse = new ChargeResponse<>(result);

        return chargeResponse;

    }

    @Override
    public ChargeResponse<Object> reserve(ChargeRequest<Payment> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard

        Payment receivedPayment = chargeRequest.getBody();

        Payment persistentPayment = paymentRepository.findOne(receivedPayment.getId());

        Order persistentOrder = persistentPayment.getOrder();

        ProfessionalCategory professionalCategory = persistentOrder.getProfessionalCategory();
        Category category = professionalCategory.getCategory();

        Customer customer = persistentOrder.getIdCustomer();
        Address address = customer.getAddress();
        User user = customer.getUser();

        Long numeroTransacao = Long.valueOf(persistentPayment.getExternalTransactionId());

        // Nao se pega do payment pois o usuario pode ter alterado o cartao antees de concluir a order.
        // TODO incluir trava pra nao deixar alterar cartao caso haja order aberta
        CreditCard creditCard = user.getCreditCardCollection()
                .stream()
                .findFirst()
                .get();

        String token = creditCard.getToken();
        String campainha = "http://ngrok/campainha/superpay";
        String urlRedirecionamentoNaoPago = "";
        String urlRedirecionamentoPago = "";
        long valor = persistentPayment.getPriceRule().getPrice();
        String bairroEnderecoComprador = address.getNeighborhood();
        String cepEnderecoComprador = address.getCep();
        String cidadeEnderecoCompra = address.getCity();
        String emailComprador = user.getEmail();
        String enderecoComprador = address.getAddress();
        String estadoEnderecoComprador = address.getState();
        String nomeComprador = customer.getNameCustomer();
        String numeroEnderecoComprador = address.getNumber();
        String paisComprar = address.getCountry();
        String sexoComprador = String.valueOf(customer.getGenre());
        String telefoneComprador = customer.getCellPhone();
       // String cvv = creditCard.getSecurityCode(); //Eh oneclick.. nao precisa isso
        String nomeProduto = category.getName();
        Long valorUnitarioProduto = valor;
        long tipoCliente = user.getPersonType().ordinal();

        String nomeCategoria = ofNullable(category.getOwnerCategory())
                .map(c -> c.getName())
                .orElse(category.getName());

        SuperpayOneClickClient.RequestWrapper requestWrapper = new SuperpayOneClickClient.RequestWrapper();
        requestWrapper.setBairroEnderecoComprador(bairroEnderecoComprador);
        requestWrapper.setCampainha(campainha);
        requestWrapper.setCepEnderecoComprador(cepEnderecoComprador);
        requestWrapper.setCidadeEnderecoCompra(cidadeEnderecoCompra);
        //requestWrapper.setCvv(cvv);
        requestWrapper.setEstadoEnderecoComprador(estadoEnderecoComprador);
        requestWrapper.setEnderecoComprador(enderecoComprador);
        requestWrapper.setEmailComprador(emailComprador);
        requestWrapper.setNomeCategoria(nomeCategoria);
        requestWrapper.setNomeComprador(nomeComprador);
        requestWrapper.setNomeProduto(nomeProduto);
        requestWrapper.setNumeroEnderecoComprador(numeroEnderecoComprador);
        requestWrapper.setNumeroTransacao(numeroTransacao);
        requestWrapper.setPaisComprador(paisComprar);
        requestWrapper.setSexoComprador(sexoComprador);
        requestWrapper.setTelefoneAdicionalComprador(telefoneComprador);
        requestWrapper.setTelefoneComprador(telefoneComprador);
        requestWrapper.setTipoCliente(tipoCliente);
        requestWrapper.setToken(token);
        requestWrapper.setValor(valor);
        requestWrapper.setValorUnitarioProduto(valorUnitarioProduto);
        requestWrapper.setUrlRedirecionamentoNaoPago(urlRedirecionamentoNaoPago);
        requestWrapper.setUrlRedirecionamentoPago(urlRedirecionamentoPago);

        ResultadoPagamentoWS result = oneClickClient.pay(requestWrapper);

        if(mockReserveResponse != null)
        {
            log.warn("Estamos falsificando a resposta do superpay RESERVE para fins de testes.");
            result.setStatusTransacao(mockReserveResponse);
        }

        return buildResponse(result);
    }

    private ChargeResponse<Object> buildResponse(ResultadoPagamentoWS result) {

        Integer superpayStatusStransacao = result.getStatusTransacao();

        Payment.Status paymentStatus = Payment.Status.fromSuperpayStatus(superpayStatusStransacao);

        org.apache.log4j.MDC.put("superpayStatusStransacao", paymentStatus.toString() + "(" + paymentStatus.getSuperpayStatusTransacao() + ")");

        if (paymentStatus.isSuccess()) {

            //SE FOR PAGO E CAPTURADO, HOUVE UM ERRO NAS DEFINICOES DA SUPERPAY, MAS FOI FEITO O PAGAMENTO
            if (Payment.Status.PAGO_E_CAPTURADO.equals(paymentStatus)) {
                log.warn("Pedido retornou como PAGO E CAPTURADO, mas o correto seria PAGO E 'NÃO' CAPTURADO.");
            }

            //SE TRANSACAO JA PAGA, ESTAMOS TENTANDO EFETUAR O PAGAMENTO DE UM PEDIDO JA PAGO ANTERIORMENTE
            if (Payment.Status.TRANSACAO_JA_PAGA.equals(paymentStatus)) {
                log.warn("Pedido retornou como TRANSACAO JA PAGA, possível tentativa de pagamento em duplicidade.");
            }

            ChargeResponse<Object> response = new ChargeResponse<>(result);
            response.setResponseCode(paymentStatus.getResponseCode());

            return response;

        } else {
            throw new OrderValidationException(ResponseCode.GATEWAY_FAILURE, "Gateway respondeu com status  de erro");
        }
    }

    @Override
    public ChargeResponse<Object> capture(ChargeRequest<Payment> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard

        Payment receivedPayment = chargeRequest.getBody();

        Payment persistentPayment = paymentRepository.findOne(receivedPayment.getId());

        Order persistentOrder = persistentPayment.getOrder();

        Customer persistentCustomer = persistentOrder.getIdCustomer();

        User persistentUser = persistentCustomer.getUser();

        Optional<CreditCard> optionalcc = persistentUser.getCreditCardCollection()
                .stream()
                .findFirst();

        if(optionalcc.isPresent()) {
            CreditCard creditCard = optionalcc.get();

            String nomeTitularCarttaoCredito = creditCard.getOwnerName();

            // TODO: incluir  a url verdadeira
            String urlCampainha = "http://ngrok/campainha/superpay";
            Long valor = receivedPayment.getPriceRule().getPrice();
            String codigoSeguranca = creditCard.getSecurityCode();
            String ip = httpServletRequest.getRemoteAddr();
            Long numeroTransacao = Long.valueOf(persistentPayment.getExternalTransactionId());
            int parcelas = persistentOrder.getPaymentCollection().size();
            SuperpayFormaPagamento codigoFormaPagamento = SuperpayFormaPagamento.valueOf(creditCard.getVendor());
            //String dataValidadeCartao = String.valueOf(creditCard.getExpirationDate());
            //String urlRedirecionamentoNaoPago = "urlRedirecionamentoNaoPago.com";
            //String urlRedirecionamentoPago = "urlRedirecionamentoPago.com";
            //long valorDesconto = 10L;
            //String origemTransacao = "ORIGEM";
            //int idioma = 1;

            MDC.put("superpayNumeroTransacao", String.valueOf(numeroTransacao));

            /*
            Existem duas classes com o mesmo nome nos dois wsdls da superpay. Como usamos as duas (oneclick e completo),
            precisamos dar o qualified name da classe que queremos usar.
             */
            com.cosmeticos.payment.superpay.ws.completo.ResultadoPagamentoWS result = completoClient.capturePayment(
                    codigoSeguranca,
                    ip,
                    nomeTitularCarttaoCredito,
                    valor,
                    urlCampainha,
                    numeroTransacao,
                    parcelas, codigoFormaPagamento);

            if(mockCaptureResponse != null)
            {
                log.warn("Estamos falsificando a resposta do superpay CAPTURE para fins de testes.");
                result.setStatusTransacao(mockReserveResponse);
            }

            MDC.put("superpayStatusStransacao", String.valueOf(result.getStatusTransacao()));
            MDC.put("superpayMensagemVenda", result.getMensagemVenda());

            ChargeResponse<Object> response = new ChargeResponse<>(result);


            response.setResponseCode(Payment.Status.fromSuperpayStatus(
                    result.getStatusTransacao())
                    .getResponseCode()
            );

            return response;
        }
        else
        {
            throw new OrderValidationException(ResponseCode.INVALID_PAYMENT_TYPE, "Usuario nao possui mais cartao. " +
                    "Verifique se no meio do processo de contratação do profissional ele descadastrou seu cartao de credito");
        }
    }

    @Override
    public ChargeResponse<Object> getStatus(ChargeRequest<Payment> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard
        Order order = chargeRequest.getBody().getOrder();

        CreditCard creditCard = order.getCreditCardCollection().stream().findFirst().get();
        Customer customer = order.getIdCustomer();
        User user = customer.getUser();

        Payment payment = (Payment) order.getPaymentCollection();
        SuperpayFormaPagamento formaPagamento = SuperpayFormaPagamento.valueOf(creditCard.getVendor());


        String dataValidadeCartao = creditCard.getExpirationDate();
        String emailComprador = user.getEmail();
        String nomeTitularCartaoCredito = creditCard.getOwnerName();
        String numeroCartaoCredito = creditCard.getSuffix();
        String token = creditCard.getToken();

        DadosCadastroPagamentoOneClickWS result = (oneClickClient.readCard(
                dataValidadeCartao,
                emailComprador,
                formaPagamento,
                nomeTitularCartaoCredito,
                numeroCartaoCredito,
                token));

        return new ChargeResponse<Object>(result);
    }

    // TODO avaliar se as classes concretas realmente devem implementar isso pra expor ou se isso fica de reposabilidade de cada classe concreta.
    @Override
    public Boolean updatePaymentStatus(Payment payment) {
        return true;
    }

}
