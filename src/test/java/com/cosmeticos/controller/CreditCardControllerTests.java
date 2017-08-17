package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CreditCardResponseBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.commons.ProfessionalServicesResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.CreditCardRepository;
import com.cosmeticos.repository.UserRepository;
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
import java.text.ParseException;
import java.util.List;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

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



}
