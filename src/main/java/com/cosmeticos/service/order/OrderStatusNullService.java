package com.cosmeticos.service.order;

import com.cosmeticos.model.Order;
import lombok.extern.slf4j.Slf4j;

/**
 * Aplicado a alguns status que nao devem executar quaisquer instrucoes.
 * (Esta classe eh um bean definido em {@link OrderStatusNullServicesConfiguration})
 */
@Slf4j
public class OrderStatusNullService implements OrderStatusHandler {

    @Override
    public void handle(Order transientOrder, Order persistentOrder) {
        log.debug("Nada especial a executar para status = {}", transientOrder.getStatus());
    }
}
