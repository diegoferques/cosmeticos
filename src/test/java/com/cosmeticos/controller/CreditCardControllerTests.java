package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CreditCardRequestBody;
import com.cosmeticos.commons.CreditCardResponseBody;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.CreditCardRepository;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDate.now;

/**
 * Created by Vinicius on 08/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreditCardControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Before
    public void setupTests() throws ParseException{



    }

    @Test
    public void fazerTesteDeBuscaPara1CartaoPeloEmail() throws ParseException {


        try {
            // ta retornando vazio.. deve ser por causa do codigo q coloquei la
            final ResponseEntity<CreditCardResponseBody> getExchange = //
                    restTemplate.exchange( //
                            //"/creditCard?user.email=ciclanor@gmail.com",
                            "/creditCard?user.email=namek@gmail.com",
                            HttpMethod.GET, //
                            null,
                            CreditCardResponseBody.class);

            Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());

            CreditCardResponseBody response = getExchange.getBody();

            List<CreditCard> entityList = response.getCreditCardList();
            System.out.println(entityList);
            Assert.assertTrue("Nao foram retornados profissionais.", entityList.size() > 0);

            for (int i = 0; i < entityList.size(); i++) {
                CreditCard cc =  entityList.get(i);

                User u = cc.getUser();
                //cc.setUser(u);

                Assert.assertNotNull("CreditoCard deve ter User", cc);
                //Assert.assertEquals(getExchange, u.getEmail());

            }//continua ai rsrskkkkkk...
        } catch (RestClientException e) {
            Assert.fail(e.getMessage());
        }


    }
    @Test
    public void fazerTesteDeBuscaPara2CartaoPeloEmail() throws ParseException {

        final ResponseEntity<CreditCardResponseBody> getExchange = //
                restTemplate.exchange( //
                        //"/creditCard?user.email=ciclanor@gmail.com",
                        "/creditCard?user.email=killer@gmail.com",
                        HttpMethod.GET, //
                        null,
                        CreditCardResponseBody.class);

        Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());

        CreditCardResponseBody response = getExchange.getBody();

        List<CreditCard> entityList = response.getCreditCardList();
        System.out.println(entityList);
        Assert.assertTrue("Nao foram retornados profissionais.", entityList.size() > 0);

        for (int i = 0; i < entityList.size(); i++) {
            CreditCard cc =  entityList.get(i);

            User u = cc.getUser();
            //cc.setUser(u);

            Assert.assertNotNull("CreditoCard deve ter User", cc);
            //Assert.assertEquals(getExchange, u.getEmail());

        }
    }
    @Test
    public void testeDeBuscaPeloEmail() throws ParseException, URISyntaxException {

        RequestEntity<Void> entity =  RequestEntity
                //"/creditCard?user.email=ciclanor@gmail.com",
                .get(new URI("/creditCard?user.email="))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        ResponseEntity<Void> getExchange = restTemplate
                .exchange(entity, Void.class);

                Assert.assertEquals(HttpStatus.NOT_FOUND, getExchange.getStatusCode());

        }

    @Test
    public void testeCadastrarCartaoParaProfessionalUser() throws ParseException, URISyntaxException, JsonProcessingException {

        Professional professional = ProfessionalControllerTests.createFakeProfessional();

        professionalRepository.save(professional);

        // Mandando user so com o id preenchido.
        User user = new User();
        user.setIdLogin(professional.getUser().getIdLogin());

        CreditCard cc = CreditCard.builder()
                .expirationDate(now().getMonth().toString() + "/" + now().plusYears(3).getYear())
                .lastUsage(Timestamp.valueOf(LocalDateTime.now()))
                .number("0000000000000001")
                .oneClick(true)
                .ownerName("testeCadastrarCartaoParaUser")
                .securityCode("123")
                .status(CreditCard.Status.ACTIVE)
                .suffix("0001")
                .vendor("VISA")
                .user(user)
                .build();

        CreditCardRequestBody body = new CreditCardRequestBody();
        body.setEntity(cc);

        String json = Utility.toJson(body);

        ResponseEntity<?> exchange = postEntity("/creditCard", json, CreditCardResponseBody.class );

        User afterPostUser = userRepository.findOne(user.getIdLogin());
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertTrue("Ha mais de um cartao", afterPostUser.getCreditCardCollection().size() == 1);

        professional = professionalRepository.findOne(professional.getIdProfessional());

        Assert.assertTrue("Nao veio cartao de credito", professional.getUser().getCreditCardCount() > 0);

    }

    @Test
    public void testeCadastrarCartaoParaCustomerUser$assertViaRepository() throws ParseException, URISyntaxException, JsonProcessingException {

        Customer customer = CustomerControllerTests.createFakeCustomer();

        customerRepository.save(customer);

        User user = customer.getUser();

        String json = buildCreditCardJson(user);

        ResponseEntity<?> exchange = postEntity("/creditCard", json, CreditCardResponseBody.class );

        User afterPostUser = userRepository.findOne(user.getIdLogin());
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertTrue("Ha mais de um cartao", afterPostUser.getCreditCardCollection().size() == 1);

        customer = customerRepository.findOne(customer.getIdCustomer());

        Assert.assertTrue("Nao veio cartao de credito para o customer", customer.getUser().getCreditCardCount() > 0);

    }

    @Test
    public void testeCadastrarCartaoParaCustomerUser$assertViaRestCall() throws ParseException, URISyntaxException, JsonProcessingException {

        Customer customer = CustomerControllerTests.createFakeCustomer();

        customerRepository.save(customer);

        User user = customer.getUser();

        Assert.assertEquals("Customer ja nasce com 1 cartao de credito", Integer.valueOf(0), user.getCreditCardCount());

        String json = buildCreditCardJson(user);

        ResponseEntity<?> exchange = postEntity("/creditCard", json, CreditCardResponseBody.class );

       // Customer retrievedCustomer = getCustomer()


        User afterPostUser = userRepository.findOne(user.getIdLogin());
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertTrue("Ha mais de um cartao", afterPostUser.getCreditCardCollection().size() == 1);

        customer = customerRepository.findOne(customer.getIdCustomer());

        Assert.assertTrue("Nao veio cartao de credito para o customer", customer.getUser().getCreditCardCount() > 0);

    }

    String buildCreditCardJson(User sourceUser) throws JsonProcessingException {

        // Mandando user so com o id preenchido.
        User user = new User();
        user.setIdLogin(sourceUser.getIdLogin());

        CreditCard cc = buildTestCreditCard(user);

        CreditCardRequestBody body = new CreditCardRequestBody();
        body.setEntity(cc);

        return Utility.toJson(body);
    }

    public static CreditCard buildTestCreditCard(User user) {
        return CreditCard.builder()
                    .expirationDate(now().getMonth().toString() + "/" + now().plusYears(3).getYear())
                    .lastUsage(Timestamp.valueOf(LocalDateTime.now()))
                    .number("0000000000000001")
                    .oneClick(true)
                    .ownerName("testeCadastrarCartaoParaCustomerUser")
                    .securityCode("123")
                    .status(CreditCard.Status.ACTIVE)
                    .suffix("0001")
                    .vendor("VISA")
                    .user(user)
                    .build();
    }

    private ResponseEntity<?> postEntity(String url, String body, Class<?> responseClass) throws URISyntaxException {
        RequestEntity<String> entity = RequestEntity
                    .post(new URI(url))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body);

        ResponseEntity<?> exchange = restTemplate
                .exchange(entity, responseClass);

        return exchange;
    }

    public static ResponseEntity<CreditCardResponseBody> getCreditcard(final TestRestTemplate restTemplate, String query) throws URISyntaxException {

        return restTemplate.exchange( //
                "/creditCard" + (query != null && !query.isEmpty() ? "?" + query : ""), //
                HttpMethod.GET, //
                null,
                CreditCardResponseBody.class);
    }


}
