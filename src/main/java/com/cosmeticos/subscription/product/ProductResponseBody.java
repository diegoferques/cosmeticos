package com.cosmeticos.subscription.product;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 17/07/2017.
 */
@Data
public class ProductResponseBody {

    private String description;

    private List<Product> productList = new ArrayList<>(10);
}
