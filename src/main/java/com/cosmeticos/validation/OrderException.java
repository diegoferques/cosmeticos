package com.cosmeticos.validation;

import com.cosmeticos.payment.cielo.model.AuthorizeAndTokenResponse;
import com.cosmeticos.payment.cielo.model.CieloApiErrorCode;
import com.cosmeticos.payment.cielo.model.CieloApiErrorResponseBody;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class OrderException extends RuntimeException {

    @Enumerated(EnumType.STRING)
    private CieloApiErrorCode code;

    private AuthorizeAndTokenResponse authorizeAndTokenResponse;

    public OrderException() {
        super();
    }

    public OrderException(CieloApiErrorCode code, CieloApiErrorResponseBody authorizeAndTokenResponse, String message) {
        super(message);
        this.code = code;
    }

    public OrderException(CieloApiErrorCode code, CieloApiErrorResponseBody authorizeAndTokenResponse, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public OrderException(CieloApiErrorCode code, CieloApiErrorResponseBody authorizeAndTokenResponse, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
