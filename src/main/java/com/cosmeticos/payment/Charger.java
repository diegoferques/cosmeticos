package com.cosmeticos.payment;

public interface Charger<B, R> {

    ChargeResponse<R> addCard(ChargeRequest<B> chargeRequest);
    ChargeResponse<R> reserve(ChargeRequest<B> chargeRequest);
    ChargeResponse<R> capture(ChargeRequest<B> chargeRequest);
    ChargeResponse<R> getStatus(ChargeRequest<B> chargeRequest);
}
