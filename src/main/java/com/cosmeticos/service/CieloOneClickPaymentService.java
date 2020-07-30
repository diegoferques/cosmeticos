package com.cosmeticos.service;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Order;
import com.cosmeticos.model.Payment;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.payment.cielo.CieloTransactionClient;
import com.cosmeticos.payment.cielo.model.*;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.PaymentRepository;
import com.cosmeticos.validation.OrderValidationException;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.time.LocalDate.now;

/**
 * Created by matto on 08/08/2017.
 */
@AllArgsConstructor
@Slf4j
@Service
public class CieloOneClickPaymentService implements Charger{

    private enum Vendor
    {
        VISA("VISA"), MASTERCARD("MASTER"), AMEX("AMEX"), ELO("ELO"), AURA("AURA"), JCB("JCB"), DINERS("DINERS"), DISCOVER("DISCOVER"), HIPERCARD("HIPERCARD");

        private Vendor(String cieloSpell) {
            this.cieloSpell = cieloSpell;
        }

        private String cieloSpell;

        public String getCieloSpell() {
            return cieloSpell;
        }
    }

    private CieloTransactionClient cieloTransactionClient;

    private PaymentRepository paymentRepository;

    private CustomerRepository customerRepository;

    @Override
    public ChargeResponse<Object> addCard(CreditCard creditCard) {

        String nomeTitularCartaoCredito = creditCard.getOwnerName();
        String numeroCartaoCredito = creditCard.getNumber();
        String dataValidadeCartao = handleExpirationDate(creditCard.getExpirationDate());
        String brand = Vendor.valueOf(creditCard.getVendor().toUpperCase()).getCieloSpell();

        CieloAddCardRequestBody addCardRequestBody = CieloAddCardRequestBody.builder()
                .cardNumber(numeroCartaoCredito)
                .brand(brand)
                .customerName(nomeTitularCartaoCredito)
                .holder(nomeTitularCartaoCredito)
                .expirationDate(dataValidadeCartao)
                .build();

        CieloAddCardResponseBody cieloAddCardResponseBody = cieloTransactionClient.addCard(addCardRequestBody);

        ChargeResponse<Object> chargeResponse = new ChargeResponse<>(cieloAddCardResponseBody.getToken());

        return chargeResponse;

    }

    /**
     * Retorna formato MM/yyyy caso o formato recebido seja MM/yy
     * @param expirationDate
     * @return
     */
    private String handleExpirationDate(String expirationDate) {
        String separator = "/";
        String[] date = expirationDate.split(separator);
        String year = date[1];
        if(year.length() == 2)
        {
            year = String.valueOf(now().getYear()).substring(0, 2) + year;
        }
        return String.format("%s%s%s", date[0], separator, year);
    }

    private String findPersistentUserEmail(Long idCustomer) {
        Customer persistentCustomer = this.customerRepository.findOne(idCustomer);

        return  persistentCustomer.getUser().getEmail();
    }

    @Override
    public ChargeResponse<Object> reserve(ChargeRequest<Payment> chargeRequest) {

        Payment receivedPayment = chargeRequest.getBody();

        Payment persistentPayment = paymentRepository.findOne(receivedPayment.getId());

        // TODO: eh necessario criar mais testes que garantam funcionamento de vendas com cartaoo.
        CreditCard creditCard = persistentPayment
                .getOrder()
                .getIdCustomer()
                .getUser()
                .getCreditCardCollection()
                .stream()
                .findFirst()
                .get();

        Order persistentOrder = persistentPayment.getOrder();

        CieloCreditCard cieloCreditCard = CieloCreditCard.builder()
                .cardToken(creditCard.getToken())
                .securityCode(creditCard.getSecurityCode())
                .build();

        RequestCieloPayment cieloPayment = RequestCieloPayment.builder()
                .capture(false)
                .amount(persistentPayment.getPriceRule().getPrice().intValue())
                .creditCard(cieloCreditCard)
                .installments(1)
                .type("CreditCard")
                .build();

        // Este objeto nao precisa enviar tudo, so o payment e o orderid. So seria necessario se quisessemos gravar o cartao.
        AuthorizeAndTokenRequest authorizeAndTokenRequest = AuthorizeAndTokenRequest.builder()
                .merchantOrderId(String.valueOf(persistentOrder.getIdOrder()))
                .payment(cieloPayment)
                .build();

        try {
            AuthorizeAndTokenResponse authorizeResponse = cieloTransactionClient.reserve(null, authorizeAndTokenRequest);

            // Nao eh boa ideia essa alteracao por referencia.
            receivedPayment.setStatus(Payment.Status.fromSuperpayStatus(authorizeResponse.getPayment().getStatus()));

            return buildResponse(authorizeResponse);
        } catch (FeignException e) {

            org.apache.log4j.MDC.put("cieloHttpStatus", e.status());

            throw new OrderValidationException(ResponseCode.GATEWAY_FAILURE, "Falha na integracao de RESERVE com a Cielo", e);
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

    // TODO avaliar se as classes concretas realmente devem implementar isso pra expor ou se isso fica de reposabilidade de cada classe concreta.
    @Override
    public Boolean updatePaymentStatus(Payment payment) {
        return true;
    }

}
