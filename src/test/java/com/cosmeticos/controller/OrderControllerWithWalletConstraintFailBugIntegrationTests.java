package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.repository.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.Exception;
import java.net.URI;
import java.net.URISyntaxException;

import static java.time.LocalDateTime.now;

/**
 * Card que originou esta classe https://trello.com/c/v676aMHw
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerWithWalletConstraintFailBugIntegrationTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private WalletRepository walletRepository;

    @MockBean
    @Qualifier(value = "charger")
    private Charger charger;

    private Customer c1;

    private Professional professionalA;
    private Professional professionalB;
    private ProfessionalCategory professionalCategoryA, professionalCategoryB;
    private PriceRule priceRuleA, priceRuleB;

    @Before
    public void setUp() throws Exception {

        ChargeResponse<Object> response = new ChargeResponse<>("tokenFake");
        response.setResponseCode(ResponseCode.SUCCESS);

        Mockito.when(
                charger.addCard(Mockito.anyObject())
        ).thenReturn(response);

    }

    /**
     * Este teste depende da execução dos preloads. Eh um teste pra pegar um bug raro. Caso este teste quebre a
     * execução de todos os testes por algum problema de configuracao do teste, este pode ser Ignorado.
     * @throws URISyntaxException
     */
    @Test public void testaddwalletAfterALotOfOrders() throws URISyntaxException {

        String json1 = "{\"order\":{\"creditCardCollection\":[{\"cvv\":null,\"expirationDate\":null,\"idCreditCard\":null,\"lastUsage\":null,\"number\":null,\"oneClick\":false,\"ownerName\":null,\"status\":null,\"suffix\":null,\"token\":null,\"user\":null,\"vendor\":null}],\"date\":1516393480007,\"idCustomer\":{\"idCustomer\":1},\"paymentCollection\":[{\"creditCard\":null,\"id\":null,\"order\":null,\"parcelas\":null,\"priceRule\":{\"id\":3,\"name\":\"Cabelo Medio\",\"price\":15000},\"type\":\"CASH\",\"value\":null}],\"professionalCategory\":{\"professionalCategoryId\":2},\"scheduleId\":{\"scheduleStart\":1516429800000},\"status\":\"OPEN\",\"scheduled\":true}}\n";
        String json2 = "{\"order\":{\"creditCardCollection\":[{\"cvv\":null,\"expirationDate\":null,\"idCreditCard\":null,\"lastUsage\":null,\"number\":null,\"oneClick\":false,\"ownerName\":null,\"status\":null,\"suffix\":null,\"token\":null,\"user\":null,\"vendor\":null}],\"date\":1516393524735,\"idCustomer\":{\"idCustomer\":1},\"paymentCollection\":[{\"creditCard\":{\"cvv\":\"123\",\"expirationDate\":-61542560078589,\"idCreditCard\":null,\"lastUsage\":null,\"number\":\"0000000000000001\",\"oneClick\":true,\"ownerName\":\"teste\",\"status\":null,\"suffix\":null,\"token\":null,\"user\":null,\"vendor\":\"VISA\"},\"id\":null,\"order\":null,\"parcelas\":null,\"priceRule\":{\"id\":3,\"name\":\"Cabelo Medio\",\"price\":15000},\"type\":\"CC\",\"value\":null}],\"professionalCategory\":{\"professionalCategoryId\":2},\"scheduleId\":{\"scheduleStart\":1516429800000},\"status\":\"OPEN\",\"scheduled\":true}}";
        String json3 = "{\"order\":{\"creditCardCollection\":[{\"cvv\":null,\"expirationDate\":null,\"idCreditCard\":null,\"lastUsage\":null,\"number\":null,\"oneClick\":false,\"ownerName\":null,\"status\":null,\"suffix\":null,\"token\":null,\"user\":null,\"vendor\":null}],\"date\":1516393549236,\"idCustomer\":{\"idCustomer\":1},\"paymentCollection\":[{\"creditCard\":null,\"id\":null,\"order\":null,\"parcelas\":null,\"priceRule\":{\"id\":9,\"name\":\"Shiatsu\",\"price\":10000},\"type\":\"CASH\",\"value\":null}],\"professionalCategory\":{\"professionalCategoryId\":6},\"scheduleId\":{\"scheduleStart\":1516429800000},\"status\":\"OPEN\",\"scheduled\":true}}";
        String json4 = "{\"order\":{\"creditCardCollection\":[{\"cvv\":null,\"expirationDate\":null,\"idCreditCard\":null,\"lastUsage\":null,\"number\":null,\"oneClick\":false,\"ownerName\":null,\"status\":null,\"suffix\":null,\"token\":null,\"user\":null,\"vendor\":null}],\"date\":1516393570170,\"idCustomer\":{\"idCustomer\":1},\"paymentCollection\":[{\"creditCard\":null,\"id\":null,\"order\":null,\"parcelas\":null,\"priceRule\":{\"id\":3,\"name\":\"Cabelo Medio\",\"price\":15000},\"type\":\"CC\",\"value\":null}],\"professionalCategory\":{\"professionalCategoryId\":2},\"scheduleId\":{\"scheduleStart\":1516429800000},\"status\":\"OPEN\",\"scheduled\":true}}";
        String json5 = "{\"order\":{\"creditCardCollection\":[{\"cvv\":null,\"expirationDate\":null,\"idCreditCard\":null,\"lastUsage\":null,\"number\":null,\"oneClick\":false,\"ownerName\":null,\"status\":null,\"suffix\":null,\"token\":null,\"user\":null,\"vendor\":null}],\"date\":1516393593016,\"idCustomer\":{\"idCustomer\":1},\"paymentCollection\":[{\"creditCard\":null,\"id\":null,\"order\":null,\"parcelas\":null,\"priceRule\":{\"id\":3,\"name\":\"Cabelo Medio\",\"price\":15000},\"type\":\"CASH\",\"value\":null}],\"professionalCategory\":{\"professionalCategoryId\":2},\"scheduleId\":{\"scheduleStart\":1516429800000},\"status\":\"OPEN\",\"scheduled\":true}}";
        String json6 = "{\"order\":{\"creditCardCollection\":[{\"cvv\":null,\"expirationDate\":null,\"idCreditCard\":null,\"lastUsage\":null,\"number\":null,\"oneClick\":false,\"ownerName\":null,\"status\":null,\"suffix\":null,\"token\":null,\"user\":null,\"vendor\":null}],\"date\":1516393614561,\"idCustomer\":{\"idCustomer\":1},\"paymentCollection\":[{\"creditCard\":null,\"id\":null,\"order\":null,\"parcelas\":null,\"priceRule\":{\"id\":9,\"name\":\"Shiatsu\",\"price\":10000},\"type\":\"CC\",\"value\":null}],\"professionalCategory\":{\"professionalCategoryId\":6},\"scheduleId\":{\"scheduleStart\":1516429860000},\"status\":\"OPEN\",\"scheduled\":true}}";
        String json7 = "{\"order\":{\"creditCardCollection\":[{\"cvv\":null,\"expirationDate\":null,\"idCreditCard\":null,\"lastUsage\":null,\"number\":null,\"oneClick\":false,\"ownerName\":null,\"status\":null,\"suffix\":null,\"token\":null,\"user\":null,\"vendor\":null}],\"date\":1516393645240,\"idCustomer\":{\"idCustomer\":1},\"paymentCollection\":[{\"creditCard\":null,\"id\":null,\"order\":null,\"parcelas\":null,\"priceRule\":{\"id\":9,\"name\":\"Shiatsu\",\"price\":10000},\"type\":\"CC\",\"value\":null}],\"professionalCategory\":{\"professionalCategoryId\":6},\"scheduleId\":{\"scheduleStart\":1516429800000},\"status\":\"OPEN\",\"scheduled\":true}}";

        Assert.assertEquals(HttpStatus.OK, doPostOrder(json1).getStatusCode());
        Assert.assertEquals(HttpStatus.OK, doPostOrder(json2).getStatusCode());
        Assert.assertEquals(HttpStatus.OK, doPostOrder(json3).getStatusCode());
        Assert.assertEquals(HttpStatus.OK, doPostOrder(json4).getStatusCode());
        Assert.assertEquals(HttpStatus.OK, doPostOrder(json5).getStatusCode());
        Assert.assertEquals(HttpStatus.OK, doPostOrder(json6).getStatusCode());
        Assert.assertEquals(HttpStatus.OK, doPostOrder(json7).getStatusCode());
    }

    ResponseEntity<OrderResponseBody> doPostOrder(String json) throws URISyntaxException {
        RequestEntity<String> entity = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        return testRestTemplate.exchange(entity, OrderResponseBody.class);
    }

}
