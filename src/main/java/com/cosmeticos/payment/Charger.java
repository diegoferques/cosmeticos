package com.cosmeticos.payment;

import com.cosmeticos.model.Payment;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;

public interface Charger {

    ChargeResponse<Object> addCard(ChargeRequest<Payment> chargeRequest);
    ChargeResponse<Object> reserve(ChargeRequest<Payment> chargeRequest);
    ChargeResponse<Object> capture(ChargeRequest<Payment> chargeRequest);
    ChargeResponse<Object> getStatus(ChargeRequest<Payment> chargeRequest);

    /**
     * Responsavel pro atualizar o status do usuario com base na resposta du superpay. A principio, usado no processamento de callbacks.
     * @param payment
     * @return
     */
    Boolean updatePaymentStatus(Payment payment);
}