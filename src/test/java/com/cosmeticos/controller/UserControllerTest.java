package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.UserResponseBody;
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
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    @Ignore // Ignorado pq o cartao se adiciona por put
    @Test
    public void inserirUsuarionovoComCartaoNovo() throws URISyntaxException {

        String content = "{\n" +
                "\t\"entity\": \n" +
                "\t{\n" +
                "\t\t\"username\": \"KILLER7\",\n" +
                "\t    \"password\": \"109809876\",\n" +
                "\t    \"email\": \"Killer@gmail.com\",\n" +
                "\t    \"personType\":\"FÍSICA\",\n" +
                "\t    \"sourceApp\": \"facebook\",\n" +
                "\t    \"creditCardCollection\": [\n" +
                "\t\t    {\n" +
                "\t\t        \"token\": \"4321\",\n" +
                "\t\t        \"ownerName\": \"Teste\",\n" +
                "\t\t        \"suffix\": \"0987654555775434567\",\n" +
                "\t\t        \"securityCode\": \"098\",\n" +
                "\t\t        \"expirationDate\": \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 11, 10, 0, 0)).getTime() +"\",\n" +
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
                "\t      \"personType\":\"FÍSICA\",\n" +
                "\t    \"creditCardCollection\": [\n" +
                "\t\t    {\n" +
                "      \"idCreditCard\": 2,\n"+
                "\t\t        \"token\": \"4321\",\n" +
                "\t\t        \"ownerName\": \"Teste\",\n" +
                "\t\t        \"suffix\": \"0987654555775434567\",\n" +
                "\t\t        \"securityCode\": \"098\",\n" +
                "\t\t        \"expirationDate\": \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 11, 10, 0, 0)).getTime() +"\",\n" +
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

    @Ignore // Nos inserimos o cara mas no controller o cara nao retorna nem no findAll.
    @Test
    @Transactional
    public void inativarUmDosCartoesDeUsuarioCom2Cartoes() throws URISyntaxException {

        // Configurcao do usuario q vai ter o cartao alterado
        CreditCard cc1 = new CreditCard();
        cc1.setToken("4321");
        cc1.setSuffix("67730934681683053");
        cc1.setSecurityCode("321");
        cc1.setExpirationDate(Timestamp.valueOf(LocalDateTime.of(2018, 11, 11, 0, 0)));
        //cc1.setExpirationDate("11/2018");
        cc1.setVendor("MasterCard");
        cc1.setStatus(CreditCard.Status.ACTIVE);

        CreditCard cc2 = new CreditCard();
        cc2.setToken("1234");
        cc2.setSuffix("67730987357243053");
        cc2.setSecurityCode("123");
        cc2.setExpirationDate(Timestamp.valueOf(LocalDateTime.of(2020, 07, 07, 0, 0)));
        //cc2.setExpirationDate("07/2020");
        cc2.setVendor("visa");
        cc2.setStatus(CreditCard.Status.ACTIVE);

        User u1 = new User();
        u1.setUsername("KILLER card 22 maluco");
        u1.setPassword("109809876");
        u1.setEmail("Killercard22@gmail.com");
        u1.setSourceApp("facebook");
        u1.addCreditCard(cc1);
        u1.addCreditCard(cc2);

        cc1.setOwnerName(u1.getUsername());
        cc2.setOwnerName(u1.getUsername());

        cc1.setUser(u1);
        cc2.setUser(u1);

        userRepository.saveAndFlush(u1);

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
                "\t\t\"username\": \"KILLER337\",\n" +
                "\t    \"password\": \"109809876\",\n" +
                "\t    \"email\": \"Killer33@gmail.com\",\n" +
                "\t    \"sourceApp\": \"facebook\",\n" +
                "\t    \"personType\":\"FÍSICA\",\n" +
                "\t    \"creditCardCollection\": [\n" +
                "\t\t    {\n" +
                "            \"idCreditCard\": "+ idToken1234 +",\n"+
                "\t\t        \"token\": \"ALTERADOOOOOOOOOOOOO\",\n" +
                "\t\t        \"ownerName\": \"Teste\",\n" +
                "\t\t        \"suffix\": \"67730987357243053\",\n" +
                "\t\t        \"securityCode\": \"098\",\n" +
                "\t\t        \"expirationDate\": \""+ Timestamp.valueOf(LocalDateTime.MAX.of(2017, 11, 10, 0, 0)).getTime() +"\",\n" +
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

    public static User createFakeUser(String username, String email) {
        User u = new User();
        u.setEmail(email);
        //u.setUser(1234L);
        u.setPassword("123qwe");
        u.setSourceApp("google+");
        u.setUsername(username);
        u.setPersonType(User.PersonType.FISICA);
        //u.getCustomerCollection().add(c);
        //userRepository.save(u);
        return u;
    }

    @Test
    public void inserirUsuarioNovo() throws URISyntaxException {

        String content = "{\n" +
                "\t\"entity\": \n" +
                "\t{\n" +
                "\t\t\"username\": \"KILLER7\",\n" +
                "\t    \"password\": \"109809876\",\n" +
                "\t    \"email\": \"Killer@gmail.com\",\n" +
                "\t    \"sourceApp\": \"facebook\",\n" +
                "\t    \"personType\": \""+ User.PersonType.FISICA+"\",\n" +
                "\t    \"status\": \"ACTIVE\"\n" +
                "\t}\n" +
                "\t\n" +
                "}";

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/users"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(content);

        ResponseEntity<UserResponseBody> rsp = restTemplate
                .exchange(entity, UserResponseBody.class);

        Assert.assertNotNull(rsp);
        Assert.assertEquals(HttpStatus.OK, rsp.getStatusCode());

        User user = rsp.getBody().getUserList().get(0);

        String contentUpdate = "{\n" +
                "\t\"entity\": \n" +
                "\t{\n" +
                "\t\t\"idLogin\": \""+user.getIdLogin()+"\",\n" +
                "\t\t\"username\": \"KILLER337\",\n" +
                "\t    \"password\": \"109809876\",\n" +
                "\t    \"email\": \"Killer33@gmail.com\",\n" + // Aplicacao nao deve permitir atualizar email pro user.
                "\t    \"sourceApp\": \"facebook\",\n" +
                "\t    \"status\": \"GONE\",\n" +
                "\t    \"personType\":\"FISICA\",\n" +
                "\t    \"goodByeReason\": \"TESTE ALTERADO\"\n" +
                "\t    }\n " +
                "}";

        System.out.println(contentUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/users"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(contentUpdate);

        ResponseEntity<UserResponseBody> rspUpdate = restTemplate
                .exchange(entityUpdate, UserResponseBody.class);

        // Conferindo se o controller executou a operacao com sucesso
        Assert.assertNotNull(rspUpdate);
        Assert.assertEquals("Aplicacao permitiu alterar o e-mail. Esta errado.", HttpStatus.BAD_REQUEST, rspUpdate.getStatusCode());


    }

}
