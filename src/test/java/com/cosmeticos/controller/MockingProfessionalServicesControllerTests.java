package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.commons.ProfessionalServicesResponseBody;
import com.cosmeticos.commons.google.LocationGoogle;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.model.Category;
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

/**
 * Created by matto on 19/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingProfessionalServicesControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private LocationService locationService;

    @Autowired
    private CategoryRepository serviceRepository;

    //@Ignore
    @Test
    public void testNearbyWithDistance() throws ParseException, URISyntaxException {


        Category s1 = new Category();
        s1.setName("testNearbyWithDistance-service");
        serviceRepository.save(s1);

        LocationGoogle sourceLocation = new LocationGoogle();
        sourceLocation.setLat(-22.7387053);
        sourceLocation.setLng(-43.5109277);

        Mockito.when(
                locationService.getGeoCode(Mockito.any())
        ).thenReturn(sourceLocation);

        String emailUsuario = "nearby@email.com";


        String json = "{\n" +
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
                "    \"status\": null,\n" +
                "    \"user\": {\n" +
                "      \"email\": \""+ emailUsuario +"\",\n" +
                "      \"idLogin\": null,\n" +
                "      \"password\": \"123\",\n" +
                "      \"sourceApp\": null,\n" +
                "      \"username\": \""+ emailUsuario +"\"\n" +
                "    },\n" +
                "    \"cnpj\": \"05404277726\",\n" +
                "    \"idProfessional\": null,\n" +
                "    \"location\": 506592589,\n" +
                "    \"nameProfessional\": \"aaa\",\n" +
                "    \"professionalServicesCollection\": [\n" +
                "      {\n" +
                "        \"professional\": null,\n" +
                "        \"service\": {\n" +
                "          \"category\": \"testNearbyWithDistance-service\",\n" +
                "          \"idService\": "+s1.getIdCategory()+"\n" +
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

        Professional professional = exchange.getBody().getProfessionalList().get(0);

        Assert.assertNotNull(professional);
        Assert.assertNotNull(professional.getAddress());
        Assert.assertNotNull(professional.getAddress().getLatitude());
        Assert.assertNotNull(professional.getAddress().getLongitude());

        final ResponseEntity<ProfessionalServicesResponseBody> getExchange = //
                restTemplate.exchange( //
                        "/professionalservices/nearby/?service.category=testNearbyWithDistance-service" +

                                // Coordenadas do cliente: Casa do garry
                                "&latitude=-22.7331757&longitude=-43.5209273" +

                                "&radius=6000",
                        HttpMethod.GET, //
                        null,
                        ProfessionalServicesResponseBody.class);

        List<ProfessionalServices> entityList = getExchange.getBody().getProfessionalServicesList();

        Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());
        Assert.assertTrue("Nao foram retornados profissionais.", entityList.size() > 0);

        for (int i = 0; i < entityList.size(); i++) {
            ProfessionalServices ps =  entityList.get(i);

            Professional p = ps.getProfessional();
            Category s = ps.getCategory();

            Assert.assertNotNull("ProfessionalServices deve ter Servico e Profissional", p);
            Assert.assertEquals("testNearbyWithDistance-service", s.getName());
            Assert.assertNotNull("Professional deve ter distance setado", p.getDistance());
        }
    }


}
