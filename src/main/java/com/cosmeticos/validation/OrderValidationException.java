package com.cosmeticos.validation;

import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by matto on 27/07/2017.
 */
@Data
public class OrderValidationException extends RuntimeException {

    public enum Type{
        INVALID_SCHEDULE_END(HttpStatus.BAD_REQUEST),
        INVALID_SCHEDULE_START(HttpStatus.BAD_REQUEST),
        DUPLICATE_RUNNING_ORDER(HttpStatus.CONFLICT),
        INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST), 
        CONFLICTING_SCHEDULES(HttpStatus.CONFLICT),
        INVALID_PAYMENT_CONFIGURATION(HttpStatus.BAD_REQUEST),
        INVALID_PROFESSIONAL_CATEGORY_PAIR(HttpStatus.BAD_REQUEST),
        INVALID_PAYMENT_TYPE(HttpStatus.BAD_REQUEST),
        GATEWAY_DUPLICATE_PAYMENT(HttpStatus.CONFLICT), FORBIDEN_PAYMENT(HttpStatus.FORBIDDEN);

        private HttpStatus httpStatus;

        private Type(HttpStatus status) {
            this.httpStatus = status;
        }

        public HttpStatus getHttpStatus(){
            return httpStatus;
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
