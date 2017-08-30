package com.cosmeticos.subscription.test;

import com.cosmeticos.Application;
import com.cosmeticos.subscription.product.Product;
import com.cosmeticos.subscription.product.ProductRepository;
import com.cosmeticos.subscription.product.ProductResponseBody;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Created by Vinicius on 18/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setup() {

    }

    @Test
    public void createProductOk() throws URISyntaxException {

        String content = "{\n" +
                "  \"entity\" : {\n" +
                "    \"nameProduct\" : \"Professional 2\",\n" +
                "    \"descriptionProduct\" : \"Taxa de cadastro e taxa à cada 3 prestações de serviço do profissioanl.\", \n" +
                "    \"statusProduct\" : \"ACTIVE\"\n" +
                "  }\n" +
                "}";

        System.out.println(content);

        RequestEntity<String> entity = RequestEntity
                .post(new URI("/products"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(content);

        ResponseEntity<ProductResponseBody> exchange = restTemplate
                .exchange(entity, ProductResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //Assert.assertEquals(Product.statusProduct.ACTIVE.ordinal(), exchange.getStatusCode());
        //Assert.assertNotNull(exchange.getBody().getProductList().get(0));
    }

    @Test
    public void testGetAll() throws ParseException {

        Product p1 = new Product();
        p1.setNameProduct("Professional listado");
        p1.setDescriptionProduct("Taxa de cadastro e taxa à cada 3 prestações de serviço do profissioanl.");

        productRepository.save(p1);

        final ResponseEntity<ProductResponseBody> getExchange = //
                restTemplate.exchange( //
                        "/products", //
                        HttpMethod.GET, //
                        null,
                        ProductResponseBody.class);

        Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());

    }
    @Test
    public void testGetById() throws ParseException, URISyntaxException {

// menos 1rsrsrsrs proximo...update
        String content = "{\n" +
                "  \"entity\" : {\n" +
                "    \"nameProduct\" : \"Professional 2\",\n" +
                "    \"descriptionProduct\" : \"Taxa de cadastro e taxa à cada 3 prestações de serviço do profissioanl.\", \n" +
                "    \"statusProduct\" : \"ACTIVE\"\n" +
                "  }\n" +
                "}";

        System.out.println(content);

        RequestEntity<String> entity = RequestEntity
                .post(new URI("/products"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(content);

        ResponseEntity<ProductResponseBody> exchange = restTemplate
                .exchange(entity, ProductResponseBody.class);

        ProductResponseBody responseBody = exchange.getBody();

        Product createdProduct = responseBody.getProductList().get(0);


        final ResponseEntity<ProductResponseBody> getExchange = //
                restTemplate.exchange( //
                        "/products/" + createdProduct.getIdProduct(),
                        HttpMethod.GET,
                        null,
                        ProductResponseBody.class);

        Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());
        Assert.assertTrue(!getExchange.getBody().getProductList().isEmpty());

    }
    @Test
    public void updateProductOk() throws URISyntaxException {

        /////// Cadastro do product que será alterado ///////////////////
        String postContent = "{\n" +
                "  \"entity\" : {\n" +
                "    \"nameProduct\" : \"Professional 2\",\n" +
                "    \"descriptionProduct\" : \"Taxa de cadastro e taxa à cada 3 prestações de serviço do profissioanl.\", \n" +
                "    \"statusProduct\" : \"ACTIVE\"\n" +
                "  }\n" +
                "}";

        System.out.println(postContent);

        RequestEntity<String> entity = RequestEntity
                .post(new URI("/products"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(postContent);

        ResponseEntity<ProductResponseBody> exchange = restTemplate
                .exchange(entity, ProductResponseBody.class);

        ProductResponseBody responseBody = exchange.getBody();

        Product product = responseBody.getProductList().get(0);


        /////// Alteracao do Product criado acima ///////////////////////??
        String putContent = "{\n" +
                "  \"entity\" : {\n" +
                "    \"idProduct\" : "+product.getIdProduct() +",\n" +
                "    \"nameProduct\" : \"Produto ATUALIZADO\",\n" +
                "    \"descriptionProduct\" : \"Taxa de cadastro e taxa à cada 3 prestações de serviço do profissioanl.\", \n" +
                "    \"statusProduct\" : \"ACTIVE\"\n" +
                "  }\n" +
                "}";

        System.out.println(putContent);

        RequestEntity<String> putEntity = RequestEntity
                .put(new URI("/products"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(putContent);

        ResponseEntity<ProductResponseBody> putExchange = restTemplate
                .exchange(putEntity, ProductResponseBody.class);

        ProductResponseBody putResponseBody = putExchange.getBody();

        Product updatedProduct = putResponseBody.getProductList().get(0);

        Assert.assertNotNull(putExchange);
        Assert.assertEquals(HttpStatus.OK, putExchange.getStatusCode());
        Assert.assertEquals("Produto ATUALIZADO", updatedProduct.getNameProduct());
        //Assert.assertEquals(Product.statusProduct.ACTIVE.ordinal(), exchange.getStatusCode());
        //Assert.assertNotNull(exchange.getBody().getProductList().get(0));
    }

}