package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OrderService;
import com.cosmeticos.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 28/06/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingOrderControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ProfessionalServicesRepository professionalServicesRepository;

    @MockBean
    private OrderService orderService;

    @MockBean
    PaymentService paymentService;

    @MockBean
    private PaymentController paymentController;

    @Test
    public void testCreateError500() throws IOException, OrderService.ValidationException {
        /**/
        Mockito.when(
                orderService.create(Mockito.anyObject())
        ).thenThrow(new RuntimeException());

        Order s1 = new Order();
        s1.setIdOrder(1L);
        s1.setStatus(Order.Status.CANCELLED);

        OrderRequestBody or = new OrderRequestBody();
        or.setOrder(s1);

        final ResponseEntity<OrderResponseBody> exchange = //
        restTemplate.exchange( //
                "/orders", //
                HttpMethod.POST, //
                new HttpEntity(or), // Body
                OrderResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
    }

    @Test
    public void testUpdateError500() throws Exception {
        /**/
        Mockito.when(
                orderService.update(Mockito.anyObject())
        ).thenThrow(new RuntimeException());

        Order s1 = new Order();
        s1.setIdOrder(1L);
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
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
    }

    //BRANCH: RNF105
    //CARD: https://trello.com/c/8djL5hdx/105-fazer-testes-fim-a-fim-com-a-aplica%C3%A7%C3%A3o-de-p%C3%A9-no-ambiente
    //-de-dev-local-mas-com-as-credenciais-superpay-de-homologa%C3%A7%C3%A3o
    @Test
    public void testUpdateOrderStatusToAcceptedOK () throws URISyntaxException, ParseException, JsonProcessingException {

        Optional<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao();

        Mockito.when(
                paymentController.sendRequest(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);

        //-------- INICIO DA CRIACAO DE CUSTOMER ----------/
        String emailCustomer = "testUpdateOrderStatusToAcceptedOK-customer@email.com";

        String jsonCustomerCreate = "{\n" +
                "   \"customer\":{\n" +
                "      \"address\":{\n" +
                "         \"address\": \"Avenida dos Metalúrgicos, 22\",\n" +
                "         \"cep\":\"26083-275\",\n" +
                "         \"city\":\"Nova Iguaçu\",\n" +
                "         \"country\":\"BR\",\n" +
                "         \"neighborhood\":\"Rodilândia\",\n" +
                "         \"state\":\"RJ\"\n" +
                "      },\n" +
                "      \"birthDate\":1310353200000,\n" +
                "      \"cellPhone\":null,\n" +
                "      \"dateRegister\":null,\n" +
                "      \"genre\":null,\n" +
                "      \"status\":null,\n" +
                "      \"user\":{\n" +
                "         \"email\":\""+ emailCustomer +"\",\n" +
                "         \"idLogin\":null,\n" +
                "         \"password\":\"123\",\n" +
                "         \"sourceApp\":null,\n" +
                "         \"username\":\""+ emailCustomer +"\"\n" +
                "      },\n" +
                "      \"cpf\":\"984.068.804-05\",\n" +
                "      \"idAddress\":null,\n" +
                "      \"idCustomer\":null,\n" +
                "      \"idLogin\":null,\n" +
                "      \"nameCustomer\":\"testUpdateOrderStatusToAcceptedOK-customer\"\n" +
                "   }\n" +
                "}";

        System.out.println(jsonCustomerCreate);

        RequestEntity<String> entityCustomer =  RequestEntity
                .post(new URI("/customers"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCustomerCreate);

        ResponseEntity<CustomerResponseBody> exchange = restTemplate
                .exchange(entityCustomer, CustomerResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals("testUpdateOrderStatusToAcceptedOK-customer", exchange.getBody().getCustomerList().get(0).getNameCustomer());

        //ABAIXO SEGUE O CUSTOMER QUE FOI RETORNADO APOS CRIAR ACIMA, NOTE QUE O ID DE ADDRESS RETORNADO FOI 15
        Customer customer = exchange.getBody().getCustomerList().get(0);

        //TODO - AO BUSCAR NO BANCO O CUSTOMER PELO ID, O ADDRESS RETORNADO NAO EH O MESMO QUE FOI CRIADO INICIALMENTE
        //ABAIXO SEGUE O CUSTOMER QUE BUSCAMOS NO BANCO PELO ID DO CUSTOMER CRIADO ACIMA, O ID DE ADDRESS RETORNADO FOI 7
        Customer customer2 = customerRepository.findOne(customer.getIdCustomer());

        //-------- FIM DA CRIACAO DE CUSTOMER ----------/

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testPaymentOk-professional");
        professional.getUser().setEmail("testPaymentOk-professional@email.com");
        professional.getUser().setPassword("123");
        professional.setCnpj("123.605.789-06");

        customerRepository.save(customer);
        professionalRepository.save(professional);

        Category category = categoryRepository.findByName("PEDICURE");
        category = categoryRepository.findWithSpecialties(category.getIdCategory());

        ProfessionalServices ps1 = new ProfessionalServices(professional, category);
        //ADICIONADO PARA TESTAR O NULLPOINTER
        //professionalServicesRepository.save(ps1);

        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

        /*
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
                "       },\n" +
                "       \"address\": { \n" +
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
        */

        //JSON PARA CRIAR ORDER PARA EFETUAR O PAGAMENTO
        String jsonCreateOrder = "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : 1498324200000,\n" +
                "    \"status\" : 0,\n" +
                "    \"paymentType\" : \""+ Order.PayType.CREDITCARD +"\",\n" +
                //"    \"scheduleId\" : {\n" +
                //"      \"scheduleDate\" : \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 05, 12, 10, 0)).getTime() +"\",\n" +
                //"      \"status\" : \"ACTIVE\",\n" +
                //"      \"orderCollection\" : [ ]\n" +
                //"    },\n" +

                "    \"professionalServices\" : {\n" +
                "      \"category\" : {\n" +
                "        \"idCategory\" : "+category.getIdCategory()+"\n" +
                //"        \"category\" : \"PEDICURE\"\n" +
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
                "      \"idAddress\" : "+ customer.getAddress().getIdAddress() +",\n" +
                "      \"address\": { \n" +
                "   	    \"idAddress\": "+ customer.getAddress().getIdAddress() +"\n" +
                "      }\n" +
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
        //TODO - NAO SEI POR QUAL MOTIVO, MAS OS DADOS DO ENDERECO NAO ESTAO VINDO - PARECE QUE NAO ESTA SALVANDO EM ORDER CREATE
        //-- checar se o seu teste esta incluindo address no json
        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeCreate.getStatusCode());

        Order order = exchangeCreate.getBody().getOrderList().get(0);
        Order order1 = orderRepository.findOne(order.getIdOrder());

        //TODO - OS DO ENDERECO NAO ESTAO VINDO
        Address address = addressRepository.findOne(order1.getIdCustomer().getAddress().getIdAddress());

        /************ FIM DAS PRE_CONDICOES **********************************/

        Optional<RetornoTransacao> retornoTransacao = paymentController.sendRequest(order);

        Assert.assertNotNull(retornoTransacao.isPresent());
        Assert.assertNotNull(retornoTransacao.get().getAutorizacao());
        Assert.assertNotNull(retornoTransacao.get().getNumeroTransacao());

    }

    private Optional<RetornoTransacao> getOptionalFakeRetornoTransacao() {
        RetornoTransacao retornoTransacao = new RetornoTransacao();
        retornoTransacao.setNumeroTransacao(14);
        retornoTransacao.setCodigoEstabelecimento("1501698887865");
        retornoTransacao.setCodigoFormaPagamento(170);
        retornoTransacao.setValor(100);
        retornoTransacao.setValorDesconto(0);
        retornoTransacao.setParcelas(1);
        retornoTransacao.setStatusTransacao(31);
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
