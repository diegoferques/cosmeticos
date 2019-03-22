package com.cosmeticos.service.order;

import com.cosmeticos.model.Order;
import com.cosmeticos.service.BalanceItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static com.cosmeticos.service.BalanceItemService.creditFromOrder;

/**
 * (Esta classe eh um bean definido em {@link OrderStatusServicesConfiguration})
 */
@AllArgsConstructor
@Slf4j
public class OrderStatusClosedService implements OrderStatusHandler {

    private final BalanceItemService balanceItemService;

    @Override
    public void handle(Order transientOrder, Order persistentOrder) {
        log.debug("Nada especial a executar para status = {}", transientOrder.getStatus());
    }

    @Override
    public void onAfterOrderPesistence(Order persistentOrder) {

        if (persistentOrder.isCreditCard()) {
            balanceItemService.create(creditFromOrder(persistentOrder));
        }

        // TODO: acumular pontos ao prof e ao cliente.
    }
}
