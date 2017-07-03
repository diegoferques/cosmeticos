package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ScheduleRepository;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;

/**
 * Created by diego.MindTek on 26/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    //TODO - FALTA FINALIZAR, PROVAVELMENTE SERÁ NECESSÁRIO ALTERAR A ENTIDADE
    @Test
    public void createScheduledOrderOk() {
        Customer c1 = customerRepository.findOne(1L);
        Professional p1 = professionalRepository.findOne(1L);
        ProfessionalServices ps1 = new ProfessionalServices(1L, 1L);

        //Schedule s1 = scheduleRepository.findOne(1L);
        Schedule s1 = new Schedule();
        s1.setScheduleDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 10, 14, 00, 0)));
        s1.setStatus(Schedule.Status.ACTIVE);

        Sale o1 = new Sale();
        o1.setStatus(Sale.Status.CREATED.ordinal());
        o1.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 10, 0)));
        o1.setIdCustomer(c1);
        //o1.setIdLocation();
        o1.setProfessionalServices(p1.getProfessionalServicesCollection().iterator().next());
        o1.setScheduleId(s1);
        orderRepository.save(o1);


        OrderRequestBody or = new OrderRequestBody();
        or.setSale(o1);

        final ResponseEntity<OrderResponseBody> exchange = //
                restTemplate.exchange( //
                        "/orders", //
                        HttpMethod.POST, //
                        new HttpEntity(or), // Body
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals((int)Sale.Status.CREATED.ordinal(), (int)exchange.getBody().getSaleList().get(0).getStatus());
        Assert.assertNotNull(exchange.getBody().getSaleList().get(0).getScheduleId());


    }

}
