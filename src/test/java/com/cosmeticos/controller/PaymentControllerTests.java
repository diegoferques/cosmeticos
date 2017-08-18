package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CampainhaSuperpeyResponseBody;
import com.cosmeticos.model.Category;
import com.cosmeticos.repository.CategoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Before;
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
import java.text.ParseException;

/**
 * Created by matto on 08/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Before
    public void setup()
    {
        Category category = categoryRepository.findByName("PEDICURE");

        if(category == null) {
            category = new Category();
            category.setName("PEDICURE");
            categoryRepository.save(category);
        }
    }

    @Test
    public void testCampainhaBadRequest() throws URISyntaxException, ParseException, JsonProcessingException {

        String numeroTransacao = null;
        String codigoEstabelecimento = "1501698887865";
        String campoLivre1 = "TESTE";

        ResponseEntity<CampainhaSuperpeyResponseBody> exchange = this.executaCampainha(
                numeroTransacao, codigoEstabelecimento, campoLivre1);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());

    }

    @Test
    public void testCampainhaNotFound() throws URISyntaxException, ParseException, JsonProcessingException {

        String numeroTransacao = "9999";
        String codigoEstabelecimento = "1501698887865";
        String campoLivre1 = "TESTE";

        ResponseEntity<CampainhaSuperpeyResponseBody> exchange = this.executaCampainha(
                numeroTransacao, codigoEstabelecimento, campoLivre1);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.NOT_FOUND, exchange.getStatusCode());

    }

    public ResponseEntity<CampainhaSuperpeyResponseBody> executaCampainha(
            String numeroTransacao, String codigoEstabelecimento, String campoLivre1)
            throws URISyntaxException, ParseException, JsonProcessingException {

        String urlCampainha = "/campainha/superpay/";
        urlCampainha += "?numeroTransacao=" + numeroTransacao;
        urlCampainha += "&codigoEstabelecimento=" + codigoEstabelecimento;
        urlCampainha += "&campoLivre1=" + campoLivre1;

        System.out.println(urlCampainha);

        RequestEntity<String> entityCustomer =  RequestEntity
                .post(new URI(urlCampainha))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(null);

        ResponseEntity<CampainhaSuperpeyResponseBody> exchange = restTemplate
                .exchange(entityCustomer, CampainhaSuperpeyResponseBody.class);

        return exchange;
    }




}
