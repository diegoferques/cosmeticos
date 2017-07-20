package com.cosmeticos.subscription.product;

import lombok.Data;

import javax.validation.Valid;

/**
 * Created by Vinicius on 17/07/2017.
 */
@Data
public class ProductRequestBody {

    @Valid
    private Product entity;
}
