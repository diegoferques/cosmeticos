package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OneClickPaymentService;
import com.cosmeticos.service.OrderService;
import com.cosmeticos.service.MulticlickPaymentService;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by matto on 28/06/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingOrderControllerTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private OrderService orderService;

    @MockBean
    MulticlickPaymentService paymentService;

    @MockBean
    OneClickPaymentService oneClickPaymentService;

    @MockBean
    private PaymentController paymentController;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private CreditCardRepository creditcardRepository;

    @Autowired
    @Qualifier(value = "charger")
    private Charger charger;

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
        testRestTemplate.exchange( //
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
                testRestTemplate.exchange( //
                        "/orders", //
                        HttpMethod.PUT, //
                        new HttpEntity(or), // Body
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
    }


    @Test
    public void testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard() throws Exception {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        ////////////// SETUP     //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        ChargeResponse<Object> response = new ChargeResponse<>("tokenFake");
        response.setResponseCode(ResponseCode.SUCCESS);

        //comentei o lance aki de baixo pra testar uma parada q a stacktrace orientou a fazer. nesse caso agora tah vindo nullpointer..
        //Mockito.doReturn(response)
          //      .when(oneClickPaymentService.addCard(Mockito.anyObject())
        //);
        Mockito.when(
                charger.addCard(Mockito.anyObject())
        ).thenReturn(response);

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-cliente");
        c1.getUser().setEmail(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-cliente@bol");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-professional");
        professional.getUser().setEmail(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(priceRule);

        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        ////////////// TESTING   //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        String json = OrderJsonHelper.buildJsonCreateNonScheduledOrder(
                c1,
                ps1,
                Payment.Type.CC,
                priceRule
        );


        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<OrderResponseBody> exchange = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals(Order.Status.OPEN, exchange.getBody().getOrderList().get(0).getStatus());
        Assert.assertNull(exchange.getBody().getOrderList().get(0).getScheduleId());

        List<CreditCard> cards = creditcardRepository.findByUserEmail(professional.getUser().getEmail());
        Optional<CreditCard> ccOptional = cards.stream().findFirst();

        Assert.assertTrue(ccOptional.isPresent());
        Assert.assertEquals(
                "Token gravado esta errado: " + ccOptional.get().getToken(),
                "tokenFake",
                ccOptional.get().getToken());

    }
}
