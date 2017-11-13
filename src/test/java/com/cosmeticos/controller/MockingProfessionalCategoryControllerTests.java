package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalCategoryResponseBody;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.commons.google.LocationGoogle;
import com.cosmeticos.model.Category;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalCategory;
import com.cosmeticos.repository.CategoryRepository;
import com.cosmeticos.service.LocationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 19/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingProfessionalCategoryControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private LocationService locationService;

    @Autowired
    private CategoryRepository serviceRepository;

    //@Ignore
    @Test
    public void testNearbyWithDistance() throws ParseException, URISyntaxException {

        String emailLista = "lista@email.com";
        String emailNaoLista = "naolista@email.com";

        Category s1 = new Category();
        s1.setName("testNearbyWithDistance");
        serviceRepository.save(s1);

        //R. Perlita, 42 - Rodilândia, Nova Iguaçu - RJ, 26083-290, Brazil
        LocationGoogle sourceLocation = new LocationGoogle();
        sourceLocation.setLat(-22.7373681);
        sourceLocation.setLng(-43.5091621);

        Mockito.when(
                locationService.getGeoCode(Mockito.any())
        ).thenReturn(sourceLocation);

        CreateProfessionalThatWontReturn createProfessionalThatWontReturn = new CreateProfessionalThatWontReturn().invoke(s1, emailNaoLista);
        CreateProfessionalThatWillReturn professionalThatWillReturn = new CreateProfessionalThatWillReturn().invoke(s1, emailLista);

        ResponseEntity<ProfessionalResponseBody> exchange = createProfessionalThatWontReturn.exchange;

        Professional professional = exchange.getBody().getProfessionalList().get(0);

        Assert.assertNotNull(professional);
        Assert.assertNotNull(professional.getAddress());
        Assert.assertNotNull(professional.getAddress().getLatitude());
        Assert.assertNotNull(professional.getAddress().getLongitude());

        final ResponseEntity<ProfessionalCategoryResponseBody> getExchange = //
                restTemplate.exchange( //
                        "/professionalcategories/nearby/?category.name=testNearbyWithDistance" +

                                // Coordenadas do cliente: Casa do garry
                                "&latitude=-22.7331757&longitude=-43.5209273" +

                                "&radius=6000",
                        HttpMethod.GET, //
                        null,
                        ProfessionalCategoryResponseBody.class);

        List<ProfessionalCategory> entityList = getExchange.getBody().getProfessionalCategoryList();

        ///////////////////////////////////////////////////////////////////////////////////
        ///// Checando a integridade dos dados retornados//////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////
        Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());
        Assert.assertTrue("Nao foram retornados profissionais.", entityList.size() > 0);

        for (int i = 0; i < entityList.size(); i++) {
            ProfessionalCategory ps =  entityList.get(i);

            Professional p = ps.getProfessional();
            Category s = ps.getCategory();

            Assert.assertNotNull("ProfessionalServices deve ter Servico e Profissional", p);
            Assert.assertEquals("testNearbyWithDistance", s.getName());
            Assert.assertNotNull("Professional deve ter distance setado", p.getDistance());
            Assert.assertNotNull("Evaluation nao esta sendo exibido", p.getUser().getEvaluation());
        }


        ///////////////////////////////////////////////////////////////////////////////////
        ///// Checando se usuario sem pricerule eh omitido ////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////
        Optional<ProfessionalCategory> professionalThatShouldNotBeEnlisted = entityList.stream()
                .filter(pc -> pc.getProfessional().getUser().getEmail().equals(emailNaoLista))
                .findFirst();

        Assert.assertEquals(emailNaoLista + " nao deveria ter retornado no nearby pois nao possui pricerule.",
                Optional.empty(),
                professionalThatShouldNotBeEnlisted);

        ///////////////////////////////////////////////////////////////////////////////////
        ///// Checando se quem deveria retornar realmente retornou no nearby //////////////
        ///////////////////////////////////////////////////////////////////////////////////
        Optional<ProfessionalCategory> professionalThatShouldBeEnlisted = entityList.stream()
                .filter(pc -> pc.getProfessional().getUser().getEmail().equals(emailLista))
                .findFirst();

        Assert.assertTrue(emailLista + " nao deveria ter retornado no nearby pois nao possui pricerule.",
                professionalThatShouldBeEnlisted.isPresent());
    }


    private class CreateProfessionalThatWontReturn {

        private ResponseEntity<ProfessionalResponseBody> exchange;

        public CreateProfessionalThatWontReturn invoke(Category s1, String email) throws URISyntaxException {

            String jsonDoProfissionalQueNaoRetornaraPorNaoTerPriceRule = "{\n" +
                    "  \"professional\": {\n" +
                    "    \"address\": { \n" +
                    "	    \"address\": \"Avenida dos Metalúrgicos, 22\",\n" +
                    "	    \"cep\": \"26083-275\",\n" +
                    "	    \"neighborhood\": \"Rodilândia\",\n" +
                    "	    \"city\": \"Nova Iguaçu\",\n" +
                    "	    \"state\": \"RJ\",\n" +
                    "	    \"country\": \"BR\" ,\n" +
                    "	    \"complement\": \"BR\" \n" +
                    "    },\n" +
                    "    \"birthDate\": 1120705200000,\n" +
                    "    \"cellPhone\": null,\n" +
                    "    \"dateRegister\": null,\n" +
                    "    \"genre\": null,\n" +
                    "    \"status\": 1,\n" +
                    "    \"user\": {\n" +
                    "      \"email\": \""+ email +"\",\n" +
                    "      \"idLogin\": null,\n" +
                    "      \"password\": \"123\",\n" +
                    "      \"sourceApp\": null,\n" +
                    "      \"username\": \""+ email +"\",\n" +
                    "      \"personType\": \"FISICA\"\n" +
                    "    },\n" +
                    "    \"cnpj\": \"05404277726\",\n" +
                    "    \"idProfessional\": null,\n" +
                    "    \"location\": 506592589,\n" +
                    "    \"nameProfessional\": \"aaa\",\n" +
                    "    \"professionalCategoryCollection\": [\n" +
                    "      {\n" +


                    "       \"professional\" : {\n" +
                    "        \"user\" : {\n" +
                    "            \"voteCollection\" : [\n" +
                    "                {\n" +
                    "                    \"value\" : 2\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "       },\n" +


                    "        \"category\": {\n" +
                    "          \"name\": \"testNearbyWithDistance-service\",\n" +
                    "          \"idCategory\": "+s1.getIdCategory()+"\n" +
                    "        }\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

            System.out.println(jsonDoProfissionalQueNaoRetornaraPorNaoTerPriceRule);


            RequestEntity<String> entity =  RequestEntity
                    .post(new URI("/professionals"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(jsonDoProfissionalQueNaoRetornaraPorNaoTerPriceRule);

            exchange = restTemplate
                    .exchange(entity, ProfessionalResponseBody.class);
            return this;
        }
    }

    private class CreateProfessionalThatWillReturn {

        private ResponseEntity<ProfessionalResponseBody> exchange;

        public CreateProfessionalThatWillReturn invoke(Category s1, String email) throws URISyntaxException {

            String jsonDoProfissionalQueNaoRetornaraPorNaoTerPriceRule = "{\n" +
                    "  \"professional\": {\n" +
                    "    \"address\": { \n" +
                    "	    \"address\": \"Avenida dos Metalúrgicos, 22\",\n" +
                    "	    \"cep\": \"26083-275\",\n" +
                    "	    \"neighborhood\": \"Rodilândia\",\n" +
                    "	    \"city\": \"Nova Iguaçu\",\n" +
                    "	    \"state\": \"RJ\",\n" +
                    "	    \"country\": \"BR\" ,\n" +
                    "	    \"complement\": \"BR\" \n" +
                    "    },\n" +
                    "    \"birthDate\": 1120705200000,\n" +
                    "    \"cellPhone\": null,\n" +
                    "    \"dateRegister\": null,\n" +
                    "    \"genre\": null,\n" +
                    "    \"status\": 1,\n" +
                    "    \"user\": {\n" +
                    "      \"email\": \""+ email +"\",\n" +
                    "      \"idLogin\": null,\n" +
                    "      \"password\": \"123\",\n" +
                    "      \"sourceApp\": null,\n" +
                    "      \"username\": \""+ email +"\",\n" +
                    "      \"personType\": \"FISICA\"\n" +
                    "    },\n" +
                    "    \"cnpj\": \"05404277726\",\n" +
                    "    \"idProfessional\": null,\n" +
                    "    \"location\": 506592589,\n" +
                    "    \"nameProfessional\": \"aaa\",\n" +
                    "    \"professionalCategoryCollection\": [\n" +
                    "      {\n" +

                    "       \"priceRuleList\": [\n" +
                    "           {\n" +
                    "             \"name\": \"COMPRIMENTO ATÉ 10cm\",\n" +
                    "             \"price\": 75.00\n" +
                    "           }\n" +
                    "       ],\n" +

                    "       \"professional\" : {\n" +
                    "        \"user\" : {\n" +
                    "            \"voteCollection\" : [\n" +
                    "                {\n" +
                    "                    \"value\" : 2\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "       },\n" +

                    "        \"category\": {\n" +
                    "          \"idCategory\": "+s1.getIdCategory()+"\n" +
                    "        }\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

            System.out.println(jsonDoProfissionalQueNaoRetornaraPorNaoTerPriceRule);


            RequestEntity<String> entity =  RequestEntity
                    .post(new URI("/professionals"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(jsonDoProfissionalQueNaoRetornaraPorNaoTerPriceRule);

            exchange = restTemplate
                    .exchange(entity, ProfessionalResponseBody.class);
            return this;
        }
    }
}
