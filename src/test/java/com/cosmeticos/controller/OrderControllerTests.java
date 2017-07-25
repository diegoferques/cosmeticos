package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OrderService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;

/**
 * Created by diego.MindTek on 26/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTests {

    private Order orderRestultFrom_createScheduledOrderOk = null;
    private Order orderRestultFrom_createOrderOk = null;
    private Order orderRestultFrom_updateOrderOkToScheduled = null;
    private Order orderRestultFrom_updateScheduledOrderOkToScheduled = null;
    private Order orderRestultFrom_updateScheduledOrderToInactive = null;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

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

    @Before
    public void setup()
    {
        Service service = serviceRepository.findByCategory("PEDICURE");

        if(service == null) {
            service = new Service();
            service.setCategory("PEDICURE");
            serviceRepository.save(service);
        }
    }

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
    public void testUpdateOK() throws IOException, URISyntaxException {

        /*
        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : 1,\n" +
                "    \"status\" : "+ Order.Status.ABORTED.ordinal() +"\n" +
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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertEquals((int) Order.Status.ABORTED.ordinal(), (int)exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        */


        Order s1 = new Order();
        s1.setIdOrder(1L);
        s1.setStatus(Order.Status.CANCELLED.ordinal());

        OrderRequestBody or = new OrderRequestBody();
        or.setOrder(s1);

        final ResponseEntity<OrderResponseBody> exchange = //
                restTemplate.exchange( //
                        "/orders", //
                        HttpMethod.PUT, //
                        new HttpEntity(or), // Body
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals((int) Order.Status.CANCELLED.ordinal(), (int)exchange.getBody().getOrderList().get(0).getStatus());

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


        Service service = serviceRepository.findByCategory("PEDICURE");

        ProfessionalServices ps1 = new ProfessionalServices(professional, service);

        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        /************ FIM DAS PRE_CONDICOES **********************************/



        /*
         O teste comeca aqui:
         Fazemos um json com informacoes que batem com o que foi inserido acima. Nossa pre-condicao pede que 3
         objetos estejam persistidos no banco. Usamos os IDs desses caras nesse json abaixo pq se fosse um servico em
         producao as pre-condicoes seriam as mesmas e o json abaixo seria igual.
          */
        String json = "{\n" +
               "  \"order\" : {\n" +
               "    \"date\" : 1498324200000,\n" +
               "    \"status\" : 0,\n" +
               "    \"scheduleId\" : {\n" +
               "      \"scheduleDate\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 05, 12, 10, 0)).getTime() +"\",\n" +
               "      \"status\" : \"ACTIVE\",\n" +
               "      \"orderCollection\" : [ ]\n" +
               "    },\n" +
               "    \"professionalServices\" : {\n" +
               "      \"service\" : {\n" +
               "        \"idService\" : "+service.getIdService()+",\n" +
               "        \"category\" : \"MASSAGISTA\"\n" +
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
               "        \"sourceApp\" : \"facebook\"\n" +
               "      },\n" +
               "      \"idAddress\" : null\n" +
               "    }\n" +
               "  }\n" +
               "}";

        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<OrderResponseBody> exchange = restTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals((int) Order.Status.CREATED.ordinal(), (int)exchange.getBody().getOrderList().get(0).getStatus());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalServices());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalServices().getService());
        Assert.assertEquals("PEDICURE",
                exchange.getBody().getOrderList().get(0).getProfessionalServices().getService().getCategory());

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

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Service service = serviceRepository.findByCategory("PEDICURE");

        ProfessionalServices ps1 = new ProfessionalServices(professional, service);

        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        /*
         O teste comeca aqui:
         Fazemos um json com informacoes que batem com o que foi inserido acima. Um usuario que existe no banco e
         um profissional associado a um servico que existirao no banco.
          */


        Assert.assertTrue(professional.getWallet() == null || professional.getWallet().getCustomers().isEmpty());

        String json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleDate\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"service\" : {\n" +
                "        \"idService\" : "+service.getIdService()+",\n" +
                "        \"category\" : \"MASSAGISTA\"\n" +
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

        // Antes do 1o request a carteira tem que estar vazia.
        // //Apos o 2o request a carteira ainda tem q estar vazia.
        Assert.assertTrue(professional.getWallet() == null || professional.getWallet().getCustomers().isEmpty());

        json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleDate\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"service\" : {\n" +
                "        \"idService\" : "+service.getIdService()+",\n" +
                "        \"category\" : \"MASSAGISTA\"\n" +
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
                "        \"sourceApp\" : \"facebook\"\n" +
                "      },\n" +
                "      \"idAddress\" : null\n" +
                "    }\n" +
                "  }\n" +
                "}";

         entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

         exchange = restTemplate
                .exchange(entity, OrderResponseBody.class);

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

        Service service = serviceRepository.findByCategory("PEDICURE");

        ProfessionalServices ps1 = new ProfessionalServices(professional, service);

        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        /*
         O teste comeca aqui:
         Fazemos um json com informacoes que batem com o que foi inserido acima. Um usuario que existe no banco e
         um profissional associado a um servico que existirao no banco.
          */
        String json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleDate\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"service\" : {\n" +
                "        \"idService\" : "+service.getIdService()+",\n" +
                "        \"category\" : \"MASSAGISTA\"\n" +
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

        ResponseEntity<OrderResponseBody> exchangePut = restTemplate
                .exchange(entityPut, OrderResponseBody.class);

        Assert.assertNotNull(exchangePut);
        Assert.assertEquals(HttpStatus.OK, exchangePut.getStatusCode());
        Assert.assertEquals((int) Order.Status.CLOSED.ordinal(),
                (int)exchangePut.getBody().getOrderList().get(0).getStatus());
    }

    @Test
    public void testParaTravarUpdateDeStatus() throws URISyntaxException {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testParaTravarUpdateDeStatus-cliente");
        c1.getUser().setEmail("testParaTravarUpdateDeStatus-cliente@bol");
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testParaTravarUpdateDeStatus-professional");
        professional.getUser().setEmail("testParaTravarUpdateDeStatus-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Service service = serviceRepository.findByCategory("PEDICURE");

        ProfessionalServices ps1 = new ProfessionalServices(professional, service);

        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        /*
         O teste comeca aqui:
         Fazemos um json com informacoes que batem com o que foi inserido acima. Um usuario que existe no banco e
         um profissional associado a um servico que existirao no banco.
          */
        String json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleDate\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"service\" : {\n" +
                "        \"idService\" : "+service.getIdService()+",\n" +
                "        \"category\" : \"MASSAGISTA\"\n" +
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

        ResponseEntity<OrderResponseBody> exchangePut = restTemplate
                .exchange(entityPut, OrderResponseBody.class);

        Assert.assertNotNull(exchangePut);
        Assert.assertEquals(HttpStatus.OK, exchangePut.getStatusCode());
        Assert.assertEquals((int) Order.Status.CLOSED.ordinal(),
                (int)exchangePut.getBody().getOrderList().get(0).getStatus());

        String jsonUpdate2 = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+newOrder.getIdOrder()+",\n" +
                "    \"status\" : 5\n" +
                "  }\n" +
                "}";

        RequestEntity<String> entityPut2 =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate2);

        ResponseEntity<OrderResponseBody> exchangePut2 = restTemplate
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
                "      \"scheduleDate\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 10, 30, 0)).getTime()  +"\"\n" +
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertEquals(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 10, 30, 0)).getTime(), exchangeUpdate.getBody().getOrderList().get(0).getScheduleId().getScheduleDate().getTime());

    }

    @Test
    public void updateScheduledOrderToInactive() throws URISyntaxException {

        createScheduledOrderOk();

        Order o1 = orderRestultFrom_createScheduledOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleId\" : "+ o1.getScheduleId().getScheduleId() +",\n" +
                "      \"status\" : \""+ Schedule.Status.INACTIVE +"\"\n" +
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertEquals(Schedule.Status.INACTIVE, exchangeUpdate.getBody().getOrderList().get(0).getScheduleId().getStatus());

        orderRestultFrom_updateScheduledOrderToInactive = exchangeUpdate.getBody().getOrderList().get(0);
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

        Service service = serviceRepository.findByCategory("PEDICURE");

        ProfessionalServices ps1 = new ProfessionalServices(professional, service);

        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        /************ FIM DAS PRE_CONDICOES **********************************/


        String json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"professionalServices\" : {\n" +
                "      \"service\" : {\n" +
                "        \"idService\" : "+service.getIdService()+",\n" +
                "        \"category\" : \"MASSAGISTA\"\n" +
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
        Assert.assertEquals((int) Order.Status.CREATED.ordinal(), (int)exchange.getBody().getOrderList().get(0).getStatus());
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
                "      \"scheduleDate\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 22, 30, 0)).getTime()  +"\",\n" +
                "      \"status\" : \""+ Schedule.Status.ACTIVE +"\"\n" +
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertEquals(Schedule.Status.ACTIVE, exchangeUpdate.getBody().getOrderList().get(0).getScheduleId().getStatus());

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
                "      \"scheduleId\" : "+ o1.getScheduleId().getScheduleId() +",\n" +
                //"      \"scheduleDate\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 22, 30, 0)).getTime()  +"\",\n" +
                "      \"status\" : \""+ Schedule.Status.DENIED +"\"\n" +
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertEquals(Schedule.Status.DENIED, exchangeUpdate.getBody().getOrderList().get(0).getScheduleId().getStatus());

        //orderRestultFrom_updateOrderOkToScheduled = exchangeUpdate.getBody().getOrderList().get(0);

    }

    @Test
    public void updateScheduledOrderOkToOrderStatusScheduled() throws URISyntaxException {

        createScheduledOrderOk();
        Order o1 = orderRestultFrom_createScheduledOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"status\" : "+ Order.Status.SCHEDULED.ordinal() +"\n" +
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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        //Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertEquals(Order.Status.SCHEDULED.ordinal(),(int) exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        orderRestultFrom_updateScheduledOrderOkToScheduled = exchangeUpdate.getBody().getOrderList().get(0);
    }

    @Test
    public void updateCreatedOrderOkToStatusFinished() throws URISyntaxException {

        updateScheduledOrderOkToOrderStatusScheduled();

        Order o1 = orderRestultFrom_updateScheduledOrderOkToScheduled;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"status\" : "+ Order.Status.EXECUTED.ordinal() +"\n" +
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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertEquals(Order.Status.EXECUTED.ordinal(), (int)exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        //orderRestultFrom_updateOrderOkToScheduled = exchangeUpdate.getBody().getOrderList().get(0);
    }

    @Test
    public void updatedTestpenaltyStatusAborted() throws URISyntaxException {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        Professional professional = ProfessionalControllerTests.createFakeProfessional();

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Service service = serviceRepository.findByCategory("PEDICURE");

        ProfessionalServices ps1 = new ProfessionalServices(professional, service);

        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        /*
         O teste comeca aqui:
         Fazemos um json com informacoes que batem com o que foi inserido acima. Um usuario que existe no banco e
         um profissional associado a um servico que existirao no banco.
          */
        String json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleDate\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"service\" : {\n" +
                "        \"idService\" : "+service.getIdService()+",\n" +
                "        \"category\" : \"MASSAGISTA\"\n" +
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

        Order newOrder = exchange.getBody().getOrderList().get(0);


        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+newOrder.getIdOrder()+",\n" +
                "    \"status\" : 2\n" +
                "  }\n" +
                "}";

        RequestEntity<String> entityPut =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangePut = restTemplate
                .exchange(entityPut, OrderResponseBody.class);

        Assert.assertNotNull(exchangePut);
        Assert.assertEquals(HttpStatus.OK, exchangePut.getStatusCode());
        Assert.assertEquals((int) Order.Status.CANCELLED.ordinal(),
                (int)exchangePut.getBody().getOrderList().get(0).getStatus());

    }

    //TESTANDO O RETORNO DE ORDER SEM EXIBIR OS STATUS CANCELLED
    @Test
    public void testFindByStatusNotCancelled() throws ParseException, URISyntaxException {

        createOrderOk();

        Order o1 = orderRestultFrom_createOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"status\" : "+ Order.Status.CANCELLED.ordinal() +"\n" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        //ABAIXO GARANTIMOS QUE REALMENTE TEMOS NO BANCO UM ORDER COM STATUS CLOSED
        Assert.assertEquals(Order.Status.CANCELLED.ordinal(), (int)exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        final ResponseEntity<OrderResponseBody> exchange = //
                restTemplate.exchange( //
                        "/orders", //
                        HttpMethod.GET, //
                        null,
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertNotNull(exchange.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        for (Order order : exchange.getBody().getOrderList()) {
            //ABAIXO VERIFICAMOS SE NENHUM STATUS DOS PEDISOS SAO CANCELLED OU CLOSED
            Assert.assertNotEquals(Order.Status.CANCELLED.ordinal(), (int)order.getStatus());
            Assert.assertNotEquals(Order.Status.CLOSED.ordinal(), (int)order.getStatus());
        }

    }

    //TESTANDO O RETORNO DE ORDER SEM EXIBIR OS STATUS CLOSED
    @Test
    public void testFindByStatusNotClosed() throws ParseException, URISyntaxException {

        createOrderOk();

        Order o1 = orderRestultFrom_createOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ o1.getIdOrder() +",\n" +
                "    \"status\" : "+ Order.Status.CLOSED.ordinal() +"\n" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        //ABAIXO GARANTIMOS QUE REALMENTE TEMOS NO BANCO UM ORDER COM STATUS CLOSED
        Assert.assertEquals(Order.Status.CLOSED.ordinal(), (int)exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        final ResponseEntity<OrderResponseBody> exchange = //
                restTemplate.exchange( //
                        "/orders", //
                        HttpMethod.GET, //
                        null,
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertNotNull(exchange.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        for (Order order : exchange.getBody().getOrderList()) {
            //ABAIXO VERIFICAMOS SE NENHUM STATUS DOS PEDISOS SAO CANCELLED OU CLOSED
            Assert.assertNotEquals(Order.Status.CANCELLED.ordinal(), (int)order.getStatus());
            Assert.assertNotEquals(Order.Status.CLOSED.ordinal(), (int)order.getStatus());
        }

    }


}
