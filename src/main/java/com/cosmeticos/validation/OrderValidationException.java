package com.cosmeticos.validation;

import lombok.Data;
import org.springframework.http.HttpStatus;

<<<<<<< HEAD
=======
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

>>>>>>> dev
/**
 * Created by matto on 27/07/2017.
 */
@Data
public class OrderValidationException extends Exception {

<<<<<<< HEAD
    private HttpStatus status;
=======
    public enum Type{
        INVALID_SCHEDULE_END(HttpStatus.BAD_REQUEST),
        INVALID_SCHEDULE_START(HttpStatus.BAD_REQUEST),
        DUPLICATE_RUNNING_ORDER(HttpStatus.CONFLICT),
        INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST);

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


>>>>>>> dev

    public OrderValidationException() {
        super();
    }

<<<<<<< HEAD
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
=======
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
>>>>>>> dev
    }
}
