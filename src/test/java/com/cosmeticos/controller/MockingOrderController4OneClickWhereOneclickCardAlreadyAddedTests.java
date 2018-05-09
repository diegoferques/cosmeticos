package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.commons.SuperpayFormaPagamento;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.superpay.SuperpayCompletoClient;
import com.cosmeticos.payment.superpay.SuperpayOneClickClient;
import com.cosmeticos.payment.superpay.ws.oneclick.ResultadoPagamentoWS;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.RandomCode;
import com.cosmeticos.service.SuperpayOneClickPaymentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.Exception;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpResponse.response;

/**
 * Testa um novo pedido sendo criado pra um cliente que ja fez um pedido outro dia, oportunidade na qual também registrou um cartao
 * oneclick, mas dessa vez seu request de pedido vai sem o cartao. O servidor devera realizar a compra com oneclic
 * ao checar no banco que esse cliente tem um cartao oneclick registrado.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingOrderController4OneClickWhereOneclickCardAlreadyAddedTests {

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
    private SuperpayOneClickPaymentService charger;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private SuperpayOneClickPaymentService oneClickPaymentService;

    @MockBean
    private SuperpayOneClickClient oneClickClient;

    @MockBean
    private SuperpayCompletoClient completoClient;

    private Customer c1;
    private Professional professional;
    private ProfessionalCategory ps1;
    private PriceRule priceRule;

    private ClientAndServer mockServer;

    @Before
    public void setup() throws ParseException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        ////////////// SETUP     //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////

        ChargeResponse<Object> addCardResponse = new ChargeResponse<>("tokenFake");
        addCardResponse.setResponseCode(ResponseCode.SUCCESS);


        // O reserve nao existe no one click real. So numa api "Completo"
        ResultadoPagamentoWS oneclickReserveResult = new ResultadoPagamentoWS();
        oneclickReserveResult.setStatusTransacao(Payment.Status.PAGO_E_NAO_CAPTURADO.getSuperpayStatusTransacao());

        com.cosmeticos.payment.superpay.ws.completo.ResultadoPagamentoWS oneclickCaptureResult =
                new com.cosmeticos.payment.superpay.ws.completo.ResultadoPagamentoWS();
        oneclickCaptureResult.setStatusTransacao(Payment.Status.PAGO_E_CAPTURADO.getSuperpayStatusTransacao());

        Mockito.when(
                charger.addCard(Mockito.anyObject())
        ).thenReturn(addCardResponse);

        Mockito.when(
                charger.reserve(Mockito.anyObject())
        ).thenCallRealMethod();

        Mockito.when(
                charger.capture(Mockito.anyObject())
        ).thenCallRealMethod();

        Mockito.when(
                oneClickClient.pay(Mockito.anyObject())
        ).thenReturn(oneclickReserveResult);

        Mockito.when(
                completoClient.capturePayment(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyObject(),
                        Mockito.anyString(),
                        Mockito.anyObject(),
                        Mockito.anyObject(),
                        Mockito.anyObject())
        ).thenReturn(oneclickCaptureResult);


        c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-cliente");
        c1.getUser().setEmail(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-cliente@bol");
        c1.getUser().setPersonType(User.PersonType.FISICA);

        professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-professional");
        professional.getUser().setEmail(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-professional@bol");
        professional.getUser().setPersonType(User.PersonType.JURIDICA);


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
    public void testOpenSecondOrderUsingOneclickCardRegisteredAtTheFirstOpenedOrder() throws Exception {

        mockServer = startClientAndServer(9000);
        mockServer.when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/1/card")
                .withHeader("merchantId","873a9a0d-07dd-44a0-ae9d-c935061b9678")
                .withHeader("merchantKey","abcd"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBody("{\n" +
                                "  \"CardToken\": \"tokenFake\",\n" +
                                "  \"Links\": {\n" +
                                "    \"Method\": \"GET\",\n" +
                                "    \"Rel\": \"self\",\n" +
                                "    \"Href\": \"https://apiquerydev.cieloecommerce.cielo.com.br/1/card/db62dc71-d07b-4745-9969-42697b988ccb\"}\n" +
                                "}"));



        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Criaremos uma order nova com registro de cartao oneclick, cancelaremos ela, e iniciaremos //
        // uma nova order SEM cartao oneclick. A intencao eh usar o cartao oneclick registrado na 1a ///
        // order para efetuar a venda da segunda order. ///////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////

        String ccNumber = new RandomCode(16).nextString();

        String oneclickCardJsonFragment =
                "         ,\n" +
                "         \"creditCard\": \n" +
                "         {\n" +
                "            \"token\": \"ALTERADOOOOOOOOOOOOO\",\n" +
                "            \"ownerName\": \"Teste\",\n" +
                "            \"suffix\": \""+ccNumber.substring(12)+"\",\n" +
                "            \"number\": \""+ccNumber+"\",\n" +
                "            \"securityCode\": \"098\",\n" +
                "            \"expirationDate\": \""+ Timestamp.valueOf(now().plusDays(30)).getTime() +"\",\n" +
                "            \"vendor\": \""+ SuperpayFormaPagamento.MASTERCARD +"\",\n" +
                "            \"status\": \"ACTIVE\",\n" +
                "            \"oneClick\": true"+
                "         }\n";

        String jsonTemplate = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : "+ Timestamp.valueOf(LocalDateTime.now()).getTime()+",\n" +
                "    \"status\" : 0,\n" +
                "    \"attendanceType\": \"ON_SITE\"," +

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
                "         }\n" +

                "%s" +

                "       }\n" +
                "    ]\n" +

                "  }\n" +
                "}";

        String jsonWithCard = String.format(jsonTemplate, oneclickCardJsonFragment);

        ResponseEntity<OrderResponseBody> exchange = post(jsonWithCard);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        Order order = exchange.getBody().getOrderList().get(0);

        Assert.assertEquals(Order.Status.OPEN, order.getStatus());

        List<CreditCard> cards = creditcardRepository.findByUserEmail(c1.getUser().getEmail());
        Optional<CreditCard> ccOptional = cards.stream().findFirst();

        Assert.assertTrue("CArtao de credito oneclick nao foi registrado!", ccOptional.isPresent());
        Assert.assertEquals(
                "Token gravado esta errado: " + ccOptional.get().getToken(),
                "tokenFake",
                ccOptional.get().getToken());

        Long orderId = order.getIdOrder();

        updateToCancelled(orderId);


        // Segunda Order

        String jsonWithoutCard = String.format(jsonTemplate, "");

        ResponseEntity<OrderResponseBody> exchange2ndOrder = post(jsonWithoutCard);

        Assert.assertEquals(HttpStatus.OK, exchange2ndOrder.getStatusCode());

        Order secondOrder = exchange2ndOrder.getBody().getOrderList().get(0);

        Assert.assertEquals(Order.Status.OPEN, secondOrder.getStatus());

        mockServer.stop();
    }

    private ResponseEntity<OrderResponseBody> post(String json) throws URISyntaxException {
        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        return testRestTemplate
                .exchange(entity, OrderResponseBody.class);
    }

    private void updateToCancelled(Long orderId) throws URISyntaxException {

        //////////////////////////////////////////////////////////////////////////////////
        //////// ATUALIZANDO ORDER PARA CANCELLED  ///////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        String jsonUpdateAccepted = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderId+",\n" +
                "    \"status\" : "+ Order.Status.CANCELLED.ordinal() +"\n" +
                "\n}\n" +
                "}";

        RequestEntity<String> entityUpdateAccepted =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdateAccepted);

        ResponseEntity<OrderResponseBody> exchangeUpdateAccepted = testRestTemplate
                .exchange(entityUpdateAccepted, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdateAccepted);
        Assert.assertNotNull(exchangeUpdateAccepted.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateAccepted.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdateAccepted.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.CANCELLED, orderUpdateAccepted.getStatus());

    }

}
