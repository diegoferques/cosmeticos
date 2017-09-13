package com.cosmeticos.service;

import com.cosmeticos.model.*;
import com.cosmeticos.payment.*;
import com.cosmeticos.payment.superpay.client.rest.model.*;
import com.cosmeticos.payment.superpay.ws.oneclick.DadosCadastroPagamentoOneClickWS;
import com.cosmeticos.payment.superpay.ws.oneclick.ResultadoPagamentoWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

/**
 * Created by matto on 08/08/2017.
 */
@Slf4j
@Service
public class OneClickPaymentService implements Charger<Order> {

    @Autowired
    private SuperpayOneClickClient oneClickClient;

    @Autowired
    private SuperpayCompletoClient completoClient;


    @Override
    public ChargeResponse<Object> addCard(ChargeRequest<Order> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard

        Order order = chargeRequest.getBody();

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
    public ChargeResponse<Object> reserve(ChargeRequest<Order> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard

        Order order = chargeRequest.getBody();

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
    public ChargeResponse<Object> capture(ChargeRequest<Order> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard

        Order order = chargeRequest.getBody();

        CreditCard creditCard = order.getCreditCardCollection()
                .stream()
                .findFirst()
                .get();

        Payment payment = order.getPaymentCollection()
                .stream()
                .findFirst()
                .get();

        //Long numeroTransacao;
        //int codigoFormaPagamento = 2;
        String codigoSeguranca = creditCard.getSecurityCode();
        String dataValidadeCartao = String.valueOf(creditCard.getExpirationDate());
        //int idioma = 1;
        //String ip = "09876";
        String nomeTitularCarttaoCredito = creditCard.getOwnerName();
        //String origemTransacao = "ORIGEM";
        //int parcelas = 1;
        String urlCampainha = "http://ngrok/campainha/superpay";
        String urlRedirecionamentoNaoPago = "urlRedirecionamentoNaoPago.com";
        String urlRedirecionamentoPago = "urlRedirecionamentoPago.com";
        Long valor = payment.getValue();
        //long valorDesconto = 10L;

        com.cosmeticos.payment.superpay.ws.completo.ResultadoPagamentoWS result = completoClient.capturePayment(
                dataValidadeCartao,
                nomeTitularCarttaoCredito,
                urlCampainha,
                valor, urlCampainha);

        return new ChargeResponse<Object>(result);
    }

    @Override
    public ChargeResponse<Object> getStatus(ChargeRequest<Order> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard
        Order order = chargeRequest.getBody();

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

    @Override
    public Boolean updatePaymentStatus(Object data) {
        return true;
    }

}
