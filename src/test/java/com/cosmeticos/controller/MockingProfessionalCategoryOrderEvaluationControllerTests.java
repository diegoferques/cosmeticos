package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.commons.ProfessionalCategoryResponseBody;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.commons.google.LocationGoogle;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.CategoryRepository;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.service.LocationService;
import org.junit.Assert;
import org.junit.Before;
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
public class MockingProfessionalCategoryOrderEvaluationControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private LocationService locationService;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    Customer c1;
    Professional professional;
    ProfessionalCategory ps1;

    PriceRule pr = null;

    @Before
    public void setUp() throws Exception {

        // Nao sei que lugar eh esse..... Acho que eh o endereço da casa do Garry.
        LocationGoogle sourceLocation = new LocationGoogle();
        sourceLocation.setLat(-22.7387053);
        sourceLocation.setLng(-43.5109277);

        Mockito.when(
                locationService.getGeoCode(Mockito.any())
        ).thenReturn(sourceLocation);

        c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-createOrderOk" + "-cliente");
        c1.getUser().setEmail(System.nanoTime()+ "-createOrderOk" + "-cliente@bol");
        professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime()+ "-createOrderOk" + "-professional");
        professional.getUser().setEmail(System.nanoTime()+ "-createOrderOk" + "-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Category category = categoryRepository.findByName("PEDICURE");
        category = categoryRepository.findWithSpecialties(category.getIdCategory());


        ps1 = new ProfessionalCategory(professional, category);
        ps1.addPriceRule(pr = new PriceRule("Preco de teste 50 mil reais", 5000000));

        professionalCategoryRepository.save(ps1);
    }

    //@Ignore
    @Test
    public void testNearbyWithDistance() throws ParseException, URISyntaxException {

        //////////////////////////////////////////////////////////////////////////////////
        //////// CRIANDO ORDER  //////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        String json = OrderJsonHelper.buildJsonCreateNonScheduledOrder(
                c1,
                ps1,
                Payment.Type.CASH,
                pr
        );

        ResponseEntity<OrderResponseBody> exchange = post(json);

        Order responsedOrder = exchange.getBody().getOrderList().get(0);

        Long orderId = responsedOrder.getIdOrder();

        //////////////////////////////////////////////////////////////////////////////////
        //////// ATUALIZANDO ORDER PARA ACCEPTED   ///////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        String jsonUpdateAccepted = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderId+",\n" +
                "    \"status\" : "+ Order.Status.ACCEPTED.ordinal() +"\n" +
                "\n}\n" +
                "}";

// todo: FAZER O PUT



        //////////////////////////////////////////////////////////////////////////////////
        //////// ATUALIZANDO ORDER PARA INPROGRESS  //////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        String jsonUpdateInprogress = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderId+",\n" +
                "    \"status\" : "+ Order.Status.INPROGRESS.ordinal() +"\n" +
                "\n}\n" +
                "}";

// todo: FAZER O PUT



        //////////////////////////////////////////////////////////////////////////////////
        //////// ATUALIZANDO ORDER PARA SEMICLOSED  //////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        // Aqui deveria ir a avaliacao do Customer mas estou ignorando isso por enquanto.

        String jsonUpdateSemiclosed = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderId+",\n" +
                "    \"status\" : "+ Order.Status.SEMI_CLOSED.ordinal() +"\n" +
                "\n}\n" +
                "}";

// todo: FAZER O PUT


        //////////////////////////////////////////////////////////////////////////////////
        //////// ATUALIZANDO ORDER PARA SEMICLOSED  //////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        String jsonUpdateReady2charge = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderId+",\n" +
                "    \"status\" : "+ Order.Status.READY2CHARGE.ordinal() +",\n" +


                "    \"professionalCategory\" : {\n" +
                "       \"professional\" : {\n" +
                "        \"user\" : {\n" +
                "            \"voteCollection\" : [\n" +
                "                {\n" +
                "                    \"value\" : 2\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" + //user
                "     }\n" +// professional
                "    }\n" + // professionalCategory


                "\n}\n" +
                "}";

// todo: FAZER O PUT


        //////////////////////////////////////////////////////////////////////////////////
        //////// OBTENDO PROFISSIONAIS DISPONIVEIS NO RAIO RECEBIDO NO NEARBY   //////////
        //////////////////////////////////////////////////////////////////////////////////

        final ResponseEntity<ProfessionalCategoryResponseBody> getExchange = //
                restTemplate.exchange( //
                        "/professionalcategories/nearby/?category.name=testNearbyWithDistance-service" +

                                // Coordenadas do cliente: Casa do garry
                                "&latitude=-22.7331757&longitude=-43.5209273" +

                                "&radius=6000",
                        HttpMethod.GET, //
                        null,
                        ProfessionalCategoryResponseBody.class);

        List<ProfessionalCategory> entityList = getExchange.getBody().getProfessionalCategoryList();

        Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());
        Assert.assertTrue("Nao foram retornados profissionais.", entityList.size() > 0);

        for (int i = 0; i < entityList.size(); i++) {
            ProfessionalCategory ps =  entityList.get(i);

            Professional p = ps.getProfessional();
            Category s = ps.getCategory();

            Assert.assertNotNull("ProfessionalServices deve ter Servico e Profissional", p);
            Assert.assertEquals("testNearbyWithDistance-service", s.getName());
            Assert.assertNotNull("Professional deve ter distance setado", p.getDistance());
            Assert.assertNotNull("Evaluation nao esta sendo exibido", p.getUser().getEvaluation());
        }
    }

    private ResponseEntity<OrderResponseBody> post(String json) throws URISyntaxException {
        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        return restTemplate
                .exchange(entity, OrderResponseBody.class);
    }


}
