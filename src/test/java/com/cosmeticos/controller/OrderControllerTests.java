package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.Sale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by diego.MindTek on 26/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    //TESTANDO O RETORNO DE ORDER PELO ID
    @Test
    public void testFindById() throws ParseException {

        final ResponseEntity<OrderResponseBody> exchange = //
                restTemplate.exchange( //
                        "/orders/1", //
                        HttpMethod.GET, //
                        null,
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

    }

    //TESTANDO O RETORNO DA LISTA DOS ULTIMOS ORDERS EFETUADOS
    @Test
    public void testLastest10OK() throws ParseException {

        final ResponseEntity<OrderResponseBody> exchange = //
                restTemplate.exchange( //
                        "/orders", //
                        HttpMethod.GET, //
                        null,
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

    }

    //TESTANDO O DELETE DE ORDER QUE NAO EH PERMITIDO
    @Test
    public void testDeleteForbiden() throws ParseException {

        final ResponseEntity<OrderResponseBody> exchange = //
                restTemplate.exchange( //
                        "/orders/1", //
                        HttpMethod.DELETE, //
                        null,
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

    }

    @Test
    public void testUpdateOK() throws IOException {

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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals((int)Sale.Status.ABORTED.ordinal(), (int)exchange.getBody().getSaleList().get(0).getStatus());
    }


}
