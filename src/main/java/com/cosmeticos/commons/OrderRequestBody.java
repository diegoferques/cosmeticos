package com.cosmeticos.commons;

import com.cosmeticos.model.Order;
import lombok.Data;

/**
 * Created by matto on 22/06/2017.
 */
@Data
public class OrderRequestBody {

    private Order order;

    //ISSO JA ESTAVA AQUI, SERA QUE FOI ALGUEM QUE FEZ PENSANDO EM USAR COMO VOTE?
    private Integer value;

    private Integer vote;
}
