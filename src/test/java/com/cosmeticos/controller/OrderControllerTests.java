package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
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
import java.text.ParseException;

/**
 * Created by diego.MindTek on 26/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTests {

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

    @Before
    public void setup()
    {
        Service service = new Service();
        service.setCategory("PEDICURE");
        serviceRepository.save(service);
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
    public void testUpdateOK() throws IOException {

        Order s1 = new Order();
        s1.setIdOrder(1L);
        s1.setStatus(Order.Status.ABORTED.ordinal());

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
        Assert.assertEquals((int) Order.Status.ABORTED.ordinal(), (int)exchange.getBody().getOrderList().get(0).getStatus());
    }

    //TODO - FALTA FINALIZAR, PROVAVELMENTE SERÁ NECESSÁRIO ALTERAR A ENTIDADE
    @Test
    public void createScheduledOrderOk() throws URISyntaxException {

        /*
         Preparacao do teste:
         Criamos um Customer qualquer. Criamos um Profissional qualquer e o associamos a um Service.
         Salvamos tudo no banco.
          */
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

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals((int) Order.Status.CREATED.ordinal(), (int)exchange.getBody().getOrderList().get(0).getStatus());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getScheduleId());


    }


    @Test public void testaddwallet() throws URISyntaxException {

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
}
