package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created by Vinicius on 17/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CashOrderControllerTests {

    private Order orderRestultFrom_createOrderOk = null;
    private Order orderRestultFrom_testUpdateOk = null;


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testReady2ChargeToSemiClosed() throws URISyntaxException {
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-createOrderOk" + "-cliente");
        c1.getUser().setEmail(System.nanoTime()+ "-createOrderOk" + "-cliente@bol");
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime()+ "-createOrderOk" + "-professional");
        professional.getUser().setEmail(System.nanoTime()+ "-createOrderOk" + "-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Category category = categoryRepository.findByName("PEDICURE");
        category = categoryRepository.findWithSpecialties(category.getIdCategory());


        ProfessionalServices ps1 = new ProfessionalServices(professional, category);

        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        /************ FIM DAS PRE_CONDICOES **********************************/


        String json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"professionalServices\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+category.getIdCategory()+",\n" +
                "        \"name\" : \"MASSAGISTA\"\n" +
                "      },\n" +
                "      \"professional\" : {\n" +
                "        \"idProfessional\" : "+professional.getIdProfessional()+",\n" +
                "        \"nameProfessional\" : \"Fernanda Cavalcante\",\n" +
                "        \"genre\" : \"F\",\n" +
                "        \"birthDate\" : 688010400000,\n" +
                "        \"cellPhone\" : \"(21) 99887-7665\",\n" +
                "        \"dateRegister\" : 1499195092952,\n" +
                "        \"status\" : 0\n" +
                "      }\n" +
                "    },\n" +
                "    \"idLocation\" : null,\n" +
                "    \"idCustomer\" : {\n" +
                "      \"idCustomer\" : "+c1.getIdCustomer()+",\n" +
                "      \"nameCustomer\" : \"Fernanda Cavalcante\",\n" +
                "      \"cpf\" : \"816.810.695-68\",\n" +
                "      \"genre\" : \"F\",\n" +
                "      \"birthDate\" : 688010400000,\n" +
                "      \"cellPhone\" : \"(21) 99887-7665\",\n" +
                "      \"dateRegister\" : 1499195092952,\n" +
                "      \"status\" : 0,\n" +
                "      \"idLogin\" : {\n" +
                "        \"username\" : \"KILLER\",\n" +
                "        \"email\" : \"Killer@gmail.com\",\n" +
                "        \"personType\":\"FÍSICA\",\n" +
                "        \"sourceApp\" : \"facebook\"\n" +
                "      },\n" +
                "      \"idAddress\" : null\n" +
                "    }\n" +
                "  }\n" +
                "}";

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<OrderResponseBody> exchange = restTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals(Order.Status.OPEN, exchange.getBody().getOrderList().get(0).getStatus());
        Assert.assertEquals(Order.PayType.CASH, exchange.getBody().getOrderList().get(0).getPaymentType());
        Assert.assertNull(exchange.getBody().getOrderList().get(0).getScheduleId());


        Order orderOpenOk = exchange.getBody().getOrderList().get(0);

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderOpenOk.getIdOrder()+",\n" +
                "    \"status\" : "+ Order.Status.ACCEPTED.ordinal() +"\n" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders")) // put pra atualizar o que inserimos la em cima
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        OrderResponseBody responseBodyDoPut = exchangeUpdate.getBody();
        Order orderAtualizada = responseBodyDoPut.getOrderList().get(0);

        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertEquals(Order.Status.ACCEPTED, orderAtualizada.getStatus());

        Order createdOrder = exchangeUpdate.getBody().getOrderList().get(0);


        ResponseEntity<OrderResponseBody> exchangeUpdateInProgress = this.updateOrderStatus(
                createdOrder.getIdOrder(), Order.Status.INPROGRESS);

        Assert.assertNotNull(exchangeUpdateInProgress);
        Assert.assertNotNull(exchangeUpdateInProgress.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateInProgress.getStatusCode());
        Assert.assertEquals(Order.Status.INPROGRESS, exchangeUpdateInProgress.getBody().getOrderList().get(0).getStatus());

        Order updatedOrder = exchangeUpdateInProgress.getBody().getOrderList().get(0);


        ResponseEntity<OrderResponseBody> exchangeUpdaterReady2Charge = this.updateOrderStatus(
                updatedOrder.getIdOrder(), Order.Status.READY2CHARGE);

        Assert.assertNotNull(exchangeUpdaterReady2Charge);
        Assert.assertNotNull(exchangeUpdaterReady2Charge.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdaterReady2Charge.getStatusCode());
        Assert.assertEquals(Order.Status.READY2CHARGE, exchangeUpdaterReady2Charge.getBody().getOrderList().get(0).getStatus());

        Order readyOrder = exchangeUpdateInProgress.getBody().getOrderList().get(0);

        ResponseEntity<OrderResponseBody> exchangeUpdaterSemiClosed = this.updateOrderStatus(
                readyOrder.getIdOrder(), Order.Status.SEMI_CLOSED);

        Assert.assertNotNull(exchangeUpdaterSemiClosed);
        Assert.assertNotNull(exchangeUpdaterSemiClosed.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdaterSemiClosed.getStatusCode());
        Assert.assertEquals(Order.Status.SEMI_CLOSED, exchangeUpdaterSemiClosed.getBody().getOrderList().get(0).getStatus());
    }

    public ResponseEntity<OrderResponseBody> updateOrderStatus(Long orderId, Order.Status status) throws URISyntaxException {

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ orderId +",\n" +
                "    \"status\" : \""+ status +"\"\n" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders")) // put pra atualizar o que inserimos la em cima
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        return exchangeUpdate;
    }



}
