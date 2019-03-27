package com.cosmeticos.service.order;

import com.cosmeticos.model.Order;
import com.cosmeticos.service.BalanceItemService;
import com.cosmeticos.service.PointService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import static com.cosmeticos.service.BalanceItemService.creditFromOrder;

/**
 * (Esta classe eh um bean definido em {@link OrderStatusServicesConfiguration})
 */
@AllArgsConstructor
@Slf4j
public class OrderStatusClosedService implements OrderStatusHandler {

    private final BalanceItemService balanceItemService;
    private final PointService pointService;

    @Override
    public void handle(Order transientOrder, Order persistentOrder) {
        log.debug("Nada especial a executar para status = {}", transientOrder.getStatus());
    }

    @Override
    public void onAfterOrderPesistence(Order persistentOrder) {

        // Creditos para resgate em R$ sao dados em vendas por cartao de credito, pois como essas vendas são feitas em nome da ipretty, o profissional precisa solicitar ao ipretty qye lhe devolva o R$.
        if (persistentOrder.isCreditCard()) {
            balanceItemService.create(creditFromOrder(persistentOrder));
        }

        // Incremento de pontos acontece assincronamente, independente da venda ter sido por cartao ou dinheiro.
        new SimpleAsyncTaskExecutor().execute(() -> pointService.increase(persistentOrder));
    }
}
