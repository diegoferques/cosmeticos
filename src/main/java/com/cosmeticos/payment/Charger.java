package com.cosmeticos.payment;

import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;

public interface Charger<B, R> {

    ChargeResponse<R> addCard(ChargeRequest<B> chargeRequest);
    ChargeResponse<R> reserve(ChargeRequest<B> chargeRequest);
    ChargeResponse<R> capture(ChargeRequest<B> chargeRequest);
    ChargeResponse<R> getStatus(ChargeRequest<B> chargeRequest);

    /**
     * Responsavel pro atualizar o status do usuario com base na resposta du superpay. A principio, usado no processamento de callbacks.
     * @param retornoTransacao
     * @return
     */
    Boolean updatePaymentStatus(RetornoTransacao retornoTransacao);
}
