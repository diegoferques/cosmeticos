package com.cosmeticos.validation;

import com.cosmeticos.commons.ResponseCode;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by matto on 27/07/2017.
 */
@Data
public class UserValidationException extends RuntimeException {

    @Enumerated(EnumType.STRING)
    private ResponseCode code;

    public UserValidationException() {
        super();
    }

    public UserValidationException(ResponseCode code, String message) {
        super(message);
        this.code = code;
    }

    public UserValidationException(ResponseCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public UserValidationException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
