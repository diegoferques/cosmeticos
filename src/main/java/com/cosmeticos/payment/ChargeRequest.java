package com.cosmeticos.payment;

import lombok.Data;

@Data
public class ChargeRequest<B> {

    private B body;
}
