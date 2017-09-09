package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.UserResponseBody;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
import com.cosmeticos.service.RandomCode;
import com.cosmeticos.smtp.MailSenderService;
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

/**
 * Created by matto on 04/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingUserControllerTests {

    @MockBean
    private MailSenderService mailSenderService;

    @MockBean
    private RandomCode randomCode;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserPasswordReset() throws URISyntaxException {
        Mockito.when(
                randomCode.nextString()
        ).thenReturn("0000");

        Mockito.when(
                mailSenderService.sendEmail(Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
        ).thenReturn(true);



        //////////////////////////////////////////////////////////////////////
        //PRIMEIRO CRIAMOS UM USUARIO PARA DEPOIS SOLICITAR A TROCA DA SENHA//

        User user = new User();
        user.setUsername("Killer");
        user.setEmail("killerhomage@gmail.com");

        userRepository.save(user);

        String jsonCreate = "{\n" +
                "\t\"entity\": \n" +
                "\t{\n" +
                "\t    \"username\": \""+ user.getUsername() +"\", \n" +
                "\t    \"email\": \""+ user.getEmail() +"\"\n" +
                "\t}\n" +
                "\t\n" +
                "}";

        System.out.println(jsonCreate);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/users/prepare_send_token"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonCreate);

        ResponseEntity<UserResponseBody> response = restTemplate
                .exchange(entity, UserResponseBody.class);

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        User userCreated = response.getBody().getUserList().get(0);

        //AGORA CRIAMOS UM UMA SOLICITACAO DE PASSWORD_RESET
        String jsonUpdate = "{\n" +
                "\t\"entity\": \n" +
                "\t{\n" +
                "\t    \"email\": \""+ userCreated.getEmail() +"\", \n" +
                "\t    \"generateToken\": \"0000\"\n" +
                "\t}\n" +
                "\t\n" +
                "}";

        System.out.println(jsonUpdate);


        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/users/password_reset"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<UserResponseBody> rspUpdate = restTemplate
                .exchange(entityUpdate, UserResponseBody.class);

        // Conferindo se o controller executou a operacao com sucesso
        Assert.assertNotNull(rspUpdate);
        Assert.assertEquals(rspUpdate.getBody().getDescription(), HttpStatus.OK, rspUpdate.getStatusCode());

        //NAO TEMOS COMO TESTAR OS QUE SEGUEM ABAIXO, POIS NAO PODEMOS RETORNAR O USER COM EMAIL E SENHA NO RESPONSEBODY
        //Assert.assertEquals("diegoferques@gmail.com", rspUpdate.getBody().getUserList().get(0).getEmail());
        //Assert.assertNotEquals("abcd1234", rspUpdate.getBody().getUserList().get(0).getPassword());

    }
}
