package com.cosmeticos.validation;

/**
 * Created by matto on 27/07/2017.
 */
public class OrderValidationException extends Exception {

    public OrderValidationException() {
    }

    public OrderValidationException(String message) {
        super(message);
    }

    public OrderValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderValidationException(Throwable cause) {
        super(cause);
    }

    public OrderValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
