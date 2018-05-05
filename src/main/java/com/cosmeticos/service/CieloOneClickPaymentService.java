package com.cosmeticos.service;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.commons.SuperpayFormaPagamento;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.*;
import com.cosmeticos.payment.cielo.CieloTransactionClient;
import com.cosmeticos.payment.cielo.model.*;
import com.cosmeticos.payment.superpay.SuperpayCompletoClient;
import com.cosmeticos.payment.superpay.SuperpayOneClickClient;
import com.cosmeticos.payment.superpay.ws.oneclick.DadosCadastroPagamentoOneClickWS;
import com.cosmeticos.payment.superpay.ws.oneclick.ResultadoPagamentoWS;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.PaymentRepository;
import com.cosmeticos.validation.OrderValidationException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.secure.spi.IntegrationException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.Exception;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Created by matto on 08/08/2017.
 */
@Slf4j
@Service
public class CieloOneClickPaymentService implements Charger{

    @Autowired
    private CieloTransactionClient cieloTransactionClient;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private CustomerRepository customerRepository;

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




/*

        Order order = chargeRequest.getBody().getOrder();

        String emailComprador = findPersistentUserEmail(order.getIdCustomer().getIdCustomer());

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

        */
        throw new UnsupportedOperationException("Nao faremos add card em passos separados. O cartao sera salvo na primeira compra.");
    }

    private String findPersistentUserEmail(Long idCustomer) {
        Customer persistentCustomer = this.customerRepository.findOne(idCustomer);

        return  persistentCustomer.getUser().getEmail();
    }

    @Override
    public ChargeResponse<Object> reserve(ChargeRequest<Payment> chargeRequest) {

        Payment receivedPayment = chargeRequest.getBody();

        Payment persistentPayment = paymentRepository.findOne(receivedPayment.getId());

        CreditCard creditCard = persistentPayment.getCreditCard();

        Order persistentOrder = persistentPayment.getOrder();

        Customer customer = persistentOrder.getIdCustomer();

        Address customerAddress = customer.getAddress();

        CieloCreditCard cieloCreditCard = CieloCreditCard.builder()
                .brand(creditCard.getVendor())
                .cardNumber(creditCard.getNumber())
                .expirationDate(creditCard.getExpirationDate())
                .saveCard(true)
                .securityCode(creditCard.getSecurityCode())
                .holder(creditCard.getOwnerName())
                .build();

        CieloAddress cieloAddress = CieloAddress.builder()
                .street(customerAddress.getAddress())
                .number(customerAddress.getNumber())
                .city(customerAddress.getCity())
                .complement(customerAddress.getComplement())
                .state(customerAddress.getState())
                .build();

        CieloCustomer cieloCustomer = CieloCustomer.builder()
                .name(customer.getNameCustomer())
                .email(customer.getUser().getEmail())
                .birthdate(new SimpleDateFormat("dd/MM/yyyy").format(customer.getBirthDate()))
                .identity(customer.getCpf())
                .address(cieloAddress)
                .build();

        RequestCieloPayment cieloPayment = RequestCieloPayment.builder()
                .capture(false)
                .amount(persistentPayment.getPriceRule().getPrice().intValue())
                .creditCard(cieloCreditCard)
                .installments(1)
                .type("CreditCard")
                .build();

        AuthorizeAndTokenRequest authorizeAndTokenRequest = AuthorizeAndTokenRequest.builder()
                .customer(cieloCustomer)
                .merchantOrderId(String.valueOf(persistentOrder.getIdOrder()))
                .payment(cieloPayment)
                .build();

        try {
            AuthorizeAndTokenResponse authorizeResponse = cieloTransactionClient.reserve(null, authorizeAndTokenRequest);

            return buildResponse(authorizeResponse);
        } catch (FeignException e) {

            org.apache.log4j.MDC.put("cieloHttpStatus", e.status());

            throw new OrderValidationException(CieloApiErrorCode"Falha na integracao de RESERVE com a Cielo", e);
        }
    }

    private ChargeResponse<Object> buildResponse(AuthorizeAndTokenResponse result) {

        Integer cieloPaymentStatus = result.getPayment().getStatus();

        Payment.Status paymentStatus = Payment.Status.fromSuperpayStatus(cieloPaymentStatus);

        org.apache.log4j.MDC.put("cieloPaymentStatus", paymentStatus.toString() + "(" +cieloPaymentStatus + ")");

        if (paymentStatus.isSuccess()) {

            if (Payment.Status.PAGO_E_CAPTURADO.equals(paymentStatus)) {
                log.warn("Pedido retornou como PAGO E CAPTURADO, mas o correto seria PAGO E 'NÃO' CAPTURADO.");
            }

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


           /* response.setResponseCode(Payment.Status.fromSuperpayStatus(
                    result.getStatusTransacao())
                    .getResponseCode()
            );*/
            response.setResponseCode(Payment.Status.PAGO_E_CAPTURADO.getResponseCode());

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
