package com.cosmeticos.validation;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Created by matto on 27/07/2017.
 */
@Data
public class OrderValidationException extends Exception {

    private HttpStatus status;

    public OrderValidationException() {
    }

    public OrderValidationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public OrderValidationException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public OrderValidationException(HttpStatus status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    public OrderValidationException(HttpStatus status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }
}
