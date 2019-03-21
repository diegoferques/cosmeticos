package com.cosmeticos.model;


import com.cosmeticos.service.order.OrderStatusHandler;
import org.springframework.context.ApplicationContext;

public enum OrderStatus {
    OPEN(Values.OPEN_NAME),
    SCHEDULED(Values.SCHEDULED_NAME),
    INPROGRESS(Values.INPROGRESS_NAME),
    ACCEPTED(Values.ACCEPTED_NAME),
    CANCELLED(Values.CANCELLED_NAME),

    /**
     * @deprecated Este status perdeu sentido. Eh um status a mais desnecessario no processo de compra.
     */
    EXECUTED(Values.EXECUTED_NAME),
    SEMI_CLOSED(Values.SEMI_CLOSED_NAME),
    AUTO_CLOSED(Values.AUTO_CLOSED_NAME),
    CLOSED(Values.CLOSED_NAME),
    EXPIRED(Values.EXPIRED_NAME),
    FAILED_ON_PAYMENT(Values.FAILED_ON_PAYMENT_NAME),
    READY2CHARGE(Values.READY2CHARGE_NAME);

    private static final Class<OrderStatusHandler> beanClass = OrderStatusHandler.class;

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getTranslationPropertyKey() {
        return "order.status." + this.name();
    }

    /**
     * Permite executarmos um bean do spring para cada enum definido nesta classe, tornando o codigo mais claro e
     * modularizado ao executar uma instrucao que depende unicamente de um status de Order especifico.
     * @param applicationContext
     * @param transientOrder
     * @param persistentOrder
     */
    public void handle(ApplicationContext applicationContext, Order transientOrder, Order persistentOrder) {
        OrderStatusHandler bean = applicationContext.getBean(this.getValue(), beanClass);
        bean.handle(transientOrder, persistentOrder);
    }

    public String getValue() {
        return value;
    }

    public interface Values
    {
        /*
        Constantes usadas para dar nomes a spring beans
         */
        String OPEN_NAME = "ORDER_STATUS_OPEN";
        String SCHEDULED_NAME = "ORDER_STATUS_SCHEDULED";
        String INPROGRESS_NAME = "ORDER_STATUS_INPROGRESS";
        String ACCEPTED_NAME = "ORDER_STATUS_ACCEPTED";
        String CANCELLED_NAME = "ORDER_STATUS_CANCELLED";
        String EXECUTED_NAME = "ORDER_STATUS_EXECUTED";
        String SEMI_CLOSED_NAME = "ORDER_STATUS_SEMI_CLOSED";
        String AUTO_CLOSED_NAME = "ORDER_STATUS_AUTO_CLOSED";
        String CLOSED_NAME = "ORDER_STATUS_CLOSED";
        String EXPIRED_NAME = "ORDER_STATUS_EXPIRED";
        String FAILED_ON_PAYMENT_NAME = "ORDER_STATUS_FAILED_ON_PAYMENT";
        String READY2CHARGE_NAME = "ORDER_STATUS_READY2CHARGE";
    }
}