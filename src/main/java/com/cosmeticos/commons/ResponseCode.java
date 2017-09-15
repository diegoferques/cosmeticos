package com.cosmeticos.commons;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Classe criada para retornar no request http para informar detalhes sobre a requisicao feita pelo cliente.
 * Como antes retornavamos apenas o http status, agora retornamos o http status e mais este enum,
 * que funcionara como um codigo de detalhamento.
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum ResponseCode {

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
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    GATEWAY_FAILURE(HttpStatus.BAD_GATEWAY),
    FORBIDEN_PAYMENT(HttpStatus.FORBIDDEN),
    USER_PASSWORD_RESET_EMAIL_FAIL(HttpStatus.BAD_GATEWAY),
    SUCCESS(HttpStatus.OK),
    UNFINISHED_PAYMENT(HttpStatus.BAD_REQUEST),
    CANCELED_PAYMENT(HttpStatus.GONE),
    REFUNDED_PAYMENT(HttpStatus.GONE),
    INVALID_OPERATION(HttpStatus.UNAUTHORIZED);

    private HttpStatus httpStatus;

    private ResponseCode(HttpStatus status) {
        this.httpStatus = status;
    }

    public HttpStatus getHttpStatus(){
        return httpStatus;
    }
}
