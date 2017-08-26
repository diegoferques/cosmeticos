package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OrderService;
import com.cosmeticos.service.VoteService;
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
import java.util.List;

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
    private TestRestTemplate restTemplate;

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

    @Before
    public void setup()
    {
        Category service = serviceRepository.findByName("PEDICURE");

        if(service == null) {
            service = new Category();
            service.setName("PEDICURE");
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
         PRE-CONDICOES para o teste:
         Criamos um Customer qualquer. Criamos um Profissional qualquer e o associamos a um Service.
         Salvamos tudo no banco.
          */

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testUpdateOK-cliente");
        c1.getUser().setEmail("testUpdateOK-cliente@bol");
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testUpdateOK-professional");
        professional.getUser().setEmail("testUpdateOK-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);


        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);

        professional.getProfessionalCategoryCollection().add(ps1);

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
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 05, 12, 10, 0)).getTime() +"\",\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+service.getIdCategory()+"\n" +
                "      },\n" +
                "     \"priceRule\": {\n" +
                "           \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
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
                restTemplate.exchange( //
                        "/orders", //
                        HttpMethod.PUT, //
                        new HttpEntity(or), // Body
                        OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        
        Assert.assertEquals( Order.Status.CANCELLED, exchange.getBody().getOrderList().get(0).getStatus()); */

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
        ps1.getPriceRule().add(priceRule);
        priceRule.setProfessionalCategory(ps1);

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
        String json = "{\n" +
               "  \"order\" : {\n" +
               "    \"date\" : 1498324200000,\n" +
               "    \"status\" : 0,\n" +
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"scheduleId\" : {\n" +
               "      \"scheduleStart\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 05, 12, 10, 0)).getTime() +"\",\n" +
               "      \"status\" : \"ACTIVE\",\n" +
               "      \"orderCollection\" : [ ]\n" +
               "    },\n" +
               "    \"professionalCategory\" : {\n" +
               "      \"category\" : {\n" +
               "        \"idCategory\" : "+service.getIdCategory()+",\n" +
               "        \"name\" : \"MASSAGISTA\"\n" +
               "      },\n" +
                "      \"priceRule\": {\n" +
                "            \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
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

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);

        professional.getProfessionalCategoryCollection().add(ps1);

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
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+service.getIdCategory()+"\n" +
                "      },\n" +
                "       \"priceRule\": {\n" +
                "            \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
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

        RequestEntity<String> entity = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        restTemplate.exchange(entity, OrderResponseBody.class);

        // Antes do 1o request a carteira tem que estar vazia.
        // //Apos o 2o request a carteira ainda tem q estar vazia.
        Assert.assertTrue(professional.getWallet() == null || professional.getWallet().getCustomers().isEmpty());

        json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+service.getIdCategory()+"\n" +
                "      },\n" +
                "       \"priceRule\": {\n" +
                "            \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
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

        RequestEntity<String> entityPost2 = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        restTemplate.exchange(entityPost2, OrderResponseBody.class);

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

        professional.getProfessionalCategoryCollection().add(ps1);

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
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"status\" : 0,\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+service.getIdCategory()+"\n" +
                "      },\n" +
                "       \"priceRule\": {\n" +
                "            \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
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
        Assert.assertEquals(Order.Status.CLOSED,
                exchangePut.getBody().getOrderList().get(0).getStatus());
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


        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);

        professional.getProfessionalCategoryCollection().add(ps1);

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
                "    \"status\" : \""+Order.Status.OPEN+"\",\n" +
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+service.getIdCategory()+"\n" +
                "      },\n" +
                "       \"priceRule\": {\n" +
                "            \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
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

        ResponseEntity<OrderResponseBody> exchangePut = restTemplate
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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
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

        professional.getProfessionalCategoryCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        /************ FIM DAS PRE_CONDICOES **********************************/


        String json = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+service.getIdCategory()+",\n" +
                "        \"name\" : \"MASSAGISTA\"\n" +
                "      },\n" +
                "        \"priceRule\": {\n" +
                "            \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());

        //orderRestultFrom_updateOrderOkToScheduled = exchangeUpdate.getBody().getOrderList().get(0);

    }

    @Test
    public void updateScheduledOrderOkToOrderStatusScheduled() throws URISyntaxException {

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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        //TODO - FINALIZAR OS ASSERTS
        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        //Assert.assertNotNull(exchangeUpdate.getBody().getOrderList().get(0).getScheduleId());
        Assert.assertEquals(Order.Status.SCHEDULED, exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        orderRestultFrom_updateScheduledOrderOkToScheduled = exchangeUpdate.getBody().getOrderList().get(0);
    }

    @Test
    public void updateCreatedOrderOkToStatusFinished() throws URISyntaxException {

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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
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

        professional.getProfessionalCategoryCollection().add(ps1);

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
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+service.getIdCategory()+",\n" +
                "        \"name\" : \"MASSAGISTA\"\n" +
                "      },\n" +
                "      \"priceRule\": {\n" +
                "           \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
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

        ResponseEntity<OrderResponseBody> exchangePut = restTemplate
                .exchange(entityPut, OrderResponseBody.class);

        Assert.assertNotNull(exchangePut);
        Assert.assertEquals(HttpStatus.OK, exchangePut.getStatusCode());

        Assert.assertEquals(Order.Status.CANCELLED,
                exchangePut.getBody().getOrderList().get(0).getStatus());

    }

    //TESTANDO O RETORNO DE ORDER SEM EXIBIR OS STATUS CANCELLED
    @Test
    public void testFindByStatusNotCancelled() throws ParseException, URISyntaxException {

        //SETAMOS E SALVAMOS O PROFESSIONAL, CUSTOMER 1 E CUSTOMER 2 QUE QUE VAMOS UTILIZAR NESTE TESTE
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testFindByStatusNotCancelled-customer1");
        c1.getUser().setEmail("testFindByStatusNotCancelled-customer1@email.com");
        c1.getUser().setPassword("123");
        c1.setCpf("123.456.789-05");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testFindByStatusNotCancelled-professional");
        professional.getUser().setEmail("testFindByStatusNotCancelled-professional@email.com");
        professional.getUser().setPassword("123");
        professional.setCnpj("123.456.789-06");

        customerRepository.save(c1);
        professionalRepository.save(professional);


        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);

        professional.getProfessionalCategoryCollection().add(ps1);

        //Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);
        //-------

        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(service, professional, c1, priceRule);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = restTemplate
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
            Assert.assertNotEquals(Order.Status.CANCELLED, order.getStatus());
            Assert.assertNotEquals(Order.Status.CLOSED, order.getStatus());
        }

    }

    //TESTANDO O RETORNO DE ORDER SEM EXIBIR OS STATUS CLOSED
    @Test
    public void testFindByStatusNotClosed() throws ParseException, URISyntaxException {

        //SETAMOS E SALVAMOS O PROFESSIONAL, CUSTOMER 1 E CUSTOMER 2 QUE QUE VAMOS UTILIZAR NESTE TESTE
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testFindByStatusNotClosed-customer1");
        c1.getUser().setEmail("testFindByStatusNotClosed-customer1@email.com");
        c1.getUser().setPassword("123");
        c1.setCpf("123.456.789-05");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testFindByStatusNotClosed-professional");
        professional.getUser().setEmail("testFindByStatusNotClosed-professional@email.com");
        professional.getUser().setPassword("123");
        professional.setCnpj("123.456.789-06");

        customerRepository.save(c1);
        professionalRepository.save(professional);


        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);

        professional.getProfessionalCategoryCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);
        //-------

        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(service, professional, c1, priceRule);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = restTemplate
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
            Assert.assertNotEquals(Order.Status.CANCELLED, order.getStatus());
            Assert.assertNotEquals(Order.Status.CLOSED, order.getStatus());
        }

    }

    @Test
    public void testCreateToConflictedOrderErrorCausedByOrderStatusAccepted() throws IOException, URISyntaxException {

        //SETAMOS E SALVAMOS O PROFESSIONAL, CUSTOMER 1 E CUSTOMER 2 QUE QUE VAMOS UTILIZAR NESTE TESTE
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testConflictedOrder-customer1");
        c1.getUser().setEmail("testConflictedOrder-customer1@email.com");
        c1.getUser().setPassword("123");
        c1.setCpf("123.456.789-01");

        Customer c2 = CustomerControllerTests.createFakeCustomer();
        c2.getUser().setUsername("testConflictedOrder-customer2");
        c2.getUser().setEmail("testConflictedOrder-customer2@email.com");
        c2.getUser().setPassword("123");
        c2.setCpf("123.456.789-02");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testConflictedOrder-professional");
        professional.getUser().setEmail("testConflictedOrder-professional@email.com");
        professional.getUser().setPassword("123");
        professional.setCnpj("123.456.789-03");

        customerRepository.save(c1);
        customerRepository.save(c2);
        professionalRepository.save(professional);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);

        professional.getProfessionalCategoryCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);
        //-------

        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(service, professional, c1, priceRule);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);


        ResponseEntity<OrderResponseBody> exchangeCreate = restTemplate
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
        String jsonCreate2 = this.getOrderCreateJson(service, professional, c2, priceRule);

        RequestEntity<String> entity2 =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate2);

        ResponseEntity<OrderResponseBody> exchangeCreate2 = restTemplate
                .exchange(entity2, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate2);
        Assert.assertEquals(HttpStatus.CONFLICT, exchangeCreate2.getStatusCode());
        //-------

    }

    @Test
    public void testParaTravarUpdateStatusDeExpiredParaOpen() throws IOException, URISyntaxException {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testUpdateStatusDeExpiredParaOpen-cliente");
        c1.getUser().setEmail("testUpdateStatusDeExpiredParaOpen-cliente@bol");
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testUpdateStatusDeExpiredParaOpen-professional");
        professional.getUser().setEmail("testUpdateStatusDeExpiredParaOpen-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);


        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);

        professional.getProfessionalCategoryCollection().add(ps1);

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
                "    \"status\" : \"" + Order.Status.OPEN + "\",\n" +
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : 1499706000000,\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : " + service.getIdCategory() + ",\n" +
                "        \"name\" : \"MASSAGISTA\"\n" +
                "      },\n" +
                "      \"priceRule\": {\n" +
                "           \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
                "      \"professional\" : {\n" +
                "        \"idProfessional\" : " + professional.getIdProfessional() + ",\n" +
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
                "      \"idCustomer\" : " + c1.getIdCustomer() + ",\n" +
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

        RequestEntity<String> entity = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<OrderResponseBody> exchange = restTemplate
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

        ResponseEntity<OrderResponseBody> exchangePut = restTemplate
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

        ResponseEntity<OrderResponseBody> exchangePut2 = restTemplate
                .exchange(entityPut2, OrderResponseBody.class);

        Assert.assertNotNull(exchangePut2);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchangePut2.getStatusCode());

    }

    @Test
    public void testCreateToConflictedOrderErrorCausedByOrderStatusInProgress() throws IOException, URISyntaxException {

        //SETAMOS E SALVAMOS O PROFESSIONAL, CUSTOMER 1 E CUSTOMER 2 QUE QUE VAMOS UTILIZAR NESTE TESTE
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testConflictedOrderUpdate-customer1");
        c1.getUser().setEmail("testConflictedOrderUpdate-customer1@email.com");
        c1.getUser().setPassword("123");
        c1.setCpf("123.456.789-01");

        Customer c2 = CustomerControllerTests.createFakeCustomer();
        c2.getUser().setUsername("testConflictedOrderUpdate-customer2");
        c2.getUser().setEmail("testConflictedOrderUpdate-customer2@email.com");
        c2.getUser().setPassword("123");
        c2.setCpf("123.456.789-02");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testConflictedOrderUpdate-professional");
        professional.getUser().setEmail("testConflictedOrderUpdate-professional@email.com");
        professional.getUser().setPassword("123");
        professional.setCnpj("123.456.789-03");

        customerRepository.save(c1);
        customerRepository.save(c2);
        professionalRepository.save(professional);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);

        professional.getProfessionalCategoryCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);
        //-------

        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate = this.getOrderCreateJson(service, professional, c1, priceRule);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = restTemplate
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
        String jsonCreate2 = this.getOrderCreateJson(service, professional, c2, priceRule);

        RequestEntity<String> entity2 =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate2);

        ResponseEntity<OrderResponseBody> exchangeCreate2 = restTemplate
                .exchange(entity2, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate2);
        Assert.assertEquals(HttpStatus.CONFLICT, exchangeCreate2.getStatusCode());
        //-------

    }

    @Test
    public void testOrderClosedAndVote() throws IOException, URISyntaxException {

        //SETAMOS E SALVAMOS O PROFESSIONAL E CUSTOMER QUE VAMOS UTILIZAR NESTE TESTE
        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testOrderClosedAndVote-customer1");
        c1.getUser().setEmail("testOrderClosedAndVote-customer1@email.com");
        c1.getUser().setPassword("123");
        c1.setCpf("123.984.789-01");
        c1.setNameCustomer("testOrderClosedAndVote Customer");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testOrderClosedAndVote-professional");
        professional.getUser().setEmail("testOrderClosedAndVote-professional@email.com");
        professional.getUser().setPassword("123");
        professional.setCnpj("123.984.789-03");
        professional.setNameProfessional("testOrderClosedAndVote Professional");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName("RULE");
        priceRule.setPrice(7600L);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);

        professional.getProfessionalCategoryCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);
        //-------

        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA CLOSED E ENVIARMOS O VOTO
        String jsonCreate = this.getOrderCreateJson(service, professional, c1, priceRule);
        System.out.println(jsonCreate);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<OrderResponseBody> exchangeCreate = restTemplate
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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertNotNull(exchangeUpdate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Order orderUpdateAccepted = exchangeUpdate.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.CLOSED, orderUpdateAccepted.getStatus());

        float vote = voteService.getProfessionalEvaluation(professional);
        Assert.assertNotNull(vote);
        Assert.assertTrue((float)3.0 == vote);
        //-------
    }
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
                "      \"scheduleEnd\" : null\n" +
                "    }" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate = RequestEntity
                .put(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
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
    public String getOrderCreateJson(Category category, Professional professional, Customer customer, PriceRule priceRule) {

        String jsonCreate = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"paymentType\" : \"CASH\",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 05, 12, 10, 0)).getTime() +"\",\n" +
                "      \"status\" : \"ACTIVE\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+ category.getIdCategory() +",\n" +
                "        \"name\" : \"PEDICURE\"\n" +
                "      },\n" +
                "       \"priceRule\": {\n" +
                "            \"idPrice\": "+priceRule.getIdPrice()+",\n" +
                "            \"name\": \""+priceRule.getName()+"\",\n" +
                "            \"price\": "+priceRule.getPrice()+"\n" +
                "          },\n" +
                "      \"professional\" : {\n" +
                "        \"idProfessional\" : "+ professional.getIdProfessional() +",\n" +
                "        \"nameProfessional\" : \""+ professional.getNameProfessional() +"\",\n" +
                "        \"cnpj\" : \""+ professional.getIdProfessional() +"\",\n" +
                "        \"genre\" : \"F\",\n" +
                "        \"birthDate\" : 688010400000,\n" +
                "        \"cellPhone\" : \"(21) 99887-7665\",\n" +
                "        \"dateRegister\" : 1499195092952,\n" +
                "        \"status\" : 0\n" +
                "      }\n" +
                "    },\n" +
                "    \"idLocation\" : null,\n" +
                "    \"idCustomer\" : {\n" +
                "      \"idCustomer\" : "+ customer.getIdCustomer() +",\n" +
                "      \"nameCustomer\" : \""+ customer.getNameCustomer() +"\",\n" +
                "      \"cpf\" : \""+ customer.getCpf() +"\",\n" +
                "      \"genre\" : \"F\",\n" +
                "      \"birthDate\" : 688010400000,\n" +
                "      \"cellPhone\" : \"(21) 99887-7665\",\n" +
                "      \"dateRegister\" : 1499195092952,\n" +
                "      \"status\" : 0,\n" +
                "      \"idLogin\" : {\n" +
                "        \"username\" : \""+ customer.getUser().getUsername() +"\",\n" +
                "        \"email\" : \""+ customer.getUser().getEmail() +"\",\n" +
                "        \"password\" : \""+ customer.getUser().getPassword() +"\",\n" +
                "        \"personType\":\"FÍSICA\",\n" +
                "        \"sourceApp\" : \"facebook\"\n" +
                "      },\n" +
                "      \"idAddress\" : null\n" +
                "    }\n" +
                "  }\n" +
                "}";

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

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        return exchangeUpdate;
    }

}
