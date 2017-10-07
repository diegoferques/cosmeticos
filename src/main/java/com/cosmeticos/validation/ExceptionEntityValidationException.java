package com.cosmeticos.validation;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.Exception;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by Vinicius on 06/10/2017.
 */
@Data
public class ExceptionEntityValidationException extends RuntimeException {

    @Enumerated(EnumType.STRING)
    private ResponseCode code;

    public ExceptionEntityValidationException(ResponseCode code, String message) {
        super(message);
        this.code = code;
    }

    public ExceptionEntityValidationException(ResponseCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ExceptionEntityValidationException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
