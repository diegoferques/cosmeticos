package com.cosmeticos.service.order;

import com.cosmeticos.model.OrderStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Apenas para definirmos um bean com multiplos nomes. Cada nome representa um enum em {@link OrderStatus}.
 * Alguns desses enuns nao precisam executar instrucoes, por isso, damos a esses enuns a responsabilidade de
 * carregar um bean que nao faz nada ({@link OrderStatusNullService})
 */
@Configuration
public class OrderStatusNullServicesConfiguration {

    @Bean(name = {
            OrderStatus.Values.INPROGRESS_NAME,
            OrderStatus.Values.CLOSED_NAME,
            OrderStatus.Values.EXPIRED_NAME,
            OrderStatus.Values.EXECUTED_NAME,
            OrderStatus.Values.CANCELLED_NAME,
    })
    public OrderStatusNullService orderStatusNullService() {
        return new OrderStatusNullService();
    }
}
