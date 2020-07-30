package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.model.PriceRule;
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
import java.util.Set;

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

        String email = "testPutRuleForProfessional1@email.com";

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

        Long professionalCategoryId = professional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getProfessionalCategoryId();

        String jsonUpdate =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
                "        \"priceRuleList\": [\n" +
                "          {\n" +
                "            \"name\": \"COMPRIMENTO ATÉ 10cm\",\n" +
                "            \"price\": 75.00\n" +
                "          }\n" +
                "          ],\n" +

                // A ideia é que a categoria de id 2 seja apagada e a de de id 1 permaneça registrada.
                "          \"category\": {\n" +
                "            \"idCategory\": 1\n" +
                "          }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Professional persistentProfessional = professionalRepository.findById(professional.getIdProfessional()).get();

        Assert.assertEquals(1, persistentProfessional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());


    }
    @Test
    public void testPutRuleForProfessional2() throws URISyntaxException {

        String email = "testPutRuleForProfessional2@email.com";

        String json = getProfessionalJsonWithoutPriceRule(email, "LUZES");

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

        Long professionalCategoryId = professional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getProfessionalCategoryId();

        String jsonUpdate =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
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

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Professional persistentProfessional = professionalRepository.findById(professional.getIdProfessional()).get();

        Assert.assertEquals(1, persistentProfessional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());


    }

    @Test
    public void testOvewritePutRuleForProfessionalWith2Rules() throws URISyntaxException {

        String email = "testOvewritePutRuleForProfessionalWith2Rules@email.com";

        Professional professional = postJson4CreateProfessional(email);

        Long professionalCategoryId = professional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getProfessionalCategoryId();

        String jsonUpdate =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
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

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Professional persistentProfessional = professionalRepository.findById(professional.getIdProfessional()).get();
        Set<ProfessionalCategory> profCategList2 = persistentProfessional.getProfessionalCategoryCollection();

        // Escolhemos o primeiro cara so por escolher. Podia sser qq um.
        ProfessionalCategory firstProfCateg = profCategList2
                .stream()
                .findFirst()
                .get();

        Assert.assertEquals("Price Rules diferentes",1, firstProfCateg.getPriceRuleList().size());

        // A partir daqui estamos fazendo o ovewritting
        String jsonUpdate2 =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
                "        \"priceRuleList\": [\n" +
                "          {\n" +
                "            \"name\": \"COMPRIMENTO ATÉ 50cm\",\n" +
                "            \"price\": 175.00\n" +
                "          }\n" +
                "          ],\n" +
                "          \"category\": {\n" +
                "            \"idCategory\": 1\n" +
                "          }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate2 = put(jsonUpdate2, restTemplate);

        Assert.assertNotNull(exchangeUpdate2);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate2.getStatusCode());

        Professional persistentProfessional2 = professionalRepository.findById(professional.getIdProfessional()).get();

        Assert.assertEquals(1, persistentProfessional2.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());

    }

    @Test
    public void testPutRuleForProfessionalAdding2Rules() throws URISyntaxException {

        String email = "testPutRuleForProfessionalAdding2Rules@email.com";

        Professional professional = postJson4CreateProfessional(email);

        Long professionalCategoryId = professional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getProfessionalCategoryId();

        String jsonUpdate =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
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

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Professional persistentProfessional = professionalRepository.findById(professional.getIdProfessional()).get();
        Set<ProfessionalCategory> profCategList2 = persistentProfessional.getProfessionalCategoryCollection();

        // Escolhemos o primeiro cara so por escolher. Podia sser qq um.
        ProfessionalCategory firstProfCateg = profCategList2
                .stream()
                .findFirst()
                .get();

        Assert.assertEquals("Price Rules diferentes",1, firstProfCateg.getPriceRuleList().size());

        PriceRule insertedPriceRule = firstProfCateg.getPriceRuleList()
                .stream().findFirst()
                .get();

        // A partir daqui estamos enviando um pricerule que ja foi cadastrado e mais um novo.
        String jsonUpdate2 =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
                "        \"priceRuleList\": [\n" +
                "               {\n" +
                "                 \"id\": "+insertedPriceRule.getId()+",\n" +
                "                 \"name\": \""+insertedPriceRule.getName()+"\",\n" +
                "                 \"price\": "+insertedPriceRule.getPrice()+"\n" +
                "               },\n" +
                "               {\n" +
                "                 \"name\": \"COMPRIMENTO ATÉ 50cm\",\n" +
                "                 \"price\": 175.00\n" +
                "               }\n" +
                "          ],\n" +
                "          \"category\": {\n" +
                "            \"idCategory\": 1\n" +
                "          }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        ResponseEntity<ProfessionalResponseBody> exchangeUpdate2 = put(jsonUpdate2, restTemplate);

        Assert.assertNotNull(exchangeUpdate2);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate2.getStatusCode());

        Professional persistentProfessional2 = professionalRepository.findById(professional.getIdProfessional()).get();

        Assert.assertEquals("Não foi adicionado pricerule, como esparado", 2, persistentProfessional2.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());

    }

    @Test
    public void testPutRuleForProfessionalRemoving1Rule() throws URISyntaxException {

        String email = "testPutRuleForProfessionalRemoving1Rule@email.com";

        // Insere profissional
        Professional professional = postJson4CreateProfessional(email);

        Long professionalCategoryId = professional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getProfessionalCategoryId();

        String jsonUpdate =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
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

        // Inclui price rule ao profissional inserido
        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Professional persistentProfessional = professionalRepository.findById(professional.getIdProfessional()).get();
        Set<ProfessionalCategory> profCategList2 = persistentProfessional.getProfessionalCategoryCollection();

        // Escolhemos o primeiro cara so por escolher. Podia sser qq um.
        ProfessionalCategory firstProfCateg = profCategList2
                .stream()
                .findFirst()
                .get();

        Assert.assertEquals("Price Rules diferentes",1, firstProfCateg.getPriceRuleList().size());

        PriceRule insertedPriceRule = firstProfCateg.getPriceRuleList()
                .stream().findFirst()
                .get();

        // A partir daqui estamos enviando um pricerule que ja foi cadastrado e mais um novo.
        String jsonUpdate2 =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
                "        \"priceRuleList\": [\n" +
                "               {\n" +
                "                 \"id\": "+insertedPriceRule.getId()+",\n" +
                "                 \"name\": \""+insertedPriceRule.getName()+"\",\n" +
                "                 \"price\": "+insertedPriceRule.getPrice()+"\n" +
                "               },\n" +
                "               {\n" +
                "                 \"name\": \"COMPRIMENTO ATÉ 50cm\",\n" +
                "                 \"price\": 175.00\n" +
                "               }\n" +
                "          ],\n" +
                "          \"category\": {\n" +
                "            \"idCategory\": 1\n" +
                "          }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        // Insere um segundo preco
        ResponseEntity<ProfessionalResponseBody> exchangeUpdate2 = put(jsonUpdate2, restTemplate);

        Assert.assertNotNull(exchangeUpdate2);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate2.getStatusCode());

        Professional persistentProfessional2 = professionalRepository.findById(professional.getIdProfessional()).get();

        Assert.assertEquals("Não foi adicionado pricerule, como esparado", 2, persistentProfessional2.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());



        ////////////////////////////////////////////////////////
        ////// Removendo um preço //////////////////////////////
        ////////////////////////////////////////////////////////

        // A partir daqui estamos enviando um pricerule que ja foi cadastrado e mais um novo.
        String jsonUpdate3 =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
                "        \"priceRuleList\": [\n" +
                "               {\n" +
                "                 \"id\": "+insertedPriceRule.getId()+",\n" +
                "                 \"name\": \""+insertedPriceRule.getName()+"\",\n" +
                "                 \"price\": "+insertedPriceRule.getPrice()+"\n" +
                "               }\n" +
                "          ],\n" +
                "          \"category\": {\n" +
                "            \"idCategory\": 1\n" +
                "          }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        // Remove o segundo preco
        ResponseEntity<ProfessionalResponseBody> exchangeUpdate3 = put(jsonUpdate3, restTemplate);

        Assert.assertNotNull(exchangeUpdate3);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate3.getStatusCode());

        Professional persistentProfessional3 = professionalRepository.findById(professional.getIdProfessional()).get();

        Set<ProfessionalCategory> professionalCategories3 = persistentProfessional3.getProfessionalCategoryCollection();
        ProfessionalCategory professionalCategory3 = professionalCategories3.stream()
                .findFirst()
                .get();

        Assert.assertEquals(
                "Não foi removido pricerule, como esperado",
                1,
                professionalCategory3.getPriceRuleList().size()
        );

        PriceRule remainingPriceRule = professionalCategory3.getPriceRuleList()
                .stream()
                .findFirst()
                .get();

        Assert.assertEquals(
                "Removida pricerule errada",
                insertedPriceRule.getName(),
                remainingPriceRule.getName()
        );

    }

    @Test
    public void testPutRuleForProfessionalIgnoring() throws URISyntaxException {

        String email = "testPutRuleForProfessionalIgnoring@email.com";

        // Insere profissional
        Professional professional = postJson4CreateProfessional(email);

        Long professionalCategoryId = professional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getProfessionalCategoryId();

        String jsonUpdate =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
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

        // Inclui price rule ao profissional inserido
        ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

        Professional persistentProfessional = professionalRepository.findById(professional.getIdProfessional()).get();
        Set<ProfessionalCategory> profCategList2 = persistentProfessional.getProfessionalCategoryCollection();

        // Escolhemos o primeiro cara so por escolher. Podia sser qq um.
        ProfessionalCategory firstProfCateg = profCategList2
                .stream()
                .findFirst()
                .get();

        Assert.assertEquals("Price Rules diferentes",1, firstProfCateg.getPriceRuleList().size());

        PriceRule insertedPriceRule = firstProfCateg.getPriceRuleList()
                .stream().findFirst()
                .get();

        // A partir daqui estamos enviando um pricerule que ja foi cadastrado e mais um novo.
        String jsonUpdate2 =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
                "        \"priceRuleList\": [\n" +
                "               {\n" +
                "                 \"id\": "+insertedPriceRule.getId()+",\n" +
                "                 \"name\": \""+insertedPriceRule.getName()+"\",\n" +
                "                 \"price\": "+insertedPriceRule.getPrice()+"\n" +
                "               },\n" +
                "               {\n" +
                "                 \"name\": \"COMPRIMENTO ATÉ 50cm\",\n" +
                "                 \"price\": 175.00\n" +
                "               }\n" +
                "          ],\n" +
                "          \"category\": {\n" +
                "            \"idCategory\": 1\n" +
                "          }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        // Insere um segundo preco
        ResponseEntity<ProfessionalResponseBody> exchangeUpdate2 = put(jsonUpdate2, restTemplate);

        Assert.assertNotNull(exchangeUpdate2);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate2.getStatusCode());

        Professional persistentProfessional2 = professionalRepository.findById(professional.getIdProfessional()).get();

        Assert.assertEquals("Não foi adicionado pricerule, como esparado", 2, persistentProfessional2.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());



        ////////////////////////////////////////////////////////
        ////// Ignorando preço //////////////////////////////
        ////////////////////////////////////////////////////////

        // A partir daqui estamos enviando um pricerule que ja foi cadastrado e mais um novo.
        String jsonUpdate3 =  "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": " + professional.getIdProfessional() + ",\n" +
                "    \"professionalCategoryCollection\": [\n" +
                "      {\n" +
                "        \"professionalCategoryId\": "+professionalCategoryId+",\n" +
                "          \"category\": {\n" +
                "            \"idCategory\": 1\n" +
                "          }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        // Ignora preços
        ResponseEntity<ProfessionalResponseBody> exchangeUpdate3 = put(jsonUpdate3, restTemplate);

        Assert.assertNotNull(exchangeUpdate3);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate3.getStatusCode());

        Professional persistentProfessional3 = professionalRepository.findById(professional.getIdProfessional()).get();

        Set<ProfessionalCategory> professionalCategories3 = persistentProfessional3.getProfessionalCategoryCollection();
        ProfessionalCategory professionalCategory3 = professionalCategories3.stream()
                .findFirst()
                .get();

        Assert.assertEquals(
                "Passar priceRuleList null nao funcionou como esperado. Nenhuma deveria ter sido removida ou adicionada",
                0, // Passar preços nulo, remove-os
                professionalCategory3.getPriceRuleList().size()
        );

    }

    private Professional postJson4CreateProfessional(String email) throws URISyntaxException {
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

        return exchange.getBody().getProfessionalList().get(0);
    }

    @Test
    public void testRuleIgualEntreProfessional() throws URISyntaxException {

        String email = "testRuleIgualEntreProfessional@email.com";

        String json = getProfessionalJsonWithoutPriceRule(email, "LUZES");

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

        Long professionalCategoryId1 = professional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getProfessionalCategoryId();

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

        Professional persistentProfessional = professionalRepository.findById(professional.getIdProfessional()).get();

        Assert.assertEquals(1, persistentProfessional.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());


        String email2 = "testRuleIgualEntreProfessionalRNF58@email.com";

        String json2 = getProfessionalJsonWithoutPriceRule(email2, "LUZES");

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
        Long professionalCategoryId2 = professional2.getProfessionalCategoryCollection().stream()
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

        Professional persistentProfessional2 = professionalRepository.findById(professional2.getIdProfessional()).get();

        Assert.assertEquals(1, persistentProfessional2.getProfessionalCategoryCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRuleList().size());
    }

    public static String getProfessionalJsonWithoutPriceRule(String email, String category) {
        String json =
                "{\n" +
                        "  \"professional\": {\n" +
                        "    \"address\": {\n" +
                                // Rua da abolicao, 5, austin
                        "        \"latitude\": \"-22.7245761\", " +
                        "        \"longitude\": \"-43.51020159999999\"" +
                        "    },\n" +
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
                        "          \"name\": \""+category+"\",\n" +
                        "          \"idCategory\": 1\n" +
                        "        }\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}";

        System.out.println(json);

        return json;
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
