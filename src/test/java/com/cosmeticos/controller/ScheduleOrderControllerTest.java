package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.CategoryRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

/**
 * Created by Vinicius on 04/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScheduleOrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Order orderRestultFrom_createScheduledOrderOk = null;


    @Test
    public void scheduledOrderOk() throws URISyntaxException {

/*
         PRE-CONDICOES para o teste:
         Criamos um Customer qualquer. Criamos um Profissional qualquer e o associamos a um Service.
         Salvamos tudo no banco.
          */

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-cliente_Schedule");
        c1.getUser().setEmail(System.nanoTime() + "-cliente@bol_Schedule");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime() + "-professional_Schedule");
        professional.getUser().setEmail(System.nanoTime() + "-professional@bol_Schedule");

        customerRepository.save(c1);
        professionalRepository.save(professional);


        Category service = serviceRepository.findByName("PEDICURE");

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
                "      \"scheduleStart\" : \"" + Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 01, 12, 10)).getTime() + "\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : " + service.getIdCategory() + ",\n" +
                "        \"name\" : \"MASSAGISTA\"\n" +
                "      },\n" +
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
                "        \"sourceApp\" : \"facebook\"\n" +
                "      },\n" +
                "      \"idAddress\" : null\n" +
                "    }\n" +
                "  }\n" +
                "}";

        System.out.println(json);

        RequestEntity<String> entity = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<OrderResponseBody> exchange = restTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals(Order.Status.OPEN, exchange.getBody().getOrderList().get(0).getStatus());

        Schedule s = exchange.getBody().getOrderList().get(0).getScheduleId();

        Assert.assertNotNull(s);
        String scheduleDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(s.getScheduleStart());
        Assert.assertEquals("01/07/2017 12:10", scheduleDate);



        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalServices());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalServices().getCategory());
        Assert.assertEquals("PEDICURE",
                exchange.getBody().getOrderList().get(0).getProfessionalServices().getCategory().getName());

        orderRestultFrom_createScheduledOrderOk = exchange.getBody().getOrderList().get(0);

    }
    @Test
    public void scheduledOrderPutOk() throws URISyntaxException {

/*
         PRE-CONDICOES para o teste:
         Criamos um Customer qualquer. Criamos um Profissional qualquer e o associamos a um Service.
         Salvamos tudo no banco.
          */

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-cliente_SchedulePut");
        c1.getUser().setEmail(System.nanoTime() + "-cliente@bol_SchedulePut");

        Customer c2 = CustomerControllerTests.createFakeCustomer();
        c2.getUser().setUsername(System.nanoTime() + "-cliente_ScheduleFail");
        c2.getUser().setEmail(System.nanoTime() + "-cliente@bol_SchedulePutFail");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime() + "-professional_SchedulePut");
        professional.getUser().setEmail(System.nanoTime() + "-professional@bol_SchedulePut");

        customerRepository.save(c1);
        customerRepository.save(c2);
        professionalRepository.save(professional);


        Category service = serviceRepository.findByName("PEDICURE");

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
                "      \"scheduleStart\" : \"" + Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 14, 00)).getTime() + "\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : " + service.getIdCategory() + ",\n" +
                "        \"name\" : \"MASSAGISTA\"\n" +
                "      },\n" +
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
                "        \"sourceApp\" : \"facebook\"\n" +
                "      },\n" +
                "      \"idAddress\" : null\n" +
                "    }\n" +
                "  }\n" +
                "}";

        System.out.println(json);

        RequestEntity<String> entity = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<OrderResponseBody> exchange = restTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals(Order.Status.OPEN, exchange.getBody().getOrderList().get(0).getStatus());

        Schedule s = exchange.getBody().getOrderList().get(0).getScheduleId();

        Assert.assertNotNull(s);
        String scheduleDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(s.getScheduleStart());
        Assert.assertEquals("07/07/2017 14:00", scheduleDate);



        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalServices());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalServices().getCategory());
        Assert.assertEquals("PEDICURE",
                exchange.getBody().getOrderList().get(0).getProfessionalServices().getCategory().getName());

        orderRestultFrom_createScheduledOrderOk = exchange.getBody().getOrderList().get(0);

        Order o1 = orderRestultFrom_createScheduledOrderOk;

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+o1.getIdOrder()+",\n"+
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : \""+Order.Status.SCHEDULED+"\",\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : \"" + Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 14, 00)).getTime() + "\",\n" +
                "      \"scheduleEnd\" : \"" + Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 16, 00)).getTime() + "\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : " + service.getIdCategory() + ",\n" +
                "        \"name\" : \"MASSAGISTA\"\n" +
                "      },\n" +
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
                "        \"sourceApp\" : \"facebook\"\n" +
                "      },\n" +
                "      \"idAddress\" : null\n" +
                "    }\n" +
                "  }\n" +
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
        
        Schedule s1 = exchangeUpdate.getBody().getOrderList().get(0).getScheduleId();
        Assert.assertNotNull(s1);
        String scheduleUpdate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(s1.getScheduleEnd());
        Assert.assertEquals("07/07/2017 16:00", scheduleUpdate);

        Assert.assertEquals(Order.Status.SCHEDULED, exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        String jsonFail = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : \"" + Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 07, 15, 00)).getTime() + "\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"service\" : {\n" +
                "        \"idService\" : " + service.getIdCategory() + ",\n" +
                "        \"category\" : \"MASSAGISTA\"\n" +
                "      },\n" +
                "      \"professional\" : {\n" +
                "        \"idProfessional\" : " + professional.getIdProfessional() + ",\n" +
                "        \"nameProfessional\" : \"Fernanda Cavalcante100  \",\n" +
                "        \"genre\" : \"F\",\n" +
                "        \"birthDate\" : 688010400000,\n" +
                "        \"cellPhone\" : \"(21) 99887-7665\",\n" +
                "        \"dateRegister\" : 1499195092952,\n" +
                "        \"status\" : 0\n" +
                "      }\n" +
                "    },\n" +
                "    \"idLocation\" : null,\n" +
                "    \"idCustomer\" : {\n" +
                "      \"idCustomer\" : " + c2.getIdCustomer() + ",\n" +
                "      \"nameCustomer\" : \"Fernanda Cavalcante9\",\n" +
                "      \"cpf\" : \"816.810.695-688\",\n" +
                "      \"genre\" : \"F\",\n" +
                "      \"birthDate\" : 688010400000,\n" +
                "      \"cellPhone\" : \"(21) 99887-7665\",\n" +
                "      \"dateRegister\" : 1499195092952,\n" +
                "      \"status\" : 0,\n" +
                "      \"idLogin\" : {\n" +
                "        \"username\" : \""+c2.getNameCustomer()+"\",\n" +
                "        \"email\" : \"Kill@bol.com\",\n" +
                "        \"sourceApp\" : \"facebook\"\n" +
                "      },\n" +
                "      \"idAddress\" : null\n" +
                "    }\n" +
                "  }\n" +
                "}";

        System.out.println(jsonFail);

        RequestEntity<String> entityFail = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonFail);

        ResponseEntity<OrderResponseBody> exchangeFail = restTemplate
                .exchange(entityFail, OrderResponseBody.class);

        Assert.assertNotNull(exchangeFail);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchangeFail.getStatusCode());


    }

    @Test
    public void scheduledOrderFail() throws URISyntaxException {



    }

    public String getOrderScheduleJson(Category service, Professional professional, Customer customer) {


        String jsonCreate = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 8,\n" +
                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 05, 14, 00, 0)).getTime() +"\",\n" +
                "      \"scheduleEnd\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 05, 16, 00, 0)).getTime() +"\",\n" +
                "      \"orderCollection\" : [ ]\n" +
                "    },\n" +
                "    \"professionalServices\" : {\n" +
                "      \"service\" : {\n" +
                "        \"idService\" : "+ service.getIdCategory() +",\n" +
                "        \"category\" : \"PEDICURE\"\n" +
                "      },\n" +
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
                "        \"sourceApp\" : \"facebook\"\n" +
                "      },\n" +
                "      \"idAddress\" : null\n" +
                "    }\n" +
                "  }\n" +
                "}";

        return jsonCreate;

    }

    public ResponseEntity<OrderResponseBody> updateOrderStatusInSchedule(Long orderId, Order.Status status) throws URISyntaxException {

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
