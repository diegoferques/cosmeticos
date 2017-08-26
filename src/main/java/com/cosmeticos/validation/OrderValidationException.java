package com.cosmeticos.validation;

import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by matto on 27/07/2017.
 */
@Data
public class OrderValidationException extends Exception {

    public enum Type{
        INVALID_SCHEDULE_END(HttpStatus.BAD_REQUEST), INVALID_SCHEDULE_START(HttpStatus.BAD_REQUEST), DUPLICATE_RUNNING_ORDER(HttpStatus.CONFLICT);

        private HttpStatus status;

        private Type(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus getStatus(){
            return status;
        }
    }

    @Enumerated(EnumType.STRING)
    private Type type;



    public OrderValidationException() {
        super();
    }

    public OrderValidationException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public OrderValidationException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public OrderValidationException(Type type, Throwable cause) {
        super(cause);
        this.type = type;
    }
}
