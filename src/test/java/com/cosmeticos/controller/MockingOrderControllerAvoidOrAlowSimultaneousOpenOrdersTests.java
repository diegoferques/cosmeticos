package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.superpay.SuperpayCompletoClient;
import com.cosmeticos.payment.superpay.SuperpayOneClickClient;
import com.cosmeticos.payment.superpay.ws.oneclick.ResultadoPagamentoWS;
import com.cosmeticos.repository.CategoryRepository;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.service.SuperpayOneClickPaymentService;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.LocalDateTime.now;

/**
 * Created by matto on 28/06/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingOrderControllerAvoidOrAlowSimultaneousOpenOrdersTests {

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

    @MockBean
    private SuperpayOneClickPaymentService charger;

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
    }

    @Test
    public void testProfessionalShouldAcceptAnyScheduledOrdersWhileHasAnInProgressOrder() throws Exception {

        // Usuario que vai estar inprogress com o profissional
        Customer c1 = createAndPersistCustomer();

        Professional professional = createAndPersistProfessional();

        ProfessionalCategory ps1 = professional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get();
        PriceRule priceRule = ps1.getPriceRuleList()
                .stream()
                .findFirst()
                .get();

        String json = OrderJsonHelper.buildJsonCreateScheduledOrder(
                c1,
                ps1,
                priceRule,
                Payment.Type.CASH,
                Timestamp.valueOf(now().plusDays(2)).getTime()
        ) ;

        ResponseEntity<OrderResponseBody> exchange = postOrder(json);

        Order order = exchange.getBody()
                .getOrderList()
                .get(0);

        Long orderId = order.getIdOrder();

        updateToAccepted(orderId);

        updateToInprogress(orderId);


        // Neste momento outra pessoa (c2) abre um pedido agendado também

        Customer c2 = createAndPersistCustomer();

        String json2 = OrderJsonHelper.buildJsonCreateScheduledOrder(
                c2,
                ps1,
                priceRule,
                Payment.Type.CASH,
                Timestamp.valueOf(now().plusDays(3)).getTime()
        ) ;

        ResponseEntity<OrderResponseBody> exchange2 = postOrder(json2);

        Order order2 = exchange2.getBody()
                .getOrderList()
                .get(0);

        Long orderId2 = order2.getIdOrder();

        // Faz assert de httpstatus eh 200
        updateToAccepted(orderId2);
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

    Professional createAndPersistProfessional() {
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setEmail(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-professional@bol");
        professional.getUser().setPersonType(User.PersonType.JURIDICA);


        professionalRepository.save(professional);

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(priceRule);

        professionalCategoryRepository.save(ps1);

        return professional;
    }

    Customer createAndPersistCustomer() {
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setEmail(System.nanoTime()+ "-testOpenOrderAndSaveOneClickCreditcardAfterSuccesfullySuperpayAddCard"
                + "-cliente@bol");
        c1.getUser().setPersonType(User.PersonType.FISICA);

        customerRepository.save(c1);

        return c1;
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
