package com.cosmeticos.payment.cielo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Builder
@Data
public class CieloAddCardRequestBody {

    @NotEmpty
    @JsonProperty("CustomerName")
    private String customerName;

    @NotEmpty
    @JsonProperty("CardNumber")
    private String cardNumber;

    @NotEmpty
    @JsonProperty("ExpirationDate")
    private String expirationDate;

    @NotEmpty
    @JsonProperty("Brand")
    private String brand;

    @NotEmpty
    @JsonProperty("Holder")
    private String holder;
}
