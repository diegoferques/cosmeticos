package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ErrorCode;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OrderService;
import com.cosmeticos.service.PaymentService;
import com.cosmeticos.service.VoteService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;

/**
 * Created by diego.MindTek on 26/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTests {

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
    private CategoryRepository serviceRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

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

    /**
     * Apesar de nao ser uma classe de teste mocking, precisamos mocar a ida ao superpay,.
     */
    @MockBean
    private PaymentService paymentService;

    @Before
    public void setup() throws ParseException, JsonProcessingException {
        Category service = serviceRepository.findByName("PEDICURE");

        if(service == null) {
            service = new Category();
            service.setName("PEDICURE");
            serviceRepository.save(service);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////// Mocando o controller que vai no superpay e vai sofrer um refactoring monstruoso //////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        Optional<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao(2);

        Mockito.when(
                paymentService.sendRequest(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);


        ///////////////////////////////////////////////////////////////////////
        ///////// Mocando chamadas a consulta transacao ///////////////////////
        ///////////////////////////////////////////////////////////////////////
        RetornoTransacao fakeRetornoTransacao = new RetornoTransacao();
        fakeRetornoTransacao.setStatusTransacao(1);// TODO: Transformar esse 1 em um enum de status

        ResponseEntity<RetornoTransacao> mockedResponse = new ResponseEntity<RetornoTransacao>(
                fakeRetornoTransacao,
                HttpStatus.OK
        ) ;
        Mockito.when(
                paymentService.doConsultaTransacao(
                        Mockito.anyObject(),
                        Mockito.anyObject(),
                        Mockito.anyObject())
        ).thenReturn(mockedResponse);

        ////////////////////////////////////////////////////////////////////////////
        ///////// Mocando Update Payment Status, que ainda nao foi implementado  ///
        ////////////////////////////////////////////////////////////////////////////
        Mockito.doReturn(true).when(paymentService).
                updatePaymentStatus(Mockito.anyObject()
        );
    }

    //TESTANDO O RETORNO DE ORDER PELO ID
    @Test
    public void testFindById() throws ParseException {

        final ResponseEntity<OrderResponseBody> exchange = //
                testRestTemplate.exchange( //
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
                testRestTemplate.exchange( //
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
                testRestTemplate.exchange( //
                        "/orders/1", //
                        HttpMethod.DELETE, //
                        null,
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

    }

    @Test
    public void testUpdateOK() throws IOException, URISyntaxException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(ps1, c1);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchange = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        OrderResponseBody orderResponseBody = exchange.getBody();

        Order orderInserida = orderResponseBody.getOrderList().get(0);

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +

                "    \"idOrder\" : "+orderInserida.getIdOrder()+",\n" +
                "    \"status\" : "+ Order.Status.CANCELLED.ordinal() +"\n" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders")) // put pra atualizar o que inserimos la em cima
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        OrderResponseBody responseBodyDoPut = exchangeUpdate.getBody();

        Order orderAtualizada = responseBodyDoPut.getOrderList().get(0);

        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Assert.assertEquals(Order.Status.CANCELLED, orderAtualizada.getStatus());

        orderRestultFrom_testUpdateOk = exchange.getBody().getOrderList().get(0);

/*

        Order s1 = fakeOrder();


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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        
        Assert.assertEquals( Order.Status.CANCELLED, exchange.getBody().getOrderList().get(0).getHttpStatus()); */

    }

    //TODO - Corrigir este teste, pois algo foi alterado e o Schedule esta retornando NULL. Gera erros em outros testes
    @Test
    public void createScheduledOrderOk() throws URISyntaxException {

        /*
         PRE-CONDICOES para o teste:
         Criamos um Customer qualquer. Criamos um Profissional qualquer e o associamos a um Service.
         Salvamos tudo no banco.
          */

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-cliente");
        c1.getUser().setEmail(System.nanoTime() + "-cliente@bol");
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime() + "-professional");
        professional.getUser().setEmail(System.nanoTime() + "-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);


        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(priceRule);

        professional.getProfessionalCategoryCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);
        professionalCategoryRepository.save(ps1);

        /************ FIM DAS PRE_CONDICOES **********************************/

        /*
         O teste comeca aqui:
         Fazemos um json com informacoes que batem com o que foi inserido acima. Nossa pre-condicao pede que 3
         objetos estejam persistidos no banco. Usamos os IDs desses caras nesse json abaixo pq se fosse um servico em
         producao as pre-condicoes seriam as mesmas e o json abaixo seria igual.
          */
        String json = OrderJsonHelper.buildJsonCreateScheduledOrder(
                c1,
                ps1,
                priceRule,
                Payment.Type.CASH,
                Timestamp.valueOf(now().plusHours(5)).getTime()
        );

        System.out.println(json);

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
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalCategory());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalCategory().getCategory());
        Assert.assertEquals("PEDICURE",
                exchange.getBody().getOrderList().get(0).getProfessionalCategory().getCategory().getName());

        orderRestultFrom_createScheduledOrderOk = exchange.getBody().getOrderList().get(0);

    }

    @Test public void testaddwallet() throws URISyntaxException {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testaddwallet-cliente");
        c1.getUser().setEmail("testaddwallet-cliente@bol");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testaddwallet-professional");
        professional.getUser().setEmail("testaddwallet-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(priceRule);

        // Atualizando associando o Profeissional ao Servico
        professionalCategoryRepository.save(ps1);

        /*
         O teste comeca aqui:
         Fazemos um json com informacoes que batem com o que foi inserido acima. Um usuario que existe no banco e
         um profissional associado a um servico que existirao no banco.
          */
        Assert.assertTrue(professional.getWallet() == null || professional.getWallet().getCustomers().isEmpty());

        String json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, ps1, priceRule, Payment.Type.CASH,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );


        RequestEntity<String> entity = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        testRestTemplate.exchange(entity, OrderResponseBody.class);

        // Antes do 1o request a carteira tem que estar vazia.
        // //Apos o 2o request a carteira ainda tem q estar vazia.
        Assert.assertTrue(professional.getWallet() == null || professional.getWallet().getCustomers().isEmpty());

        // Identico ao criado acima...
        json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, ps1, priceRule, Payment.Type.CASH,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );


        RequestEntity<String> entityPost2 = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        testRestTemplate.exchange(entityPost2, OrderResponseBody.class);

        Wallet wallet = walletRepository.findByProfessional_idProfessional(professional.getIdProfessional());//

        Assert.assertTrue(wallet != null && !wallet.getCustomers().isEmpty());
        Assert.assertEquals(1, wallet.getCustomers().size());

    }
    @Test
    public void updateStatusWithJson() throws URISyntaxException {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("updateStatusWithJson-cliente");
        c1.getUser().setEmail("updateStatusWithJson-cliente@bol");
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("updateStatusWithJson-professional");
        professional.getUser().setEmail("updateStatusWithJson-professional@bol");

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
                "    \"status\" : 5\n" +
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
        Assert.assertEquals(Order.Status.CLOSED,
                exchangePut.getBody().getOrderList().get(0).getStatus());
    }

    @Test
    public void testParaTravarUpdateDeStatus() throws URISyntaxException, ParseException, JsonProcessingException {


        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testParaTravarUpdateDeStatus-cliente");
        c1.getUser().setEmail("testParaTravarUpdateDeStatus-cliente@bol");
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testParaTravarUpdateDeStatus-professional");
        professional.getUser().setEmail("testParaTravarUpdateDeStatus-professional@bol");

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
                "    \"status\" : \""+Order.Status.CLOSED + "\"\n" +
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
        Assert.assertEquals(Order.Status.CLOSED,
                exchangePut.getBody().getOrderList().get(0).getStatus());

        String jsonUpdate2 = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+newOrder.getIdOrder()+",\n" +
                "    \"status\" : \""+Order.Status.SEMI_CLOSED + "\"\n" +
                "  }\n" +
                "}";

        RequestEntity<String> entityPut2 =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate2);

        ResponseEntity<OrderResponseBody> exchangePut2 = testRestTemplate
                .exchange(entityPut2, OrderResponseBody.class);

        Assert.assertNotNull(exchangePut2);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchangePut2.getStatusCode());

    }


    @Test
    public void updateScheduledOrder() throws URISyntaxException {

        createScheduledOrderOk();

        Order o1 = orderRestultFrom_createScheduledOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleId\" : "+ o1.getScheduleId().getScheduleId() +",\n" + //AQUI PEGO O SCHEDULE JA CRIADO
                "      \"scheduleStart\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 10, 30, 0)).getTime()  +"\"\n" +
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertEquals(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 10, 30, 0)).getTime(), exchangeUpdate.getBody().getOrderList().get(0).getScheduleId().getScheduleStart().getTime());

    }

    @Test
    public void updateScheduledOrderToInactive() throws URISyntaxException {

        createScheduledOrderOk();

        Order o1 = orderRestultFrom_createScheduledOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleId\" : "+ o1.getScheduleId().getScheduleId() +"\n" +
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());
    }

    @Test
    public void createOrderOk() throws URISyntaxException {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-createOrderOk" + "-cliente");
        c1.getUser().setEmail(System.nanoTime()+ "-createOrderOk" + "-cliente@bol");
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime()+ "-createOrderOk" + "-professional");
        professional.getUser().setEmail(System.nanoTime()+ "-createOrderOk" + "-professional@bol");

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

        /*
         O teste comeca aqui:
        Este teste nao cria order agendada.
          */
        String json = OrderJsonHelper.buildJsonCreateNonScheduledOrder(
                c1,
                ps1,
                Payment.Type.CASH,
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


        orderRestultFrom_createOrderOk = exchange.getBody().getOrderList().get(0);
    }

    @Test
    public void updateOrderOkToScheduled() throws URISyntaxException {

        createOrderOk();

        Order o1 = orderRestultFrom_createOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"scheduleId\" : {\n" +
                //"      \"scheduleId\" : "+ o1.getScheduleId().getScheduleId() +",\n" +
                "      \"scheduleStart\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 22, 30, 0)).getTime()  +"\"\n" +
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());

        orderRestultFrom_updateOrderOkToScheduled = exchangeUpdate.getBody().getOrderList().get(0);

    }

    @Test
    public void updateScheduledOrderOkToScheduledStatusDenied() throws URISyntaxException {

        updateOrderOkToScheduled();

        Order o1 = orderRestultFrom_updateOrderOkToScheduled;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleId\" : "+ o1.getScheduleId().getScheduleId() +"\n" +
                //"      \"scheduleDate\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 22, 30, 0)).getTime()  +"\",\n" +
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());

        //orderRestultFrom_updateOrderOkToScheduled = exchangeUpdate.getBody().getOrderList().get(0);

    }

    @Test
    public void updateScheduledOrderOkToOrderStatusScheduled() throws URISyntaxException, ParseException, JsonProcessingException {

        createScheduledOrderOk();
        Order o1 = orderRestultFrom_createScheduledOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"status\" : \""+ Order.Status.SCHEDULED +"\"\n" +
                //"    \"scheduleId\" : {\n" +
                //"      \"scheduleId\" : "+ o1.getScheduleId().getScheduleId() +",\n" +
                //"      \"scheduleDate\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 22, 30, 0)).getTime()  +"\",\n" +
                //"      \"status\" : \""+ Schedule.Status.DENIED +"\"\n" +
                //"    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        //Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertEquals(Order.Status.SCHEDULED, exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        orderRestultFrom_updateScheduledOrderOkToScheduled = exchangeUpdate.getBody().getOrderList().get(0);
    }

    @Test
    public void updateCreatedOrderOkToStatusFinished() throws URISyntaxException, ParseException, JsonProcessingException {

        updateScheduledOrderOkToOrderStatusScheduled();

        Order o1 = orderRestultFrom_updateScheduledOrderOkToScheduled;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"status\" : \""+ Order.Status.EXECUTED +"\"\n" +
                //"    \"scheduleId\" : {\n" +
                //"      \"scheduleId\" : "+ o1.getScheduleId().getScheduleId() +",\n" +
                //"      \"scheduleDate\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 22, 30, 0)).getTime()  +"\",\n" +
                //"      \"status\" : \""+ Schedule.Status.DENIED +"\"\n" +
                //"    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertEquals(Order.Status.EXECUTED, exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        //orderRestultFrom_updateOrderOkToScheduled = exchangeUpdate.getBody().getOrderList().get(0);
    }

    @Test
    public void updatedTestpenaltyStatusAborted() throws URISyntaxException {

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
                "    \"status\" : 1\n" +
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

    //TESTANDO O RETORNO DE ORDER SEM EXIBIR OS STATUS CANCELLED
    @Test
    public void testFindByStatusNotCancelled() throws ParseException, URISyntaxException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(ps1, c1);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());

        Assert.assertEquals(Order.Status.OPEN, exchangeCreate.getBody().getOrderList().get(0).getStatus());

        Order createdOrder = exchangeCreate.getBody().getOrderList().get(0);
        //-------

        //ATUALIZAMOS ORDER PARA ACCEPTED PARA, POSTERIORMENTE, TENTAR CRIAR NOVO ORDER PARA O MESMO PROFESSIONAL
        ResponseEntity<OrderResponseBody> exchangeUpdateAccepted = this.updateOrderStatus(
                createdOrder.getIdOrder(), Order.Status.CANCELLED);

        Assert.assertNotNull(exchangeUpdateAccepted);
        Assert.assertNotNull(exchangeUpdateAccepted.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateAccepted.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdateAccepted.getBody().getOrderList().get(0);
        //ABAIXO GARANTIMOS QUE REALMENTE TEMOS NO BANCO UM ORDER COM STATUS CANCELLED
        Assert.assertEquals(Order.Status.CANCELLED, orderUpdateAccepted.getStatus());
        //-------

        final ResponseEntity<OrderResponseBody> exchange = //
                testRestTemplate.exchange( //
                        "/orders", //
                        HttpMethod.GET, //
                        null,
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertNotNull(exchange.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        for (Order order : exchange.getBody().getOrderList()) {
            //ABAIXO VERIFICAMOS SE NENHUM STATUS DOS PEDISOS SAO CANCELLED OU CLOSED
            Assert.assertNotEquals(Order.Status.CANCELLED, order.getStatus());
            Assert.assertNotEquals(Order.Status.CLOSED, order.getStatus());
        }

    }

    //TESTANDO O RETORNO DE ORDER SEM EXIBIR OS STATUS CLOSED
    @Test
    public void testFindByStatusNotClosed() throws ParseException, URISyntaxException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(ps1, c1);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());

        Assert.assertEquals(Order.Status.OPEN, exchangeCreate.getBody().getOrderList().get(0).getStatus());

        Order createdOrder = exchangeCreate.getBody().getOrderList().get(0);
        //-------

        //ATUALIZAMOS ORDER PARA ACCEPTED PARA, POSTERIORMENTE, TENTAR CRIAR NOVO ORDER PARA O MESMO PROFESSIONAL
        ResponseEntity<OrderResponseBody> exchangeUpdateAccepted = this.updateOrderStatus(
                createdOrder.getIdOrder(), Order.Status.CLOSED);

        Assert.assertNotNull(exchangeUpdateAccepted);
        Assert.assertNotNull(exchangeUpdateAccepted.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateAccepted.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdateAccepted.getBody().getOrderList().get(0);
        //ABAIXO GARANTIMOS QUE REALMENTE TEMOS NO BANCO UM ORDER COM STATUS CLOSED
        Assert.assertEquals(Order.Status.CLOSED, orderUpdateAccepted.getStatus());
        //-------

        //ABAIXO LISTAMOS TODOS OS ORDERS EXISTENTES, MAS NAO DEVEM VIR OS CANCELLED OU CLOSED
        final ResponseEntity<OrderResponseBody> exchange = //
                testRestTemplate.exchange( //
                        "/orders", //
                        HttpMethod.GET, //
                        null,
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertNotNull(exchange.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        for (Order order : exchange.getBody().getOrderList()) {
            //ABAIXO VERIFICAMOS SE NENHUM STATUS DOS PEDISOS SAO CANCELLED OU CLOSED
            Assert.assertNotEquals(Order.Status.CANCELLED, order.getStatus());
            Assert.assertNotEquals(Order.Status.CLOSED, order.getStatus());
        }

    }

    @Test
    public void testCreateToConflictedOrderErrorCausedByOrderStatusAccepted() throws IOException, URISyntaxException, ParseException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        Customer c2 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c2);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(ps1, c1);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);


        ResponseEntity<OrderResponseBody> exchangeCreate = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());

        Assert.assertEquals(Order.Status.OPEN, exchangeCreate.getBody().getOrderList().get(0).getStatus());

        Order createdOrder = exchangeCreate.getBody().getOrderList().get(0);
        //-------

        //ATUALIZAMOS ORDER PARA ACCEPTED PARA, POSTERIORMENTE, TENTAR CRIAR NOVO ORDER PARA O MESMO PROFESSIONAL
        ResponseEntity<OrderResponseBody> exchangeUpdateAccepted = this.updateOrderStatus(
                createdOrder.getIdOrder(), Order.Status.ACCEPTED);

        Assert.assertNotNull(exchangeUpdateAccepted);
        Assert.assertNotNull(exchangeUpdateAccepted.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateAccepted.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdateAccepted.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.ACCEPTED, orderUpdateAccepted.getStatus());
        //-------

        //TENTAMOS CRIAR NOVO ORDER PARA O MESMO PROFESSIONAL ENQUANTO ELE JA TEM UM ORDER COM STATUS ACCEPTED
        /*
        Nao podemos enviar o json de uma order agendada senao nao da conflito pois order agendada eh criada independente
        do profissional ja estar ocupado com outra order.
         */
        String jsonCreate2 = OrderJsonHelper.buildJsonCreateNonScheduledOrder(
                c2,
                ps1,
                Payment.Type.CASH,
                ps1.getPriceRuleList().stream().findFirst().get()
        );

        RequestEntity<String> entity2 =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate2);

        ResponseEntity<OrderResponseBody> exchangeCreate2 = testRestTemplate
                .exchange(entity2, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate2);
        Assert.assertEquals(HttpStatus.CONFLICT, exchangeCreate2.getStatusCode());
        //-------

    }

    @Test
    public void testParaTravarUpdateStatusDeExpiredParaOpen() throws IOException, URISyntaxException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(ps1, c1);

        RequestEntity<String> entity = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchange = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Order newOrder = exchange.getBody().getOrderList().get(0);


        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : " + newOrder.getIdOrder() + ",\n" +
                "    \"status\" : \"" + Order.Status.EXPIRED + "\"\n" +
                "  }\n" +
                "}";

        RequestEntity<String> entityPut = RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangePut = testRestTemplate
                .exchange(entityPut, OrderResponseBody.class);

        Assert.assertNotNull(exchangePut);
        Assert.assertEquals(HttpStatus.OK, exchangePut.getStatusCode());
        Assert.assertEquals(Order.Status.EXPIRED,
                exchangePut.getBody().getOrderList().get(0).getStatus());

        String jsonUpdate2 = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : " + newOrder.getIdOrder() + ",\n" +
                "    \"status\" : \"" + Order.Status.OPEN + "\"\n" +
                "  }\n" +
                "}";

        RequestEntity<String> entityPut2 = RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate2);

        ResponseEntity<OrderResponseBody> exchangePut2 = testRestTemplate
                .exchange(entityPut2, OrderResponseBody.class);

        Assert.assertNotNull(exchangePut2);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchangePut2.getStatusCode());

    }

    /**
     * Orders nao agendadas nao podem ser criadas pra um profissional se esse profissional ja tem uma order inprogress.
     * Se a order for agendada, podemos cria-las a vontade.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testCreateToConflictedOrderErrorCausedByOrderStatusInProgress() throws IOException, URISyntaxException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        Customer c2 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c2);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(ps1, c1);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());

        Assert.assertEquals(Order.Status.OPEN, exchangeCreate.getBody().getOrderList().get(0).getStatus());

        Order createdOrder = exchangeCreate.getBody().getOrderList().get(0);
        //-------

        //ATUALIZAMOS ORDER PARA IN_PROGRESS PARA, POSTERIORMENTE, TENTAR CRIAR NOVO ORDER PARA O MESMO PROFESSIONAL
        ResponseEntity<OrderResponseBody> exchangeUpdateAccepted = this.updateOrderStatus(
                createdOrder.getIdOrder(), Order.Status.INPROGRESS);

        Assert.assertNotNull(exchangeUpdateAccepted);
        Assert.assertNotNull(exchangeUpdateAccepted.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateAccepted.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdateAccepted.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.INPROGRESS, orderUpdateAccepted.getStatus());
        //-------

        //TENTAMOS CRIAR NOVO ORDER PARA O MESMO PROFESSIONAL ENQUANTO ELE JA TEM UM ORDER COM STATUS ACCEPTED
        // So vale para orders nao agendadas.
        String jsonCreate2 = OrderJsonHelper.buildJsonCreateNonScheduledOrder(
                c2,
                ps1,
                Payment.Type.CASH,
                ps1.getPriceRuleList().stream().findFirst().get()
        );

        RequestEntity<String> entity2 =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate2);

        ResponseEntity<OrderResponseBody> exchangeCreate2 = testRestTemplate
                .exchange(entity2, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate2);
        Assert.assertEquals(HttpStatus.CONFLICT, exchangeCreate2.getStatusCode());
        //-------

    }

    @Test
    public void testOrderClosedAndVote() throws IOException, URISyntaxException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        Customer c2 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c2);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA CLOSED E ENVIARMOS O VOTO
        String jsonCreate = this.getOrderCreateJson(ps1, c1);
        System.out.println(jsonCreate);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());

        Assert.assertEquals(Order.Status.OPEN, exchangeCreate.getBody().getOrderList().get(0).getStatus());

        Order createdOrder = exchangeCreate.getBody().getOrderList().get(0);
        //-------

        //ATUALIZAMOS ORDER PARA CLOSED E ENVIAMOS O VOTO
        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ createdOrder.getIdOrder() +",\n" +
                "    \"status\" : \""+ Order.Status.CLOSED +"\"\n" +
                "   },\n" +
                "   \"vote\" : 3\n" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdate.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.CLOSED, orderUpdateAccepted.getStatus());

        float vote = voteService.getUserEvaluation(ps1.getProfessional().getUser());
        Assert.assertNotNull(vote);
        Assert.assertTrue((float)3.0 == vote);
        //-------
    }

    @Test
    public void testOrderSemiClosedAndVoteCustomer() throws IOException, URISyntaxException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////

        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA CLOSED E ENVIARMOS O VOTO
        String jsonCreate = this.getOrderCreateJson(ps1, c1);
        System.out.println(jsonCreate);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());
        Assert.assertEquals(Order.Status.OPEN, exchangeCreate.getBody().getOrderList().get(0).getStatus());

        Order createdOrder = exchangeCreate.getBody().getOrderList().get(0);
        //-------

        //ATUALIZAMOS ORDER PARA SEMI CLOSED E ENVIAMOS O VOTO
        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ createdOrder.getIdOrder() +",\n" +
                "    \"status\" : \""+ Order.Status.SEMI_CLOSED +"\",\n" +
                "    \"idCustomer\" : {\n" +
                "        \"idCustomer\": "+c1.getIdCustomer()+",\n" +
                "        \"user\" : {\n" +
                "            \"voteCollection\" : [\n" +
                "                {\n" +
                "                    \"value\" : 4\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdate.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.SEMI_CLOSED, orderUpdateAccepted.getStatus());

        float vote = voteService.getUserEvaluation(c1.getUser());
        Assert.assertNotNull(vote);
        Assert.assertTrue((float)4.0 == vote);
    }

    @Test
    public void testOrderSemiClosedAndVoteProfessional() throws IOException, URISyntaxException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////

        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA CLOSED E ENVIARMOS O VOTO
        String jsonCreate = this.getOrderCreateJson(ps1, c1);
        System.out.println(jsonCreate);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());
        Assert.assertEquals(Order.Status.OPEN, exchangeCreate.getBody().getOrderList().get(0).getStatus());

        Order createdOrder = exchangeCreate.getBody().getOrderList().get(0);

        ////////////////////////////////////////////////////////////////////////////////////////////
        ///////// ATUALIZAMOS ORDER PARA SEMI CLOSED E ENVIAMOS O VOTO /////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        String jsonUpdateSemiclosed = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ createdOrder.getIdOrder() +",\n" +
                "    \"status\" : \""+ Order.Status.SEMI_CLOSED +"\",\n" +
                "    \"idCustomer\" : {\n" +
                "        \"idCustomer\": "+c1.getIdCustomer()+",\n" +
                "        \"user\" : {\n" +
                "            \"voteCollection\" : [\n" +
                "                {\n" +
                "                    \"value\" : 4\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        System.out.println(jsonUpdateSemiclosed);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdateSemiclosed);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdate.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.SEMI_CLOSED, orderUpdateAccepted.getStatus());

        float vote = voteService.getUserEvaluation(c1.getUser());
        Assert.assertNotNull(vote);
        Assert.assertTrue((float)4.0 == vote);


        ////////////////////////////////////////////////////////////////////////////////////////////
        ///////// ATUALIZAMOS ORDER PARA CLOSED E ENVIAMOS O VOTO DO PROFISSIONAL    ///////////////
        ////////////////////////////////////////////////////////////////////////////////////////////


        String jsonUpdateClosed = "{\n" +
                    "  \"order\" : {\n" +
                    "    \"idOrder\" : "+createdOrder.getIdOrder()+",\n" +
                    "    \"date\" : "+Timestamp.valueOf(LocalDateTime.now()).getTime()+",\n" +
                    "    \"status\" : \""+Order.Status.READY2CHARGE +"\",\n" +

                    "    \"professionalCategory\" : {\n" +
                    "       \"professional\" : {\n" +
                    "        \"user\" : {\n" +
                    "            \"voteCollection\" : [\n" +
                    "                {\n" +
                    "                    \"value\" : 2\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "       },\n" +
                    "      \"professionalCategoryId\": " +ps1.getProfessionalCategoryId()+ "\n" +
                    "    }\n" +

                    "  }\n" +
                    "}";


        System.out.println(jsonUpdateClosed);

        RequestEntity<String> entityUpdateProfessional =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdateClosed);

        ResponseEntity<OrderResponseBody> exchangeUpdateProfessionalVote = testRestTemplate
                .exchange(entityUpdateProfessional, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdateProfessionalVote);
        Assert.assertNotNull(exchangeUpdateProfessionalVote.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateProfessionalVote.getStatusCode());

        Order orderUpdateProfessionalVote =  orderRepository.findOne(createdOrder.getIdOrder());

        //Assert.assertEquals(Order.Status.CLOSED, orderUpdateAccepted.getStatus());
        Assert.assertEquals(Order.Status.READY2CHARGE, orderUpdateProfessionalVote.getStatus());

        User professionalUser = orderUpdateProfessionalVote.getProfessionalCategory()
                .getProfessional().getUser();

        float professionalVote = voteService.getUserEvaluation(professionalUser);

        Assert.assertNotNull(professionalVote);
        Assert.assertTrue((float)2.0 == professionalVote);
    }

    private ProfessionalCategory buildFakeProfessionalCategory() {

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professionalRepository.save(professional);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(priceRule);

        return ps1;
    }

    /*
     * RNF119
     */
    @Test
    public void updateScheduledOrderOkToOrderStatusScheduledBadRequest() throws URISyntaxException {

        createScheduledOrderOk();
        Order o1 = orderRestultFrom_createScheduledOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : " + o1.getIdOrder() + ",\n" +
                "    \"status\" : \"" + Order.Status.SCHEDULED + "\", \n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleId\" : "+ o1.getScheduleId().getScheduleId() +",\n" +
                "      \"scheduleStart\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 05, 12, 10, 0)).getTime() +"\",\n" +
                "      \"scheduleEnd\" : null\n" + // Foracara o erro
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate = RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchangeUpdate.getStatusCode());

        List<Order> orderList = exchangeUpdate.getBody().getOrderList();

        if(!orderList.isEmpty()){
            Assert.assertEquals(Order.Status.SCHEDULED, orderList.get(0).getStatus());
            orderRestultFrom_updateScheduledOrderOkToScheduled = orderList.get(0);

        }

    }

    //METODO PARA FACILITAR OS TESTES E EVETIAR TANTA REPETICAO DE CODIGO
    public String getOrderCreateJson(ProfessionalCategory pc, Customer customer) {

        String jsonCreate = OrderJsonHelper.buildJsonCreateScheduledOrder(
                customer,
                pc,
                pc.getPriceRuleList().stream().findFirst().get(),
                Payment.Type.CASH,
                Timestamp.valueOf(now().plusDays(1)).getTime() // Marcado pra amanha
        );


        return jsonCreate;

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

        ResponseEntity<OrderResponseBody> exchangeUpdate = testRestTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        return exchangeUpdate;
    }

    @Test
    public void testCreateToOrderStatusInProgressToOtherSchedule() throws IOException, URISyntaxException {

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// SETTING UP ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c1);

        Customer c2 = CustomerControllerTests.createFakeCustomer();
        customerRepository.save(c2);

        ProfessionalCategory ps1 = buildFakeProfessionalCategory();
        professionalCategoryRepository.save(ps1);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /////// RUNNING THE TEST //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(ps1, c1);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());

        Assert.assertEquals(Order.Status.OPEN, exchangeCreate.getBody().getOrderList().get(0).getStatus());

        Order createdOrder = exchangeCreate.getBody().getOrderList().get(0);
        //-------

        //ATUALIZAMOS ORDER PARA IN_PROGRESS PARA, POSTERIORMENTE, TENTAR CRIAR NOVO ORDER PARA O MESMO PROFESSIONAL
        ResponseEntity<OrderResponseBody> exchangeUpdateAccepted = this.updateOrderStatus(
                createdOrder.getIdOrder(), Order.Status.INPROGRESS);

        Assert.assertNotNull(exchangeUpdateAccepted);
        Assert.assertNotNull(exchangeUpdateAccepted.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateAccepted.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdateAccepted.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.INPROGRESS, orderUpdateAccepted.getStatus());
        //-------

        //TENTAMOS CRIAR NOVO ORDER PARA O MESMO PROFESSIONAL ENQUANTO ELE JA TEM UM ORDER COM STATUS ACCEPTED
        String jsonCreate2 = this.getOrderCreateJson(ps1, c2);

        RequestEntity<String> entity2 =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate2);

        ResponseEntity<OrderResponseBody> exchangeCreate2 = this.updateOrderStatus(
                createdOrder.getIdOrder(), Order.Status.SCHEDULED);

        Assert.assertNotNull(exchangeCreate2);
        Assert.assertEquals(HttpStatus.OK, exchangeCreate2.getStatusCode());
        //-------

    }

    /**
     * RNF127 - https://trello.com/c/19xzp68K
     * Customer nao tem cartao de credito cadastrado mas de alguma forma chegou Order pra ser paga no cartao de credito.
     * Retornamos erro.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testGivenUserWithoutDoCreateOrderWithPaymentTypeCcThenReturnUnsupportedPaymentType() throws IOException, URISyntaxException {

        // Criando usuario sem cartao de credito.
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testGivenUserWithoutDoCreateOrderWithPaymentTypeCcThenReturnUnsupportedPaymentType-customer1");
        c1.getUser().setEmail("testGivenUserWithoutDoCreateOrderWithPaymentTypeCcThenReturnUnsupportedPaymentType-customer1@email.com");
        c1.getUser().setPassword("123");
        c1.setCpf("123.456.789-02");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testGivenUserWithoutDoCreateOrderWithPaymentTypeCcThenReturnUnsupportedPaymentType-professional");
        professional.getUser().setEmail("testGivenUserWithoutDoCreateOrderWithPaymentTypeCcThenReturnUnsupportedPaymentType-professional@email.com");
        professional.getUser().setPassword("123");
        professional.setCnpj("123.456.789-03");

        customerRepository.save(c1);

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = buildProfessionalCateogry(professional, priceRule);

        //-------

        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = OrderJsonHelper.buildJsonCreateScheduledOrder(
                c1,
                ps1,
                priceRule,
                Payment.Type.CC,
                Timestamp.valueOf(now().plusDays(1)).getTime() // Marcado pra amanha
        );

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = testRestTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchangeCreate.getStatusCode());
        Assert.assertEquals(ErrorCode.INVALID_PAYMENT_TYPE, exchangeCreate.getBody().getErrorCode());
    }

    private ProfessionalCategory buildProfessionalCateogry(Professional professional, PriceRule priceRule) {

        professionalRepository.save(professional);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());


        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(priceRule);

        // Atualizando associando o Profeissional ao Servico
        return professionalCategoryRepository.save(ps1);
    }

    private Optional<RetornoTransacao> getOptionalFakeRetornoTransacao(int statusTransacao) {
        RetornoTransacao retornoTransacao = new RetornoTransacao();
        retornoTransacao.setNumeroTransacao(3);
        retornoTransacao.setCodigoEstabelecimento("1501698887865");
        retornoTransacao.setCodigoFormaPagamento(170);
        retornoTransacao.setValor(100);
        retornoTransacao.setValorDesconto(0);
        retornoTransacao.setParcelas(1);
        retornoTransacao.setStatusTransacao(statusTransacao);
        retornoTransacao.setAutorizacao("20170808124436912");
        retornoTransacao.setCodigoTransacaoOperadora("0");
        retornoTransacao.setDataAprovacaoOperadora("2017-08-11 04:56:25");
        retornoTransacao.setNumeroComprovanteVenda("0808124434526");
        retornoTransacao.setNsu("4436912");
        retornoTransacao.setUrlPagamento("1502206705884f8a21ff8-db8f-4c7d-a779-8f35f35cfd71");

        List<String> cartaoUtilizado = new ArrayList<>();
        cartaoUtilizado.add("000000******0001");
        retornoTransacao.setCartoesUtilizados(cartaoUtilizado);

        return Optional.of(retornoTransacao);

    }
}
