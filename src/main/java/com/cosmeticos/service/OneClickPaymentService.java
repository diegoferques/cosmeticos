package com.cosmeticos.service;

import com.cosmeticos.model.*;
import com.cosmeticos.payment.*;
import com.cosmeticos.payment.superpay.ws.oneclick.DadosCadastroPagamentoOneClickWS;
import com.cosmeticos.payment.superpay.ws.oneclick.ResultadoPagamentoWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Override
    public ChargeResponse<Object> addCard(ChargeRequest<Payment> chargeRequest) {

        Order order = chargeRequest.getBody().getOrder();

        String emailComprador = order.getIdCustomer().getUser().getEmail();
        Customer customer = order.getIdCustomer();

        User user = customer.getUser();

        CreditCard creditCard = user.getCreditCardCollection()
                .stream()
                .findFirst()
                .get();
        String nomeTitularCartaoCredito = creditCard.getOwnerName();
        String numeroCartaoCredito = creditCard.getCardNumber();
        String dataValidadeCartao = String.valueOf(creditCard.getExpirationDate());

        Payment payment = (Payment) order.getPaymentCollection();
        Long formaPagamento = Long.valueOf(payment.getType().ordinal());

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

        Order order = chargeRequest.getBody().getOrder();

        ProfessionalCategory professionalCategory = order.getProfessionalCategory();
        Category category = professionalCategory.getCategory();

        Customer customer = order.getIdCustomer();
        Address address = customer.getAddress();
        User user = customer.getUser();

        Payment payment = order.getPaymentCollection()
                .stream()
                .findFirst()
                .get();
        Long numeroTransacao = payment.getId();

        CreditCard creditCard = order.getCreditCardCollection()
                .stream()
                .findFirst()
                .get();
        String token = creditCard.getToken();
        String campainha = "http://ngrok/campainha/superpay";
        String urlRedirecionamentoNaoPago = "";
        String urlRedirecionamentoPago = "";
        long valor = payment.getValue();
        String bairroEnderecoComprador = address.getNeighborhood();
        String cepEnderecoComprador = address.getCep();
        String cidadeEnderecoCompra = address.getCity();
        String emailComprador = user.getEmail();
        String enderecoComprador = address.getAddress();
        String estadoEnderecoComprador = address.getState();
        String nomeComprador = customer.getNameCustomer();
        String numeroEnderecoComprador = address.getComplement();
        String paisComprar = address.getCountry();
        String sexoComprador = String.valueOf(customer.getGenre());
        String telefoneComprador = customer.getCellPhone();
        long tipoCliente = user.getPersonType().ordinal();
        String cvv = creditCard.getSecurityCode();
        String nomeCategoria = category.getOwnerCategory().getName();
        String nomeProduto = category.getName();
        Long valorUnitarioProduto = payment.getValue();
        //String origemTransacao;
        //String ip;
        //long valorDesconto;
        //int parcelas;
        //int idioma = 0;
        //String codigoCliente
        //String telefoneAdicionalComprador = customer.getCellPhone();
        //String codigoCategoria;
        //String codigoProduto;
        //int quantidadeProduto;
        //long codigoTipoTelefoneAdicionalComprador;
        //String dataNascimentoComprador;

        ResultadoPagamentoWS result = (oneClickClient.pay(
                numeroTransacao,
                token,
                campainha,
                urlRedirecionamentoNaoPago,
                urlRedirecionamentoPago,
                valor,
                bairroEnderecoComprador,
                cepEnderecoComprador,
                cidadeEnderecoCompra,
                emailComprador,
                enderecoComprador,
                estadoEnderecoComprador,
                nomeComprador,
                numeroEnderecoComprador,
                paisComprar,
                sexoComprador,
                telefoneComprador,
                tipoCliente,
                nomeCategoria,
                nomeProduto,
                valorUnitarioProduto,
                nomeComprador,
                estadoEnderecoComprador,
                numeroEnderecoComprador,
                sexoComprador,
                telefoneComprador,
                cvv, nomeProduto, cvv));

        return new ChargeResponse<Object>(result);

    }

    @Override
    public ChargeResponse<Object> capture(ChargeRequest<Payment> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard

        Order order = chargeRequest.getBody().getOrder();

        CreditCard creditCard = order.getCreditCardCollection()
                .stream()
                .findFirst()
                .get();

        Payment payment = order.getPaymentCollection()
                .stream()
                .findFirst()
                .get();

        String nomeTitularCarttaoCredito = creditCard.getOwnerName();
        String urlCampainha = "http://ngrok/campainha/superpay";
        Long valor = payment.getValue();
        String codigoSeguranca = creditCard.getSecurityCode();
        String ip = "09876";
        //String dataValidadeCartao = String.valueOf(creditCard.getExpirationDate());
        //String urlRedirecionamentoNaoPago = "urlRedirecionamentoNaoPago.com";
        //String urlRedirecionamentoPago = "urlRedirecionamentoPago.com";
        //long valorDesconto = 10L;
        //String origemTransacao = "ORIGEM";
        //int parcelas = 1;
        //int idioma = 1;
        //Long numeroTransacao;
        //int codigoFormaPagamento = 2;

        com.cosmeticos.payment.superpay.ws.completo.ResultadoPagamentoWS result = completoClient.capturePayment(
                codigoSeguranca,
                ip,
                nomeTitularCarttaoCredito,
                valor,
                urlCampainha);

        return new ChargeResponse<Object>(result);
    }

    @Override
    public ChargeResponse<Object> getStatus(ChargeRequest<Payment> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard
        Order order = chargeRequest.getBody().getOrder();

        CreditCard creditCard = order.getCreditCardCollection().stream().findFirst().get();
        Customer customer = order.getIdCustomer();
        User user = customer.getUser();

        Payment payment = (Payment) order.getPaymentCollection();
        Long formaPagamento = Long.valueOf(payment.getType().ordinal());

        String dataValidadeCartao = String.valueOf(creditCard.getExpirationDate());
        String emailComprador = user.getEmail();
        String nomeTitularCartaoCredito = creditCard.getOwnerName();
        String numeroCartaoCredito = creditCard.getCardNumber();
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
