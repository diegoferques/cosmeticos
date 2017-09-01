package com.cosmeticos.commons;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum ErrorCode {

    INVALID_SCHEDULE_END(HttpStatus.BAD_REQUEST),
    INVALID_SCHEDULE_START(HttpStatus.BAD_REQUEST),
    DUPLICATE_RUNNING_ORDER(HttpStatus.CONFLICT),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST), 
    CONFLICTING_SCHEDULES(HttpStatus.CONFLICT),
    INVALID_PAYMENT_CONFIGURATION(HttpStatus.BAD_REQUEST),
    INVALID_PROFESSIONAL_CATEGORY(HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_TYPE(HttpStatus.BAD_REQUEST),
    GATEWAY_DUPLICATE_PAYMENT(HttpStatus.CONFLICT),
    ILLEGAL_ORDER_OWNER_CHANGE(HttpStatus.BAD_REQUEST),
    FORBIDEN_PAYMENT(HttpStatus.FORBIDDEN);

    private HttpStatus httpStatus;

    private ErrorCode(HttpStatus status) {
        this.httpStatus = status;
    }

    public HttpStatus getHttpStatus(){
        return httpStatus;
    }
}
