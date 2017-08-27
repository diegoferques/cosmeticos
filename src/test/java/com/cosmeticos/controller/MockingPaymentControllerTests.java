package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CampainhaSuperpeyResponseBody;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.repository.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 17/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingPaymentControllerTests {

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private PaymentController paymentController;

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
    ProfessionalCategoryRepository professionalServicesRepository;


    @Test
    public void testPaymentOk() throws URISyntaxException, ParseException, JsonProcessingException {

        Optional<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao();

        Mockito.when(
                paymentController.sendRequest(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);

        /*
         PRE-CONDICOES para o teste:
         Criamos um Customer qualquer.
         Criamos um Profissional qualquer e o associamos a um Service.
         Salvamos tudo no banco.
         Criamos uma Order
         */

        //-------- INICIO DA CRIACAO DE CUSTOMER ----------/

        String emailCustomer = "testPaymentOk-customer1@email.com";

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
                "      \"cpf\":\"123.605.789-05\",\n" +
                "      \"idAddress\":null,\n" +
                "      \"idCustomer\":null,\n" +
                "      \"idLogin\":null,\n" +
                "      \"nameCustomer\":\"testPaymentOk-customer1\"\n" +
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
        Assert.assertEquals("testPaymentOk-customer1", exchange.getBody().getCustomerList().get(0).getNameCustomer());

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

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, category);
        //ADICIONADO PARA TESTAR O NULLPOINTER
        //professionalServicesRepository.save(ps1);

        professional.getProfessionalCategoryCollection().add(ps1);

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

                "    \"professionalCategory\" : {\n" +
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

    @Test
    public void testCapturarTransacaoOK() throws URISyntaxException, ParseException, JsonProcessingException {

        Mockito.when(
                paymentController.capturaTransacao(Mockito.any())
        ).thenReturn(true);

        Long numeroTransacao = 3L;

        Boolean capturaTransacao = paymentController.capturaTransacao(numeroTransacao);

        Assert.assertNotNull(capturaTransacao);
        Assert.assertEquals(true, capturaTransacao);

    }

    @Test
    public void testCampainhaOK() throws URISyntaxException, ParseException, JsonProcessingException {

        Optional<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao();

        Mockito.when(
                paymentService.consultaTransacao(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);

        Mockito.when(
                paymentService.updatePaymentStatus(Mockito.any())
        ).thenReturn(true);

        String numeroTransacao = "3";
        String codigoEstabelecimento = "1501698887865";
        String campoLivre1 = "TESTE";

        //PaymentControllerTests paymentControllerTests = new PaymentControllerTests();
        //ResponseEntity<CampainhaSuperpeyResponseBody> exchange = paymentControllerTests.executaCampainha(
        ResponseEntity<CampainhaSuperpeyResponseBody> exchange = this.executaCampainha(
                numeroTransacao, codigoEstabelecimento, campoLivre1);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

    }

    private Optional<RetornoTransacao> getOptionalFakeRetornoTransacao() {
        RetornoTransacao retornoTransacao = new RetornoTransacao();
        retornoTransacao.setNumeroTransacao(3);
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

    public ResponseEntity<CampainhaSuperpeyResponseBody> executaCampainha(
            String numeroTransacao, String codigoEstabelecimento, String campoLivre1)
            throws URISyntaxException, ParseException, JsonProcessingException {

        String urlCampainha = "/campainha/superpay/";
        urlCampainha += "?numeroTransacao=" + numeroTransacao;
        urlCampainha += "&codigoEstabelecimento=" + codigoEstabelecimento;
        urlCampainha += "&campoLivre1=" + campoLivre1;

        System.out.println(urlCampainha);

        RequestEntity<String> entityCustomer =  RequestEntity
                .post(new URI(urlCampainha))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(null);

        ResponseEntity<CampainhaSuperpeyResponseBody> exchange = restTemplate
                .exchange(entityCustomer, CampainhaSuperpeyResponseBody.class);

        return exchange;
    }

    @Test
    public void errorConflictSuperpay()throws URISyntaxException, ParseException, JsonProcessingException{

        //Optional<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao();

        Optional<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao();

        Mockito.when(
                paymentController.sendRequest(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);


        Mockito.doReturn(true).when(paymentService).updatePaymentStatus(
                Mockito.anyObject()
        );

        ResponseEntity<RetornoTransacao> response = new ResponseEntity<RetornoTransacao>(HttpStatus.CONFLICT);
        Mockito.doReturn(response).when(paymentService).doConsultaTransacaoRequest(
                Mockito.anyObject(),
                Mockito.anyObject()
        );

        // TODO: vinicius fazer o request ao endpoint que dispara o pagamento: montar o json e fazer put pra order/ com status ACCEPTED ou SCHEDULED (escolhe um dos dois pois qq um dos dois dispara o pagamento)

        //Assert.assertEquals(HttpStatus.CONFLICT, response.getStatusCodeValue());

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

        Category service = categoryRepository.findByName("PEDICURE");
        service = categoryRepository.findWithSpecialties(service.getIdCategory());

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


        //Assert.assertEquals(HttpStatus.CONFLICT.ordinal(), response.getStatusCodeValue());




        //-------



    }
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
                "            \"idPrice\": "+priceRule.getPrice()+",\n" +
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
