package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalCategory;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.UserRepository;
import org.junit.Assert;
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
 * Created by Vinicius on 04/11/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfessionalPriceRuleControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private Professional returnOfCreateOK = null;

    private Professional returnOfCreateOKWithAddress = null;

    private String emailUsuario = null;

    private String rule = null;

    @Test
    public void testPutRuleForProfessional1() throws URISyntaxException {

        String email = "professionalServicesRNF58qwert@email.com";

        String json = "{\n" +
                "  \"professional\": {\n" +
                "    \"address\": null,\n" +
                "    \"birthDate\": 1120705200000,\n" +
                "    \"cellPhone\": null,\n" +
                "    \"dateRegister\": null,\n" +
                "    \"genre\": null,\n" +
                "    \"status\": null,\n" +
                "    \"user\": {\n" +
                "      \"email\": \""+ email +"\",\n" +
                "      \"idLogin\": null,\n" +
                "      \"password\": \"123\",\n" +
                "      \"sourceApp\": null,\n" +
                "         \"personType\":\"FISICA\",\n" +
                "      \"username\": \""+ email +"\"\n" +
                "    },\n" +
                "    \"cnpj\": \"05404277726\",\n" +
                "    \"idProfessional\": null,\n" +
                "    \"location\": 506592589,\n" +
                "    \"nameProfessional\": \"aaa\",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professional\": null,\n" +
                "        \"category\": {\n" +
                "          \"name\": \"PINTURA\",\n" +
                "          \"idCategory\": 2\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/professionals"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
                .exchange(entity, ProfessionalResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        Professional professional = exchange.getBody().getProfessionalList().get(0);

        String jsonUpdate = "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
                "        \"priceRule\": [\n" +
                "          {\n" +
                "            \"name\": \"COMPRIMENTO ATÉ 30cm\",\n" +
                "            \"price\": 80.00\n" +
                "          }\n" +
                "          ]\n" +
                "  }\n" +
                "}";

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());


    }
    @Test
    public void testPutRuleForProfessional2() throws URISyntaxException {

        String email = "professionalServicesRNF58ytrewq@email.com";

        String json = "{\n" +
                "  \"professional\": {\n" +
                "    \"address\": null,\n" +
                "    \"birthDate\": 1120705200000,\n" +
                "    \"cellPhone\": null,\n" +
                "    \"dateRegister\": null,\n" +
                "    \"genre\": null,\n" +
                "    \"status\": null,\n" +
                "    \"user\": {\n" +
                "      \"email\": \""+ email +"\",\n" +
                "      \"idLogin\": null,\n" +
                "      \"password\": \"123\",\n" +
                "      \"sourceApp\": null,\n" +
                "         \"personType\":\"FISICA\",\n" +
                "      \"username\": \""+ email +"\"\n" +
                "    },\n" +
                "    \"cnpj\": \"05404277726\",\n" +
                "    \"idProfessional\": null,\n" +
                "    \"location\": 506592589,\n" +
                "    \"nameProfessional\": \"aaa\",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professional\": null,\n" +
                "        \"category\": {\n" +
                "          \"name\": \"LUZES\",\n" +
                "          \"idCategory\": 1\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/professionals"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
                .exchange(entity, ProfessionalResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        Professional professional = exchange.getBody().getProfessionalList().get(0);

        String jsonUpdate = "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
                "        \"priceRule\": [{\n" +
                "            \"name\": \"COMPRIMENTO ATÉ 60cm\",\n" +
                "            \"price\": 200.00\n" +
                "          }]\n" +
                "  }\n" +
                "}";

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());


    }

    @Test
    public void testPutRuleForProfessional3() throws URISyntaxException {

        String email = "professionalServicesRNF58asdfg@email.com";

        String json = "{\n" +
                "  \"professional\": {\n" +
                "    \"address\": null,\n" +
                "    \"birthDate\": 1120705200000,\n" +
                "    \"cellPhone\": null,\n" +
                "    \"dateRegister\": null,\n" +
                "    \"genre\": null,\n" +
                "    \"status\": null,\n" +
                "    \"user\": {\n" +
                "      \"email\": \""+ email +"\",\n" +
                "      \"idLogin\": null,\n" +
                "      \"password\": \"123\",\n" +
                "      \"sourceApp\": null,\n" +
                "         \"personType\":\"FISICA\",\n" +
                "      \"username\": \""+ email +"\"\n" +
                "    },\n" +
                "    \"cnpj\": \"05404277726\",\n" +
                "    \"idProfessional\": null,\n" +
                "    \"location\": 506592589,\n" +
                "    \"nameProfessional\": \"aaa\",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professional\": null,\n" +
                "        \"category\": {\n" +
                "          \"name\": \"LUZES\",\n" +
                "          \"idCategory\": 1\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/professionals"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
                .exchange(entity, ProfessionalResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        Professional professional = exchange.getBody().getProfessionalList().get(0);

        String jsonUpdate = "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
                "        \"priceRule\": [\n" +
                "          {\n" +
                "            \"name\": \"COMPRIMENTO ATÉ 60cm\",\n" +
                "            \"price\": 200.00\n" +
                "          }\n" +
                "          ]\n" +
                "  }\n" +
                "}";

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        String jsonUpdate2 = "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
                "        \"priceRule\": [\n" +
                "          {\n" +
                "            \"name\": \"COMPRIMENTO ATÉ 40cm\",\n" +
                "            \"price\": 150.00\n" +
                "          }\n" +
                "          ]\n" +
                "  }\n" +
                "}";

        System.out.println(jsonUpdate2);


        RequestEntity<String> entityUpdate2 =  RequestEntity
                .put(new URI("/professionals"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate2 = restTemplate
                .exchange(entityUpdate2, ProfessionalResponseBody.class);

        Assert.assertNotNull(exchangeUpdate2);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate2.getStatusCode());

    }

    @Test
    public void testRuleIgualEntreProfessional() throws URISyntaxException {

        String email = "professionalServicesRNF58@email.com";

        String json = "{\n" +
                "  \"professional\": {\n" +
                "    \"address\": null,\n" +
                "    \"birthDate\": 1120705200000,\n" +
                "    \"cellPhone\": null,\n" +
                "    \"dateRegister\": null,\n" +
                "    \"genre\": null,\n" +
                "    \"status\": null,\n" +
                "    \"user\": {\n" +
                "      \"email\": \""+ email +"\",\n" +
                "      \"idLogin\": null,\n" +
                "      \"password\": \"123\",\n" +
                "      \"sourceApp\": null,\n" +
                "         \"personType\":\"FISICA\",\n" +
                "      \"username\": \""+ email +"\"\n" +
                "    },\n" +
                "    \"cnpj\": \"05404277726\",\n" +
                "    \"idProfessional\": null,\n" +
                "    \"location\": 506592589,\n" +
                "    \"nameProfessional\": \"aaa\",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professional\": null,\n" +
                "        \"category\": {\n" +
                "          \"name\": \"LUZES\",\n" +
                "          \"idCategory\": 1\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/professionals"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
                .exchange(entity, ProfessionalResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        Professional professional = exchange.getBody().getProfessionalList().get(0);

        Long  professionalCategoryId1 = professional.getProfessionalCategoryCollection().stream().findFirst().get().getProfessionalCategoryId();
        String jsonUpdate = "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId1+",\n" +
                "        \"priceRuleList\": [\n" +
                "          {\n" +
                "            \"name\": \"COMPRIMENTO ATÉ 10cm\",\n" +
                "            \"price\": 75.00\n" +
                "          }\n" +
                "          ],\n" +
                "        \"category\": {\n" +
                "          \"name\": \"LUZES\",\n" +
                "          \"idCategory\": 1\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Professional persistentProfessional = professionalRepository.findOne(professional.getIdProfessional());

        Assert.assertEquals(1, persistentProfessional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());


        String email2 = "professionalServicesRNF581234567@email.com";

        String json2 = "{\n" +
                "  \"professional\": {\n" +
                "    \"address\": null,\n" +
                "    \"birthDate\": 1120705200000,\n" +
                "    \"cellPhone\": null,\n" +
                "    \"dateRegister\": null,\n" +
                "    \"genre\": null,\n" +
                "    \"status\": null,\n" +
                "    \"user\": {\n" +
                "      \"email\": \""+ email2 +"\",\n" +
                "      \"idLogin\": null,\n" +
                "      \"password\": \"123\",\n" +
                "      \"sourceApp\": null,\n" +
                "         \"personType\":\"FISICA\",\n" +
                "      \"username\": \""+ email2 +"\"\n" +
                "    },\n" +
                "    \"cnpj\": \"05404277726\",\n" +
                "    \"idProfessional\": null,\n" +
                "    \"location\": 506592589,\n" +
                "    \"nameProfessional\": \"aaa\",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professional\": null,\n" +
                "        \"category\": {\n" +
                "          \"name\": \"LUZES\",\n" +
                "          \"idCategory\": 1\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        System.out.println(json2);

        RequestEntity<String> entity2 =  RequestEntity
                .post(new URI("/professionals"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json2);

        ResponseEntity<ProfessionalResponseBody> exchange2 = restTemplate
                .exchange(entity2, ProfessionalResponseBody.class);

        Assert.assertNotNull(exchange2);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

        Professional professional2 = exchange2.getBody().getProfessionalList().get(0);
        Long  professionalCategoryId2 = professional.getProfessionalCategoryCollection().stream()
                .findFirst()
                .get()
                .getProfessionalCategoryId();

        String jsonUpdate2 = "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional2.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId2+",\n" +
                "        \"priceRuleList\": [\n" +
                "          {\n" +
                "            \"name\": \"COMPRIMENTO ATÉ 10cm\",\n" +
                "            \"price\": 75.00\n" +
                "          }\n" +
                "          ],\n" +
                "          \"category\": {\n" +
                "            \"idCategory\": 1\n" +
                "          }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        ResponseEntity<ProfessionalResponseBody> exchangeUpdateRule = put(jsonUpdate2, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateRule.getStatusCode());

        Professional persistentProfessional2 = professionalRepository.findOne(professional2.getIdProfessional());

        Assert.assertEquals(1, persistentProfessional2.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());
    }

    static ResponseEntity<ProfessionalResponseBody> put(String json, TestRestTemplate restTemplate) throws URISyntaxException {
        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .put(new URI("/professionals"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        return restTemplate
                .exchange(entity, ProfessionalResponseBody.class);
    }

}
