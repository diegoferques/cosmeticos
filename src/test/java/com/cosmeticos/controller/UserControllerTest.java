package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.CreditCardRepository;
import com.cosmeticos.repository.UserRepository;
import org.junit.Assert;
import org.junit.Ignore;
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

/**
 * Created by Vinicius on 29/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditCardRepository ccRepository;

    @Ignore
    @Test
    public void inserirUsuarionovoComCartaoNovo() throws URISyntaxException {

        String content = "{\n" +
                "\t\"entity\": \n" +
                "\t{\n" +
                "\t\t\"username\": \"KILLER7\",\n" +
                "\t    \"password\": \"109809876\",\n" +
                "\t    \"email\": \"Killer@gmail.com\",\n" +
                "\t    \"sourceApp\": \"facebook\",\n" +
                "\t    \"creditCardCollection\": [\n" +
                "\t\t    {\n" +
                "\t\t        \"token\": \"4321\",\n" +
                "\t\t        \"vendor\": \"MasterCard\",\n" +
                "\t\t        \"status\": \"ACTIVE\"\n" +
                "\t\t    }\n" +
                "\t    ]\n" +
                "\t}\n" +
                "\t\n" +
                "}";

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/users"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(content);

        ResponseEntity<String> rsp = restTemplate
                .exchange(entity, String.class);

        Assert.assertNotNull(rsp);
        Assert.assertNotNull(rsp.getBody());
        Assert.assertEquals(HttpStatus.OK, rsp.getStatusCode());

        System.out.println(rsp.getBody());

    }

    @Ignore
    @Test
    public void error400QuandoUserCadastrarComIdPreenchido() throws URISyntaxException {
        String content = "{\n" +
                "\t\"entity\": \n" +
                "\t{\n" +
                "\t\t\"username\": \"KILLER7\",\n" +
                "\t    \"password\": \"109809876\",\n" +
                "\t    \"email\": \"Killer@gmail.com\",\n" +
                "\t    \"sourceApp\": \"facebook\",\n" +
                "\t    \"creditCardCollection\": [\n" +
                "\t\t    {\n" +
                "      \"idCreditCard\": 2,\n"+
                "\t\t        \"token\": \"4321\",\n" +
                "\t\t        \"vendor\": \"MasterCard\",\n" +
                "\t\t        \"status\": \"ACTIVE\"\n" +
                "\t\t    }\n" +
                "\t    ]\n" +
                "\t}\n" +
                "\t\n" +
                "}";

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/users"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(content);

        ResponseEntity<String> rsp = restTemplate
                .exchange(entity, String.class);

        Assert.assertNotNull(rsp);
        Assert.assertNotNull(rsp.getBody());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());

    }
    @Ignore
    @Test
    public void inativarUmDosCartoesDeUsuarioCom2Cartoes() throws URISyntaxException {

        // Configurcao do usuario q vai ter o cartao alterado
        CreditCard cc1 = new CreditCard();
        cc1.setToken("4321");
        cc1.setVendor("MasterCard");
        cc1.setStatus(CreditCard.Status.ACTIVE);

        CreditCard cc2 = new CreditCard();
        cc2.setToken("1234");
        cc2.setVendor("visa");
        cc2.setStatus(CreditCard.Status.ACTIVE);

        User u1 = new User();
        u1.setUsername("KILLER card 22 maluco");
        u1.setPassword("109809876");
        u1.setEmail("Killercard22@gmail.com");
        u1.setSourceApp("facebook");
        cc1.setUser(u1);
        u1.addCreditCard(cc1);
        u1.addCreditCard(cc2);

        userRepository.save(u1);

        Long idToken1234 = u1.getCreditCardCollection()
                .stream()
                .filter(cc -> cc.getToken().equals("1234"))
                .findFirst()
                .get()
                .getIdCreditCard();


        // Inicio do teste.
        String content = "{\n" +
                "\t\"entity\": \n" +
                "\t{\n" +
                "\t\t\"idLogin\": "+u1.getIdLogin()+",\n" +
                "\t\t\"username\": \"KILLER7\",\n" +
                "\t    \"password\": \"109809876\",\n" +
                "\t    \"email\": \"Killer@gmail.com\",\n" +
                "\t    \"sourceApp\": \"facebook\",\n" +
                "\t    \"creditCardCollection\": [\n" +
                "\t\t    {\n" +
                "      \"idCreditCard\": "+ idToken1234 +",\n"+
                "\t\t        \"token\": \"ALTERADOOOOOOOOOOOOO\",\n" +
                "\t\t        \"vendor\": \"MasterCard\",\n" +
                "\t\t        \"status\": \"ACTIVE\"\n" +
                "\t\t    }\n" +
                "\t    ]\n" +
                "\t}\n" +
                "\t\n" +
                "}";

        RequestEntity<String> entity =  RequestEntity
                .put(new URI("/users"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(content);

        ResponseEntity<String> rsp = restTemplate
                .exchange(entity, String.class);

        // Conferindo se o controller executou a operacao com sucesso
        Assert.assertNotNull(rsp);
        Assert.assertEquals(HttpStatus.OK, rsp.getStatusCode());

        // Conferindo se o usuario ainda possui 2 cartoes apos eu mandar atualizar apenas 1.
        User updatedUser = userRepository.findOne(u1.getIdLogin());
        Assert.assertEquals(2, updatedUser.getCreditCardCollection().size());

        // Verficando se o cartao que mandamos o controller atualizar foi realmente atualizado no banco.
        CreditCard updatedCC = ccRepository.findOne(idToken1234);
        Assert.assertNotNull(updatedCC);
        Assert.assertEquals("ALTERADOOOOOOOOOOOOO", updatedCC.getToken());

    }

    static User createFakeUser() {
        User u = new User();
        u.setEmail("diego@bol.com");
        u.setPassword("123qwe");
        u.setSourceApp("google+");
        u.setUsername("diegoferques");

        return u;
    }
}
