package com.cosmeticos.commons;

import com.cosmeticos.model.Order;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matto on 17/06/2017.
 */
@Data
public class OrderResponseBody {

    @JsonView(
            {
                ResponseJsonView.OrderControllerCreate.class,
                ResponseJsonView.OrderControllerUpdate.class,
                ResponseJsonView.OrderControllerFindBy.class
            })
    private String description;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    private List<Order> orderList = new ArrayList<>(10);

    public OrderResponseBody() {
    }

    public OrderResponseBody(Order order) {
        this.orderList.add(order);
    }
}
