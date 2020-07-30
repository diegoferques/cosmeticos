package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
//import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.CieloOneClickPaymentService;
import com.cosmeticos.validation.OrderValidationException;
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
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Created by matto on 08/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CieloOneClickPaymentService paymentService;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProfessionalRepository professionalRepository;

    @Before
    public void setup()
    {
        Category category = categoryRepository.findByName("PEDICURE");

        if(category == null) {
            category = new Category();
            category.setName("PEDICURE");
            categoryRepository.save(category);
        }
    }

    @IfProfileValue(
            name = Application.ACTIVE_PROFILE_KEY,
            values = Application.PROFILE_TESTING_INTEGRATION_VALUE
    )
    @Test
    public void testCampainhaOk() throws URISyntaxException, ParseException, JsonProcessingException {

        String numeroTransacao = "3";
        String codigoEstabelecimento = "1501698887865";
        String campoLivre1 = "TESTE";

        ResponseEntity<?> exchange = this.executaCampainha(
                numeroTransacao, codigoEstabelecimento, campoLivre1);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

    }

    @Test
    public void testCampainhaBadRequest() throws URISyntaxException, ParseException, JsonProcessingException {

        String numeroTransacao = null;
        String codigoEstabelecimento = "1501698887865";
        String campoLivre1 = "TESTE";

        ResponseEntity<?> exchange = this.executaCampainha(
                numeroTransacao, codigoEstabelecimento, campoLivre1);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());

    }

    @Test
    public void testCampainhaNotFound() throws URISyntaxException, ParseException, JsonProcessingException {

        String numeroTransacao = "9999";
        String codigoEstabelecimento = "1501698887865";
        String campoLivre1 = "TESTE";

        ResponseEntity<?> exchange = this.executaCampainha(
                numeroTransacao, codigoEstabelecimento, campoLivre1);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.NOT_FOUND, exchange.getStatusCode());

    }

    public ResponseEntity<?> executaCampainha(
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

        //TODO: migrar cielo
        ResponseEntity<?> exchange = restTemplate
                .exchange(entityCustomer, Object.class);
//                .exchange(entityCustomer, CampainhaSuperpeyResponseBody.class);

        return exchange;
    }

    @IfProfileValue(
            name = Application.ACTIVE_PROFILE_KEY,
            values = Application.PROFILE_TESTING_INTEGRATION_VALUE
    )
    @Test
    public void testPaymentOk() throws URISyntaxException, ParseException, JsonProcessingException {

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
        Customer customer2 = customerRepository.findById(customer.getIdCustomer()).get();

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
        Order order1 = orderRepository.findById(order.getIdOrder()).get();

        //TODO - OS DO ENDERECO NAO ESTAO VINDO
        Address address = addressRepository.findById(order1.getIdCustomer().getAddress().getIdAddress()).get();

        /************ FIM DAS PRE_CONDICOES **********************************/

        ChargeResponse<Object> retornoTransacao = paymentService.reserve(new ChargeRequest<>(order.getPaymentCollection().stream().findFirst().get()));

        // TODO: migrar cielo
//        RetornoTransacao retornoTransacao1 = (RetornoTransacao) retornoTransacao.getBody();

        Assert.assertNotNull(retornoTransacao.getBody());
//        Assert.assertNotNull(retornoTransacao1.getAutorizacao());
//        Assert.assertNotNull(retornoTransacao1.getNumeroTransacao());

    }

    @IfProfileValue(
            name = Application.ACTIVE_PROFILE_KEY,
            values = Application.PROFILE_TESTING_INTEGRATION_VALUE
    )
    @Test
    public void testCapturarTransacaoOK() throws URISyntaxException, ParseException, JsonProcessingException, OrderValidationException {

        //CERTIFIQUE-SE QUE A ORDER ABAIXO ESTA COM O STATUS READY2CHARGE, CASO CONTRARIO RETORNARA UM ERRO!
        Order order = orderRepository.findById(14L).get();

        ChargeResponse<Object> chargeResponse = paymentService.capture(new ChargeRequest<>(order.getPaymentCollection().stream().findFirst().get()));

        // TODO: migrar cielo
//        RetornoTransacao retornoTransacao = (RetornoTransacao) chargeResponse.getBody();
//
//        Integer superpayStatusTransacao = retornoTransacao.getStatusTransacao();

//        Assert.assertTrue(Payment.Status.fromSuperpayStatus(superpayStatusTransacao).isSuccess());

    }

}
