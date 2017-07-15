package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.model.Order;
import com.cosmeticos.service.OrderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by Vinicius on 15/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void testUpdateStatus(){

        // TODO: insere umas orders com finished by professional e nao confiar no OrderPreLoad

        List<Order> preUpdateallOrders = orderService.findBy(new Order());
        int c = 0;

        for (Order o: preUpdateallOrders) {
            if(o.getStatus() == Order.Status.FINISHED_BY_PROFESSIONAL.ordinal()){
                c++;
            }
        }

        Assert.assertTrue(c > 0);

        orderService.updateStatus();

        // assert: nenhuma order do banco pode estar finished_by_professional
        List<Order> allOrders = orderService.findBy(new Order());

        for (Order o: allOrders) {
            Assert.assertNotEquals(Order.Status.FINISHED_BY_PROFESSIONAL.ordinal(), o.getStatus().intValue());
        }
    }
}
