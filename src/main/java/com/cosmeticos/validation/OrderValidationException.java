package com.cosmeticos.validation;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.cosmeticos.commons.ResponseCode;

import lombok.Data;

/**
 * Created by matto on 27/07/2017.
 */
@Data
public class OrderValidationException extends RuntimeException {

    @Enumerated(EnumType.STRING)
    private ResponseCode code;

    public OrderValidationException() {
        super();
    }

    public OrderValidationException(ResponseCode code, String message) {
        super(message);
        this.code = code;
    }

    public OrderValidationException(ResponseCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public OrderValidationException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
