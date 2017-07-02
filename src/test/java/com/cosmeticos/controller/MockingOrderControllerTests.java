package com.cosmeticos.controller;

/**
 * Created by matto on 28/06/2017.
 */

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.Sale;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ScheduleRepository;
import com.cosmeticos.service.OrderService;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingOrderControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CustomerRepository customerRepository;


    @Test
    public void testCreateError500() throws IOException {
        /**/
        Mockito.when(
                orderService.create(Mockito.anyObject())
        ).thenThrow(new RuntimeException());

        Sale s1 = new Sale();
        s1.setIdOrder(1L);
        s1.setStatus(Sale.Status.ABORTED.ordinal());

        OrderRequestBody or = new OrderRequestBody();
        or.setSale(s1);

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
    public void testUpdateError500() throws IOException {
        /**/
        Mockito.when(
                orderService.update(Mockito.anyObject())
        ).thenThrow(new RuntimeException());

        Sale s1 = new Sale();
        s1.setIdOrder(1L);
        s1.setStatus(Sale.Status.ABORTED.ordinal());

        OrderRequestBody or = new OrderRequestBody();
        or.setSale(s1);

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
