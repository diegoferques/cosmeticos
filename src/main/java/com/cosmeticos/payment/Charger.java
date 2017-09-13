package com.cosmeticos.payment;

import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;

public interface Charger<REQUEST> {

    ChargeResponse<Object> addCard(ChargeRequest<REQUEST> chargeRequest);
    ChargeResponse<Object> reserve(ChargeRequest<REQUEST> chargeRequest);
    ChargeResponse<Object> capture(ChargeRequest<REQUEST> chargeRequest);
    ChargeResponse<Object> getStatus(ChargeRequest<REQUEST> chargeRequest);

    /**
     * Responsavel pro atualizar o status do usuario com base na resposta du superpay. A principio, usado no processamento de callbacks.
     * @param retornoTransacao
     * @return
     */
    Boolean updatePaymentStatus(Object retornoTransacao);
}
