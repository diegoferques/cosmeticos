package com.cosmeticos.service;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.payment.cielo.CieloTransactionClient;
import com.cosmeticos.payment.cielo.model.*;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.PaymentRepository;
import com.cosmeticos.validation.OrderValidationException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

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
    private CustomerRepository customerRepository;

    @Override
    public ChargeResponse<Object> addCard(ChargeRequest<Payment> chargeRequest) {

        Payment payment = chargeRequest.getBody();

        CreditCard creditCard = payment.getCreditCard();

        String nomeTitularCartaoCredito = creditCard.getOwnerName();
        String numeroCartaoCredito = creditCard.getNumber();
        String dataValidadeCartao = creditCard.getExpirationDate();
        String brand = creditCard.getVendor();

        CieloAddCardRequestBody addCardRequestBody = CieloAddCardRequestBody.builder()
            .cardNumber(numeroCartaoCredito)
            .brand(brand)
            .customerName(nomeTitularCartaoCredito)
            .expirationDate(dataValidadeCartao)
            .build();

        CieloAddCardResponseBody cieloAddCardResponseBody = cieloTransactionClient.addCard(addCardRequestBody);

        ChargeResponse<Object> chargeResponse = new ChargeResponse<>(cieloAddCardResponseBody.getToken());

        return chargeResponse;

    }

    private String findPersistentUserEmail(Long idCustomer) {
        Customer persistentCustomer = this.customerRepository.findOne(idCustomer);

        return  persistentCustomer.getUser().getEmail();
    }

    @Override
    public ChargeResponse<Object> reserve(ChargeRequest<Payment> chargeRequest) {

        Payment receivedPayment = chargeRequest.getBody();

        Payment persistentPayment = paymentRepository.findOne(receivedPayment.getId());

        CreditCard creditCard = persistentPayment
                .getOrder()
                .getIdCustomer()
                .getUser()
                .getCreditCardCollection()
                .stream()
                .findFirst()
                .get();

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

            throw new OrderValidationException(ResponseCode.GATEWAY_FAILURE, "Falha na integracao de RESERVE com a Cielo", e);
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
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public ChargeResponse<Object> getStatus(ChargeRequest<Payment> chargeRequest) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    // TODO avaliar se as classes concretas realmente devem implementar isso pra expor ou se isso fica de reposabilidade de cada classe concreta.
    @Override
    public Boolean updatePaymentStatus(Payment payment) {
        return true;
    }

}
