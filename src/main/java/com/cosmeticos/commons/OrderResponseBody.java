package com.cosmeticos.commons;

import com.cosmeticos.model.Order;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matto on 17/06/2017.
 */
@Data
public class OrderResponseBody {
    private String description;

    private List<Order> orderList = new ArrayList<>(10);

    public OrderResponseBody() {
    }

    public OrderResponseBody(Order order) {
        this.orderList.add(order);
    }
}
