package com.cosmeticos.service.order;

import com.cosmeticos.model.Order;

public interface OrderStatusHandler {
    void handle(Order transientOrder, Order persistentOrder);

    /**
     * Executado por {@link OrderService} apos todos os processamentos da Order e sua persistencia.
     * @param persistentOrder
     */
    void onAfterOrderPesistence(Order persistentOrder);
}
