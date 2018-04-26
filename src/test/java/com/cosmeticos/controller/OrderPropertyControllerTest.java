package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OrderService;
import com.cosmeticos.service.VoteService;
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

import static java.time.LocalDateTime.now;

/**
 * Created by Vinicius on 18/04/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderPropertyControllerTest {

    private Order orderRestultFrom_createScheduledOrderOk = null;
    private Order orderRestultFrom_createOrderOk = null;
    private Order orderRestultFrom_testUpdateOk = null;
    private Order orderRestultFrom_updateOrderOkToScheduled = null;
    private Order orderRestultFrom_updateScheduledOrderOkToScheduled = null;
    private Order orderRestultFrom_updateScheduledOrderToInactive = null;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private OrderService service;

    @Autowired
    private VoteService voteService;

    @Test
    public void createScheduledOrderOk() throws URISyntaxException {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        Professional professional = ProfessionalControllerTests.createFakeProfessional();

        customerRepository.save(c1);
        professionalRepository.save(professional);


        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(priceRule);

        professionalCategoryRepository.save(ps1);

        /*
         O teste comeca aqui:
         Fazemos um json com informacoes que batem com o que foi inserido acima. Um usuario que existe no banco e
         um profissional associado a um servico que existirao no banco.
          */
        String json = OrderJsonHelper.buildJsonCreateScheduledOrder(
                c1,
                ps1,
                priceRule,
                Payment.Type.CASH,
                Timestamp.valueOf(now().plusHours(6)).getTime() // Agendado pra daqui ha 6 horas.
        );


        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<OrderResponseBody> exchange = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Order newOrder = exchange.getBody().getOrderList().get(0);


        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+newOrder.getIdOrder()+",\n" +
                "    \"status\" : \"CANCELLED\",\n" +
                "    \"orderPropertyCollection\" : \n" +
                "    [ \n" +
                "        { \n" +
                "            \"name\": \"teste\",\n"+
                "            \"value\":\"teste\"\n"+
                "        }\n" +
                "    ]\n"+
                "  }\n" +
                "}";

        RequestEntity<String> entityPut =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangePut = testRestTemplate
                .exchange(entityPut, OrderResponseBody.class);

        Assert.assertNotNull(exchangePut);
        Assert.assertEquals(HttpStatus.OK, exchangePut.getStatusCode());

        Assert.assertEquals(Order.Status.CANCELLED,
                exchangePut.getBody().getOrderList().get(0).getStatus());
    }
}
