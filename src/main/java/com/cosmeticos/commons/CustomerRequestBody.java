package com.cosmeticos.commons;

import com.cosmeticos.model.Customer;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by matto on 27/05/2017.
 */
@Data
public class CustomerRequestBody {

    @Valid
    @NotNull(message = "Entidade customer esta nula")
    private Customer customer;
}
