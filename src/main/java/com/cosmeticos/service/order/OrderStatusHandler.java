package com.cosmeticos.service.order;

import com.cosmeticos.model.Order;

public interface OrderStatusHandler {
    void handle(Order transientOrder, Order persistentOrder);
}
