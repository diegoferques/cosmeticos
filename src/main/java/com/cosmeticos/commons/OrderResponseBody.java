package com.cosmeticos.commons;

import java.util.ArrayList;
import java.util.List;

import com.cosmeticos.model.Order;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

/**
 * Created by matto on 17/06/2017.
 */
@JsonInclude(Include.NON_NULL)
@Data
public class OrderResponseBody {

    @JsonView(
            {
                ResponseJsonView.OrderControllerCreate.class,
                ResponseJsonView.OrderControllerUpdate.class,
                ResponseJsonView.OrderControllerFindBy.class
            })
	@JsonEnumDefaultValue	
	private ErrorCode errorCode;
	
    @JsonView(
            {
                ResponseJsonView.OrderControllerCreate.class,
                ResponseJsonView.OrderControllerUpdate.class,
                ResponseJsonView.OrderControllerFindBy.class
            })
    private String description;

    @JsonView(
            {
                ResponseJsonView.OrderControllerCreate.class,
                ResponseJsonView.OrderControllerUpdate.class,
                ResponseJsonView.OrderControllerFindBy.class
            })
    private String userFriendlyMessage;
    
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
