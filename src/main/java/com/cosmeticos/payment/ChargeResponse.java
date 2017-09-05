package com.cosmeticos.payment;

import com.cosmeticos.commons.ErrorCode;
import lombok.Data;

@Data
public class ChargeResponse<B> {

    private ErrorCode responseCode;

    private B body;
}
