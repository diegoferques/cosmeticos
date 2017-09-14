package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CampainhaSuperpeyResponseBody;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OrderService;
import com.cosmeticos.service.MulticlickPaymentService;
import com.cosmeticos.validation.OrderValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Ignore;
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
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

/**
 * Created by matto on 17/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingPaymentControllerTests {

    @MockBean
    private MulticlickPaymentService paymentService;

   // @MockBean
   // private PaymentController paymentController;

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
    private OrderService orderService;

    @Autowired
    ProfessionalCategoryRepository professionalServicesRepository;


    @Test
    public void testNonScheduledPaymentCcOk() throws URISyntaxException, ParseException, JsonProcessingException {

        ChargeResponse<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao(31);

        Mockito.when(
                paymentService.reserve(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);

        /*
         PRE-CONDICOES para o teste:
         Criamos um Customer qualquer.
         Criamos um Profissional qualquer e o associamos a um Service.
         Salvamos tudo no banco.
         Criamos uma Order
         */

        //-------- INICIO DA CRIACAO DE CUSTOMER ----------/

        Customer customer = postCustomerWhatever("testPaymentOk-customer1@email.com");

        putCustomerAddCreditCard(customer);


        //-------- FIM DA CRIACAO DE CUSTOMER ----------/

       ProfessionalCategory professionalCategory = createProfessionalWhatever(
    		   "testPaymentOk-professional@email.com",
    		   "123.605.789-06"
      );

       // Determinamos a priceRule selecionaddaa pelo cliente
        PriceRule priceRule200Reais = professionalCategory.getPriceRuleList()
                .stream()
                .filter(p -> p.getPrice() == 20000L)
                .findFirst()
                .get();

        //JSON PARA CRIAR ORDER PARA EFETUAR O PAGAMENTO
        String jsonCreateOrder = OrderJsonHelper.buildJsonCreateNonScheduledOrder(
                customer,
                professionalCategory,
                Payment.Type.CC,
                priceRule200Reais
        );

        System.out.println(jsonCreateOrder);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreateOrder);

        ResponseEntity<OrderResponseBody> exchangeCreate = restTemplate
                .exchange(entity, OrderResponseBody.class);

        //TODO - NAO SEI POR QUAL MOTIVO, MAS OS DADOS DO ENDERECO NAO ESTAO VINDO - PARECE QUE NAO ESTA SALVANDO EM ORDER CREATE
        /*
         Resp: Nao vem pq nao faz sentido retornar endereco como resultado de Order, por isso o address nem entra no jsonview do endpoint que este request vai chamar.
         TODO: Apagar estes comentarios quando tivermos entendido esta parte.
          */
        Assert.assertNotNull(exchangeCreate);
        Assert.assertNotNull(exchangeCreate.getBody().getOrderList());
        Assert.assertEquals(exchangeCreate.getBody().getDescription(), HttpStatus.OK, exchangeCreate.getStatusCode());

        Order order = exchangeCreate.getBody().getOrderList().get(0);
        Order order1 = orderRepository.findOne(order.getIdOrder());

        //TODO - OS DO ENDERECO NAO ESTAO VINDO
        //RESP: Nem deveria. Ver meu outro "Resp" acima
        Address address = addressRepository.findOne(order1.getIdCustomer().getAddress().getIdAddress());

        /************ FIM DAS PRE_CONDICOES **********************************/

        ChargeResponse<RetornoTransacao> retornoTransacao = paymentService.reserve(new ChargeRequest<>(order));

        Assert.assertNotNull(retornoTransacao.getBody());
        Assert.assertNotNull(retornoTransacao.getBody().getAutorizacao());
        Assert.assertNotNull(retornoTransacao.getBody().getNumeroTransacao());

    }

    @Test
    public void testScheduledOrderPaymentCcOk() throws URISyntaxException, ParseException, JsonProcessingException {

        ChargeResponse<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao(31);

        Mockito.when(
                paymentService.reserve(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);

        //-------- INICIO DA CRIACAO DE CUSTOMER ----------/

        Customer customer = postCustomerWhatever("testScheduledOrderPaymentOk-customer1@email.com");

        putCustomerAddCreditCard(customer);


        //-------- FIM DA CRIACAO DE CUSTOMER ----------/

       ProfessionalCategory professionalCategory = createProfessionalWhatever(
    		   "testScheduledOrderPaymentOk-professional@email.com",
    		   "123.605.789-07"
      );

       // Determinamos a priceRule selecionaddaa pelo cliente
        PriceRule priceRule200Reais = professionalCategory.getPriceRuleList()
                .stream()
                .filter(p -> p.getPrice() == 20000L)
                .findFirst()
                .get();

        //JSON PARA CRIAR ORDER PARA EFETUAR O PAGAMENTO
        String jsonCreateOrder = OrderJsonHelper.buildJsonCreateScheduledOrder(
                customer,
                professionalCategory,
                priceRule200Reais,
                Payment.Type.CC,
                Timestamp.valueOf(now().plusHours(5)).getTime()
        );

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
        Assert.assertEquals(exchangeCreate.getBody().getDescription(), HttpStatus.OK, exchangeCreate.getStatusCode());

        Order order = exchangeCreate.getBody().getOrderList().get(0);

        // Confirmando se a order foi mesmo pro banco apesar do status ter sido 200.
        Assert.assertNotNull("A order nao foi icluido no banco", orderRepository.findOne(order.getIdOrder()));

    }

    private void putCustomerAddCreditCard(Customer customer) throws URISyntaxException {
        String jsonCustomerCreate = "{\n" +
                "   \"customer\":{\n" +
                "      \"idCustomer\": "+ customer.getIdCustomer() +",\n" +
                "      \"user\":{\n" +
                "         \"idLogin\":"+customer.getUser().getIdLogin()+",\n" +
                "         \"creditCardCollection\": [\n" +
                    "         {\n" +
                    "\t\t        \"token\": \"ALTERADOOOOOOOOOOOOO\",\n" +
                    "\t\t        \"ownerName\": \"Teste\",\n" +
                    "\t\t        \"tailNumber\": \""+System.nanoTime()+"\",\n" +
                    "\t\t        \"securityCode\": \"098\",\n" +
                    "\t\t        \"expirationDate\": \""+ Timestamp.valueOf(now().plusDays(30)).getTime() +"\",\n" +
                    "\t\t        \"vendor\": \"MasterCard\",\n" +
                    "\t\t        \"status\": \"ACTIVE\"\n" +
                    "\t\t     }\n" +
                "         ]\n" +
                "      }\n" +
                "   }\n" +
                "}";

        System.out.println(jsonCustomerCreate);

        RequestEntity<String> entityCustomer =  RequestEntity
                .put(new URI("/customers"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCustomerCreate);

        ResponseEntity<CustomerResponseBody> exchange = restTemplate
                .exchange(entityCustomer, CustomerResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals(customer.getNameCustomer(), exchange.getBody().getCustomerList().get(0).getNameCustomer());

    }

    private ProfessionalCategory createProfessionalWhatever(String email, String cnpj) {
		 Professional professional = ProfessionalControllerTests.createFakeProfessional();
	        professional.getUser().setUsername(email);
	        professional.getUser().setEmail(email);
	        professional.getUser().setPassword("123");
	        professional.setCnpj(cnpj);

	        professionalRepository.save(professional);

	        Category category = categoryRepository.findByName("PEDICURE");
	        category = categoryRepository.findWithSpecialties(category.getIdCategory());

	        ProfessionalCategory ps1 = new ProfessionalCategory(professional, category);
            ps1.addPriceRule( new PriceRule("Cabelo curto", 10000L));
            ps1.addPriceRule( new PriceRule("Cabelo Medio", 15000L));
            ps1.addPriceRule( new PriceRule("Cabelo Longo", 20000L));

	        // Atualizando associando o Profeissional ao Servico
	        professionalServicesRepository.save(ps1);

	        return ps1;
	}

	private Customer postCustomerWhatever(String emailCustomer) throws URISyntaxException {
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

		return customer;
	}

	//IGNORADO, UTILIZAR A NOVA VERSAO LOGO NO PROXIMO TESTE: testCapturarTransacaoOK2
	@Ignore
    @Test
    public void testCapturarTransacaoOK() throws URISyntaxException, ParseException, JsonProcessingException, OrderValidationException {

        ChargeResponse<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao(2);
        ResponseEntity<RetornoTransacao> responseEntityFakeRetornoTransacao = this.getResponseEntityFakeRetornoTransacao(1);

        Mockito.when(
                paymentService.getStatus(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);

        Mockito.when(
                paymentService.capture(Mockito.any())
        ).thenCallRealMethod();

        //validatePaymentStatusAndSendCapture

        // TODO: diego, seu teste ta dependendo de do preload, nao pode.. tem q criar essa order dentro do seu metodo de teste.
        Order order = orderRepository.findOne(3L);

        // DIEGO, acho que aqui vc queria execuutar o metodo mas como ele ta mocado, vai sempre responder false e o teste vai falhar.
        // O Mockito permite desmocar um bean, basta fazer o que fiz acima pro paymentController

        ChargeResponse<RetornoTransacao> retornoTransacaoSuperpay = paymentService.capture(new ChargeRequest<>(order));

        Integer superpayStatusStransacao = retornoTransacaoSuperpay.getBody().getStatusTransacao();

        Payment.Status paymentStatus = Payment.Status.fromSuperpayStatus(superpayStatusStransacao);

        Assert.assertTrue(paymentStatus.isSuccess());
    }

    @Ignore
    @Test
    public void testCapturarTransacaoOK2() throws Exception {

        ChargeResponse<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao(2);
        ResponseEntity<RetornoTransacao> responseEntityFakeRetornoTransacao = this.getResponseEntityFakeRetornoTransacao(1);

        Mockito.when(
                paymentService.getStatus(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);

        Mockito.when(
                paymentService.updatePaymentStatus(Mockito.any())
        ).thenCallRealMethod();

        Mockito.when(
                paymentService.reserve(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);

        Mockito.when(
                paymentService.capture(Mockito.any())
        ).thenCallRealMethod();

        //-------- INICIO DA CRIACAO DE CUSTOMER ----------/

        Customer customer = postCustomerWhatever("testCapturarTransacaoOK-customer@email.com");

        putCustomerAddCreditCard(customer);


        //-------- FIM DA CRIACAO DE CUSTOMER ----------/

        ProfessionalCategory professionalCategory = createProfessionalWhatever(
                "testCapturarTransacaoOK-professional@email.com",
                "123.605.789-07"
        );

        // Determinamos a priceRule selecionaddaa pelo cliente
        PriceRule priceRule200Reais = professionalCategory.getPriceRuleList()
                .stream()
                .filter(p -> p.getPrice() == 20000L)
                .findFirst()
                .get();

        //JSON PARA CRIAR ORDER PARA EFETUAR O PAGAMENTO
        String jsonCreateOrder = OrderJsonHelper.buildJsonCreateNonScheduledOrder(
                customer,
                professionalCategory,
                Payment.Type.CC,
                priceRule200Reais
        );

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
        Assert.assertEquals(exchangeCreate.getBody().getDescription(), HttpStatus.OK, exchangeCreate.getStatusCode());

        Order order = exchangeCreate.getBody().getOrderList().get(0);

        // Confirmando se a order foi mesmo pro banco apesar do status ter sido 200.
        Assert.assertNotNull("A order nao foi icluido no banco", orderRepository.findOne(order.getIdOrder()));


        //--- INICIO - ABAIXO MUDAMOS DE OPEN PARA ACCEPTED ---//
        Order orderAccepted = orderRepository.findOne(order.getIdOrder());
        //APOS CRIAR ORDER, MUDAMOS SEU STATUS PARA ACCEPTED
        orderAccepted.setStatus(Order.Status.ACCEPTED);
        OrderRequestBody orderRequestAccepted = new OrderRequestBody();
        orderRequestAccepted.setOrder(orderAccepted);





        // TODO: apontar pro controller em vez do service.
        Order orderAcceptedUpdated = orderService.update(orderRequestAccepted);
        //ABAIXO VERIFICAMOS SE TUDO CORREU CONFORME O ESPERADO
        Assert.assertNotNull(orderAcceptedUpdated);
        Assert.assertEquals(Order.Status.ACCEPTED, orderAcceptedUpdated.getStatus());
        //--- FIM ---//

        //--- INICIO - ABAIXO MUDAMOS DE ACCEPTED PARA INPROGRESS ---//
        Order orderInProgress = orderRepository.findOne(order.getIdOrder());
        //APOS CRIAR ORDER, MUDAMOS SEU STATUS PARA ACCEPTED
        orderInProgress.setStatus(Order.Status.INPROGRESS);
        OrderRequestBody orderRequestInProgress = new OrderRequestBody();
        orderRequestInProgress.setOrder(orderInProgress);

        Order orderInProgressUpdated = orderService.update(orderRequestInProgress);
        //ABAIXO VERIFICAMOS SE TUDO CORREU CONFORME O ESPERADO
        Assert.assertNotNull(orderInProgressUpdated);
        Assert.assertEquals(Order.Status.INPROGRESS, orderInProgressUpdated.getStatus());
        //--- FIM ---//

        //--- INICIO - ABAIXO MUDAMOS DE INPROGRESS PARA EXECUTED ---//
        Order orderExecuted = orderRepository.findOne(order.getIdOrder());
        //APOS CRIAR ORDER, MUDAMOS SEU STATUS PARA ACCEPTED
        orderExecuted.setStatus(Order.Status.EXECUTED);
        OrderRequestBody orderRequestExecuted = new OrderRequestBody();
        orderRequestExecuted.setOrder(orderExecuted);

        Order orderExecutedUpdated = orderService.update(orderRequestExecuted);
        //ABAIXO VERIFICAMOS SE TUDO CORREU CONFORME O ESPERADO
        Assert.assertNotNull(orderExecutedUpdated);
        Assert.assertEquals(Order.Status.EXECUTED, orderExecutedUpdated.getStatus());
        //--- FIM ---//


        //--- INICIO - ABAIXO MUDAMOS DE INPROGRESS PARA READY2CHARGE E, AUTOMATICAMENTE, EFETUAMOS A CAPTURA ---//
        Order orderReady2Charge = orderRepository.findOne(order.getIdOrder());
        //APOS CRIAR ORDER, MUDAMOS SEU STATUS PARA ACCEPTED
        orderReady2Charge.setStatus(Order.Status.READY2CHARGE);
        OrderRequestBody orderRequestReady2Charge = new OrderRequestBody();
        orderRequestReady2Charge.setOrder(orderReady2Charge);

        Order orderReady2ChargeUpdated = orderService.update(orderRequestReady2Charge);
        //ABAIXO VERIFICAMOS SE TUDO CORREU CONFORME O ESPERADO
        Assert.assertNotNull(orderReady2ChargeUpdated);
        Assert.assertEquals(Order.Status.READY2CHARGE, orderReady2ChargeUpdated.getStatus());
        //--- FIM ---//




        // DIEGO, acho que aqui vc queria execuutar o metodo mas como ele ta mocado, vai sempre responder false e o teste vai falhar.
        // O Mockito permite desmocar um bean, basta fazer o que fiz acima pro paymentController
        //Boolean capturaTransacao = paymentController.validatePaymentStatusAndSendCapture(order);

        //Assert.assertNotNull(capturaTransacao);
        //Assert.assertEquals(true, capturaTransacao);

    }

    @Test
    public void testCampainhaOK() throws URISyntaxException, ParseException, JsonProcessingException {

        ChargeResponse<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao(1);

        Mockito.when(
                paymentService.getStatus(Mockito.any())
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

    private ChargeResponse<RetornoTransacao> getOptionalFakeRetornoTransacao(int statusTransacao) {
        return new ChargeResponse(this.getFakeRetornoTransacao(statusTransacao));
    }

    private ResponseEntity<RetornoTransacao> getResponseEntityFakeRetornoTransacao(int statusTransacao) {
        return ResponseEntity.ok(this.getFakeRetornoTransacao(statusTransacao));
    }

    private RetornoTransacao getFakeRetornoTransacao(int statusTransacao) {

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

        return retornoTransacao;
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

        ChargeResponse<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao(31);

        Mockito.when(
                paymentService.reserve(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);


        Mockito.doReturn(true).when(paymentService).updatePaymentStatus(
                Mockito.anyObject()
        );

        ResponseEntity<RetornoTransacao> response = new ResponseEntity<RetornoTransacao>(HttpStatus.CONFLICT);
        Mockito.doReturn(response).when(paymentService).doCapturaTransacaoRequest(
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


        String categoryName = "PEDICURE";
        String priceRuleName = "RULE";
        Long price = 6600L;

        Category service = categoryRepository.findByName(categoryName);
        service = categoryRepository.findWithSpecialties(service.getIdCategory());

        PriceRule priceRule = new PriceRule();
        priceRule.setName(priceRuleName);
        priceRule.setPrice(price);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
        ps1.addPriceRule(priceRule);

        // Atualizando associando o Profeissional ao Servico
        professionalServicesRepository.save(ps1);
        //-------

        //CRIAMOS ORDER COM O PROFESSIONAL E O CUSTOMER 1 PARA, POSTERIORMENTE, ATUALIZAMOS O STATUS PARA ACCEPTED
        String jsonCreate =
                OrderJsonHelper.buildJsonCreateScheduledOrder(
                        c1,
                        ps1,
                        priceRule,
                        Payment.Type.CASH,
                        Timestamp.valueOf(now().plusDays(3)).getTime()
                );

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
        //Assert.assertEquals(HttpStatus.OK, exchangeUpdateAccepted.getStatusCode());
        Assert.assertEquals(HttpStatus.CONFLICT, exchangeUpdateAccepted.getStatusCode());
/*
        Order orderUpdateAccepted = exchangeUpdateAccepted.getBody().getOrderList().get(0);
        Assert.assertEquals(Order.Status.ACCEPTED, orderUpdateAccepted.getHttpStatus());
        //-------

 //Removi pq antes o post estava indo no ssuperpay mas agora nao vai mais.
        //TENTAMOS CRIAR NOVO ORDER PARA O MESMO PROFESSIONAL ENQUANTO ELE JA TEM UM ORDER COM STATUS ACCEPTED
        String jsonCreate2 =OrderJsonHelper.buildJsonCreateScheduledOrder(
                        c2,
                        ps1,
                        priceRule,
                        Payment.Type.CASH,
                        Timestamp.valueOf(LocalDateTime.now().plusDays(5)).getTime()
                );

        RequestEntity<String> entity2 =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate2);

        ResponseEntity<OrderResponseBody> exchangeCreate2 = restTemplate
                .exchange(entity2, OrderResponseBody.class);

        Assert.assertNotNull(exchangeCreate2);
        Assert.assertEquals(HttpStatus.CONFLICT, exchangeCreate2.getStatusCode()); */
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
