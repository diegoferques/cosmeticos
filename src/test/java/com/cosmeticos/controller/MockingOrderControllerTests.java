package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.Order;
import com.cosmeticos.service.OrderService;
import com.cosmeticos.service.PaymentService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Created by matto on 28/06/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingOrderControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private OrderService orderService;

    @MockBean
    PaymentService paymentService;

    @MockBean
    private PaymentController paymentController;

    @Test
    public void testCreateError500() throws IOException, OrderService.ValidationException {
        /**/
        Mockito.when(
                orderService.create(Mockito.anyObject())
        ).thenThrow(new RuntimeException());

        Order s1 = new Order();
        s1.setIdOrder(1L);
        s1.setStatus(Order.Status.CANCELLED);

        OrderRequestBody or = new OrderRequestBody();
        or.setOrder(s1);

        final ResponseEntity<OrderResponseBody> exchange = //
        restTemplate.exchange( //
                "/orders", //
                HttpMethod.POST, //
                new HttpEntity(or), // Body
                OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
    }

    @Test
    public void testUpdateError500() throws Exception {
        /**/
        Mockito.when(
                orderService.update(Mockito.anyObject())
        ).thenThrow(new RuntimeException());

        Order s1 = new Order();
        s1.setIdOrder(1L);
        s1.setStatus(Order.Status.CANCELLED);

        OrderRequestBody or = new OrderRequestBody();
        or.setOrder(s1);

        final ResponseEntity<OrderResponseBody> exchange = //
                restTemplate.exchange( //
                        "/orders", //
                        HttpMethod.PUT, //
                        new HttpEntity(or), // Body
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
    }
}
