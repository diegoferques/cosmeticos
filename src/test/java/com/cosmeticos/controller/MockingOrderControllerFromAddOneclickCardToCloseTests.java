package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.*;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.SuperpayCompletoClient;
import com.cosmeticos.payment.SuperpayOneClickClient;
import com.cosmeticos.payment.superpay.ws.oneclick.ResultadoPagamentoWS;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OneClickPaymentService;
import com.cosmeticos.service.RandomCode;
import lombok.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.Exception;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;

/**
 * Created by matto on 28/06/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingOrderControllerFromAddOneclickCardToCloseTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private SuperpayOneClickClient oneClickClient;

    @MockBean
    private SuperpayCompletoClient completoClient;

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
    private OneClickPaymentService charger;

    private Customer c1;
    private Professional professional;
    private ProfessionalCategory ps1;
    private PriceRule priceRule;

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
    public void testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard() throws Exception {



        ///////////////////////////////////////////////////////////////////////////////////////////////
        ////////////// TESTING   //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////

        String ccNumber = new RandomCode(16).nextString();

        String json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : "+ Timestamp.valueOf(LocalDateTime.now()).getTime()+",\n" +
                "    \"status\" : 0,\n" +

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
                "         }\n" +

                "       }\n" +
                "    ]\n" +

                "  }\n" +
                "}";

        ResponseEntity<OrderResponseBody> exchange = postOrder(json);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        Order order = exchange.getBody().getOrderList().get(0);

        Assert.assertEquals(Order.Status.OPEN, order.getStatus());
        Assert.assertNull(order.getScheduleId());

        List<CreditCard> cards = creditcardRepository.findByUserEmail(c1.getUser().getEmail());
        Optional<CreditCard> ccOptional = cards.stream().findFirst();

        Assert.assertTrue(ccOptional.isPresent());
        Assert.assertEquals(
                "Token gravado esta errado: " + ccOptional.get().getToken(),
                "tokenFake",
                ccOptional.get().getToken());

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////// Validando que outros endpoints afetados pela inclusao do cartao se comportem corretamente ////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // GET customers/ retorne creditCardCount > 0
        ResponseEntity<MyCustomerResponseBody> customerResponseEntity =
                getCustomer(testRestTemplate, "user.email=" + c1.getUser().getEmail());
        Assert.assertEquals(HttpStatus.OK, customerResponseEntity.getStatusCode());
        Assert.assertTrue("CreditCardCount nao esta correto",
                customerResponseEntity.getBody()
                .getCustomerList()
                .get(0)
                .getUser()
                .getCreditCardCount() > 0
        );

        // GET creditCard/ retorne o cartao inserido
        ResponseEntity<CreditCardResponseBody> creditCardResponseResponse =
                CreditCardControllerTests.getCreditcard(testRestTemplate, "user.email=" + c1.getUser().getEmail());
        Assert.assertEquals(HttpStatus.OK, creditCardResponseResponse.getStatusCode());
        Assert.assertTrue("Cartao do usuario nao foi inserido",
                !creditCardResponseResponse.getBody()
                .getCreditCardList()
                .isEmpty()
        );

        ////////////////////////////////////////////////////////////////////////////////////////////////
        /////////// Validando os demais passos de compra  /////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////
        Long orderId = order.getIdOrder();

        updateToAccepted(orderId);

        updateToInprogress(orderId);

        updateToSemiclosed(orderId);

        updateToReady2chargee(orderId);

    }

    ResponseEntity<OrderResponseBody> postOrder(String json) throws URISyntaxException {
        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        return testRestTemplate
                .exchange(entity, OrderResponseBody.class);
    }

    private void updateToReady2chargee(Long orderId) throws URISyntaxException {

        //////////////////////////////////////////////////////////////////////////////////
        //////// ATUALIZANDO ORDER PARA Ready2Charger  //////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        String jsonUpdateReady2charge = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderId+",\n" +
                "    \"status\" : "+ Order.Status.READY2CHARGE.ordinal() +",\n" +
                "    \"professionalCategory\" : {\n" +
                "       \"professional\" : {\n" +
                "        \"user\" : {\n" +
                "            \"voteCollection\" : [\n" +
                "                {\n" +
                "                    \"value\" : 2\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" + //user
                "     }\n" +// professional
                "    }\n" + // professionalCategory
                "\n}\n" +
                "}";


        RequestEntity<String> entityUpdateReady2Charger =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdateReady2charge);

        ResponseEntity<OrderResponseBody> exchangeUpdateReady2Charger = testRestTemplate
                .exchange(entityUpdateReady2Charger, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdateReady2Charger);
        Assert.assertNotNull(exchangeUpdateReady2Charger.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateReady2Charger.getStatusCode());


        Order orderUpdateReady2Charger =  exchangeUpdateReady2Charger.getBody().getOrderList().get(0);

        Assert.assertEquals(Order.Status.CLOSED, orderUpdateReady2Charger.getStatus());

    }

    private void updateToSemiclosed(Long orderId) throws URISyntaxException {

        //////////////////////////////////////////////////////////////////////////////////
        //////// ATUALIZANDO ORDER PARA SEMICLOSED  //////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        // Aqui deveria ir a avaliacao do Customer mas estou ignorando isso por enquanto.

        String jsonUpdateSemiclosed = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderId+",\n" +
                "    \"status\" : "+ Order.Status.SEMI_CLOSED.ordinal() +"\n" +
                "\n}\n" +
                "}";

        RequestEntity<String> entityUpdateSemiClosed =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdateSemiclosed);

        ResponseEntity<OrderResponseBody> exchangeUpdateSemiClosed = testRestTemplate
                .exchange(entityUpdateSemiClosed, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdateSemiClosed);
        Assert.assertNotNull(exchangeUpdateSemiClosed.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateSemiClosed.getStatusCode());

        Order orderUpdateSemiClosed= exchangeUpdateSemiClosed.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.SEMI_CLOSED, orderUpdateSemiClosed.getStatus());


    }

    private void updateToInprogress(Long orderId) throws URISyntaxException {

        //////////////////////////////////////////////////////////////////////////////////
        //////// ATUALIZANDO ORDER PARA INPROGRESS  //////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        String jsonUpdateInprogress = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderId+",\n" +
                "    \"status\" : "+ Order.Status.INPROGRESS.ordinal() +"\n" +
                "\n}\n" +
                "}";

        RequestEntity<String> entityUpdateInProgress =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdateInprogress);

        ResponseEntity<OrderResponseBody> exchangeUpdateInProgress = testRestTemplate
                .exchange(entityUpdateInProgress, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdateInProgress);
        Assert.assertNotNull(exchangeUpdateInProgress.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateInProgress.getStatusCode());

        Order orderUpdateInProgress= exchangeUpdateInProgress.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.INPROGRESS, orderUpdateInProgress.getStatus());


    }

    private void updateToAccepted(Long orderId) throws URISyntaxException {

        //////////////////////////////////////////////////////////////////////////////////
        //////// ATUALIZANDO ORDER PARA ACCEPTED   ///////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        String jsonUpdateAccepted = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderId+",\n" +
                "    \"status\" : "+ Order.Status.ACCEPTED.ordinal() +"\n" +
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
        Assert.assertEquals(Order.Status.ACCEPTED, orderUpdateAccepted.getStatus());

    }

    public static ResponseEntity<MyCustomerResponseBody> getCustomer(final TestRestTemplate restTemplate, String query) throws URISyntaxException {

        return restTemplate.exchange( //
                "/customers/" + (query != null && !query.isEmpty() ? "?" + query : ""), //
                HttpMethod.GET, //
                null,
                MyCustomerResponseBody.class);
    }

    /**
     * Como a aplicacao User tem sua versao de user para adaptar-se ao json,
     * criamos esta classe para representar o json da classe {@link User} e viabilizar
     * alguns testes cujo o uso de {@link User} atrapalha mais que ajuda devido às
     * logicas que existem em alguns seus getters e imcompatilibilidade de anotacoes de
     * hibernate que impactam no marshalling e unmarshalling dos jsons que o Jackson faz.
     */
    @Data
    private static class MyCustomerResponseBody
    {
        private List<MyCustomer> customerList;
    }

    @Data
    private static class MyCustomer
    {
        private MyUser user;
    }

    @Data
    private static class MyUser
    {
        private Long idLogin;
        private String username;
        private String password;
        private Boolean lostPassword = false;
        private String lostPasswordToken;
        private Date lostPasswordValidThru;
        private String email;
        private String sourceApp;
        private User.Status status;
        private String goodByeReason;
        private User.Type userType;
        private Set<Role> roleCollection;
        private User.PersonType personType;
        private Float evaluation = .0f;
        private Integer creditCardCount = 0;
        private String profileImageUrl;
        private Set<Vote> voteCollection = new HashSet<>();
        private Date lastStatusUpdate;
    }
}
