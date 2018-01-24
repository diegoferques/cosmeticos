package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.MulticlickPaymentService;
import com.cosmeticos.service.OneClickPaymentService;
import com.cosmeticos.service.OrderService;
import org.junit.Assert;
import org.junit.Before;
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
import java.lang.Exception;
import java.net.URI;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 28/06/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingOrderControllerAddCardTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    OneClickPaymentService oneClickPaymentService;

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

    @MockBean
    @Qualifier(value = "charger")
    private Charger charger;

    private Customer c1;
    private Professional professional;
    private ProfessionalCategory ps1;
    private PriceRule priceRule;

    @Before
    public void setup() throws ParseException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        ////////////// SETUP     //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////

        ChargeResponse<Object> response = new ChargeResponse<>("tokenFake");
        response.setResponseCode(ResponseCode.SUCCESS);

        Mockito.when(
                charger.addCard(Mockito.anyObject())
        ).thenReturn(response);


        c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-cliente");
        c1.getUser().setEmail(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-cliente@bol");

        professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-professional");
        professional.getUser().setEmail(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-professional@bol");


        customerRepository.save(c1);
        professionalRepository.save(professional);

        priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

         ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(priceRule);

        professionalCategoryRepository.save(ps1);
    }

    @Test
    public void testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard() throws Exception {



        ///////////////////////////////////////////////////////////////////////////////////////////////
        ////////////// TESTING   //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////

        String json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : "+ Timestamp.valueOf(LocalDateTime.now()).getTime()+",\n" +
                "    \"status\" : 0,\n" +
                "    \"attendanceType\" : \"ON_SITE\",\n" +

                "    \"professionalCategory\" : {\n" +
                "      \"professionalCategoryId\": " +ps1.getProfessionalCategoryId()+ "\n" +
                "    },\n" +

                "    \"idCustomer\" : {\n" +
                "      \"idCustomer\" : "+ c1.getIdCustomer() +"\n" +
                "    },\n" +

                "    \"paymentCollection\" : \n" +
                "    [\n" +
                "       {\n" +
                "         \"type\": \""+Payment.Type.CC.toString()+"\",\n" +
                "         \"parcelas\": 1,\n" +
                "         \"priceRule\": {\n" +
                "             \"id\": " + priceRule.getId() + "\n" +
                "         },\n" +

                "         \"creditCard\": {\n" +
                "            \"oneClick\": true"+
                "         }\n" +

                "       }\n" +
                "    ]\n" +

                "  }\n" +
                "}";


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

        List<CreditCard> cards = creditcardRepository.findByUserEmail(c1.getUser().getEmail());
        Optional<CreditCard> ccOptional = cards.stream().findFirst();

        Assert.assertTrue(ccOptional.isPresent());
        Assert.assertEquals(
                "Token gravado esta errado: " + ccOptional.get().getToken(),
                "tokenFake",
                ccOptional.get().getToken());

    }
}
