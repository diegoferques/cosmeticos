package com.cosmeticos.payment.cielo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CieloAddCardResponseBody
{
    @JsonProperty("CardToken")
    public String token;

    /**
     * Na documentacao este atributo nao eh informado como array, como nos outros objetos.
     */
    @JsonProperty("Links")
    public ResponseLink links = null;
}
