package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.TypedCcPaymentService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 04/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScheduleOrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private TypedCcPaymentService paymentService;

    private Order orderRestultFrom_createScheduledOrderOk = null;

    @Before
    public void setup() throws ParseException, JsonProcessingException {
        Category service = serviceRepository.findByName("PEDICURE");

        if (service == null) {
            service = new Category();
            service.setName("PEDICURE");
            serviceRepository.save(service);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////// Mocando o controller que vai no superpay e vai sofrer um refactoring monstruoso //////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ChargeResponse<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao(2);

        Mockito.when(
                paymentService.reserve(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);
    }

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

        Category service = new Category();
        service.setName("PEDICUREscheduledOrderOk");
        serviceRepository.save(service);
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        PriceRule pr = new PriceRule("Promocao 20 reais", 2000);
        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(pr);

        // Atualizando associando o Profeissional ao Servico
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
                pr,
                Payment.Type.CASH,
                Timestamp.valueOf(LocalDateTime.of(2017, 7, 20, 14, 0)).getTime()
        ) ;

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
        Assert.assertEquals("20/07/2017 14:00", scheduleDate);



        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalCategory());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalCategory().getCategory());
        Assert.assertEquals("PEDICUREscheduledOrderOk",
                exchange.getBody().getOrderList().get(0).getProfessionalCategory().getCategory().getName());

        orderRestultFrom_createScheduledOrderOk = exchange.getBody().getOrderList().get(0);

    }

    //MOCK
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

        Category service = new Category();
        service.setName("scheduledOrderPutOk-service");
        serviceRepository.save(service);

        service = serviceRepository.findWithSpecialties(service.getIdCategory());


        PriceRule pr = new PriceRule("Promocao 20 reais", 2000);
        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(pr);

        // Atualizando associando o Profeissional ao Servico
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
                pr,
                Payment.Type.CASH,
                Timestamp.valueOf(LocalDateTime.of(2017, 9, 15, 10, 0)).getTime()
        ) ;

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
        Assert.assertEquals("15/09/2017 10:00", scheduleDate);



        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalCategory());
        Assert.assertNotNull(exchange.getBody().getOrderList().get(0).getProfessionalCategory().getCategory());
        Assert.assertEquals("scheduledOrderPutOk-service",
                exchange.getBody().getOrderList().get(0).getProfessionalCategory().getCategory().getName());

        orderRestultFrom_createScheduledOrderOk = exchange.getBody().getOrderList().get(0);

        Order o1 = orderRestultFrom_createScheduledOrderOk;

        // Obtendo o start time da order criada e adicionando 30 mintos de trabalho.
        LocalDateTime scheduleStart = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(
                    o1.getScheduleId().getScheduleStart().getTime()
                ),
                ZoneId.systemDefault()
        );

        String jsonUpdate = OrderJsonHelper.buildJsonUpdateScheduledOrder(
                o1.getIdOrder(),
                Order.Status.SCHEDULED,
                o1.getScheduleId().getScheduleId(),
                Timestamp.valueOf(scheduleStart.plusHours(4)).getTime()

        );

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
        Assert.assertEquals("15/09/2017 14:00", scheduleUpdate);

        Assert.assertEquals(Order.Status.SCHEDULED, exchangeUpdate.getBody().getOrderList().get(0).getStatus());

        String jsonFail =
                OrderJsonHelper.buildJsonCreateScheduledOrder(
                        c2,
                        ps1,
                        pr,
                        Payment.Type.CC,
                        Timestamp.valueOf(LocalDateTime.MAX.of(
                                2017,
                                9,
                                15,
                                13,
                                00))
                                .getTime()
                );

        System.out.println(jsonFail);

        RequestEntity<String> entityFail = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonFail);

        ResponseEntity<OrderResponseBody> exchangeFail = restTemplate
                .exchange(entityFail, OrderResponseBody.class);

        Assert.assertNotNull(exchangeFail);
        Assert.assertEquals(HttpStatus.CONFLICT, exchangeFail.getStatusCode());


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
                "    \"professionalCategory\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : " + service.getIdCategory() + "\n" +
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

    private ChargeResponse<RetornoTransacao> getOptionalFakeRetornoTransacao(int statusTransacao) {
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

        return new ChargeResponse(retornoTransacao);

    }
}
