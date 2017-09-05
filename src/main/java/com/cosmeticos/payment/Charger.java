package com.cosmeticos.payment;

public interface Charger {

    ChargeResponse addCard(ChargeRequest chargeRequest);
    ChargeResponse reserve(ChargeRequest chargeRequest);
    ChargeResponse pay(ChargeRequest chargeRequest);
    ChargeResponse getStatus(ChargeRequest chargeRequest);
    ChargeResponse capture(ChargeRequest chargeRequest);
}
