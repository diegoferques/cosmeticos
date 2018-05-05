package com.cosmeticos.payment.cielo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CieloApiErrorResponseBody {

    @JsonProperty("Code")
    private Integer code;

    @JsonProperty("Message")
    private String message;
}
