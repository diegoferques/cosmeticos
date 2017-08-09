package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OrderService;
import com.cosmeticos.service.VoteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Before;
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
import java.text.ParseException;
import java.time.LocalDateTime;

/**
 * Created by matto on 08/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTests {

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

    @Autowired
    private VoteService voteService;

    @Autowired
    private PaymentController paymentController;

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

    @Test
    public void testPaymentOk() throws URISyntaxException, ParseException, JsonProcessingException {

        /*
         PRE-CONDICOES para o teste:
         Criamos um Customer qualquer.
         Criamos um Profissional qualquer e o associamos a um Service.
         Salvamos tudo no banco.
         Criamos uma Order
         */

        Customer customer = CustomerControllerTests.createFakeCustomer();
        customer.getUser().setUsername("testPaymentOk-customer1");
        customer.getUser().setEmail("testPaymentOk-customer1@email.com");
        customer.getUser().setPassword("123");
        customer.setCpf("123.605.789-05");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testPaymentOk-professional");
        professional.getUser().setEmail("testPaymentOk-professional@email.com");
        professional.getUser().setPassword("123");
        professional.setCnpj("123.605.789-06");

        customerRepository.save(customer);
        professionalRepository.save(professional);


        Service service = serviceRepository.findByCategory("PEDICURE");

        ProfessionalServices ps1 = new ProfessionalServices(professional, service);

        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        //JSON PARA CRIAR ORDER PARA EFETUAR O PAGAMENTO
        String jsonCreateOrder = "{\n" +
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
                "        \"idService\" : "+ service.getIdService() +",\n" +
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
                //"      \"idAddress\" : null\n" +
                "       \"idAddress\": { \n" +
                "   	    \"address\": \"Avenida dos Metalúrgicos, 22\",\n" +
                "   	    \"cep\": \"26083-275\",\n" +
                "   	    \"neighborhood\": \"Rodilândia\",\n" +
                "   	    \"city\": \"Nova Iguaçu\",\n" +
                "   	    \"state\": \"RJ\",\n" +
                "   	    \"country\": \"BR\" \n" +
                "       }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        System.out.println(jsonCreateOrder);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreateOrder);

        ResponseEntity<OrderResponseBody> exchangeCreate = restTemplate
                .exchange(entity, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());

        Order order = exchangeCreate.getBody().getOrderList().get(0);

        /************ FIM DAS PRE_CONDICOES **********************************/

        String retornoSuperpay = paymentController.sendRequest(order);

        Assert.assertNotNull(retornoSuperpay);
        System.out.println(retornoSuperpay);

    }


}
