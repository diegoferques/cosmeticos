package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CreditCardRequestBody;
import com.cosmeticos.commons.CreditCardResponseBody;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.CreditCardRepository;
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
public class CreditCardControllerCommomUsagesTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    private Professional professional = null;

    @Before
    public void setupTests() throws ParseException, JsonProcessingException, URISyntaxException {

        professional = ProfessionalControllerTests.createFakeProfessional();

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

        User afterPostUser = userRepository.findById(user.getIdLogin());
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertTrue("Ha mais de um cartao", afterPostUser.getCreditCardCollection().size() == 1);


    }

    @Test
    public void testCustomerUserHasCreditCardCount() throws Exception {

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


}
