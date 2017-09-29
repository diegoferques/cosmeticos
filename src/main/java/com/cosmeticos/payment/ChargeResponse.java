package com.cosmeticos.payment;

import com.cosmeticos.commons.ResponseCode;
import lombok.Data;

@Data
public class ChargeResponse<B> {

    private ResponseCode responseCode;

    private B body;

    public ChargeResponse(B body) {
        this.body = body;
    }

}
