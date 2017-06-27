package com.cosmeticos.commons;

import com.cosmeticos.model.Sale;
import com.cosmeticos.model.Sale;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matto on 17/06/2017.
 */
@Data
public class OrderResponseBody {
    private String description;

    private List<Sale> saleList = new ArrayList<>(10);

    public OrderResponseBody() {
    }

    public OrderResponseBody(Sale sale) {
        this.saleList.add(sale);
    }
}
