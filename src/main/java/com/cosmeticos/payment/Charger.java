package com.cosmeticos.payment;

import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Payment;

public interface Charger {

    ChargeResponse<Object> addCard(CreditCard creditCard);
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
