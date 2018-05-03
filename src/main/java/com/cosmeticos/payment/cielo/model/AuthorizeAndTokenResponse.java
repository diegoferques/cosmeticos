
package com.cosmeticos.payment.cielo.model;


import com.cosmeticos.model.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthorizeAndTokenResponse {

    private String merchantOrderId;
    private Customer customer;

    @JsonProperty("Payment")
    private ResponseCieloPayment payment;

}
