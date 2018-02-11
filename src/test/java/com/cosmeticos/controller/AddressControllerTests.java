package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.AddressResponseBody;
import com.cosmeticos.model.Address;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by matto on 11/02/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddressControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @IfProfileValue(name="integrationTest")
    @Test
    public void LocalizarEnderecoPeloCepChamandoViacepComSucesso() {

        String cep = "26084120";

        final ResponseEntity<AddressResponseBody> exchange = //
                restTemplate.exchange( //
                        "/addresses?cep="+cep,
                        HttpMethod.GET,
                        null,
                        AddressResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        AddressResponseBody addressResponseBody = exchange.getBody();
        Address address = addressResponseBody.getAddress();

        Assert.assertEquals("Rua da Abolição", address.getAddress());
        Assert.assertEquals("Riachão", address.getNeighborhood());
        Assert.assertEquals("Nova Iguaçu", address.getCity());
        Assert.assertEquals("RJ", address.getState());

    }

    @IfProfileValue(name="integrationTest")
    @Test
    public void LocalizarEnderecoPeloCepComTracoChamandoViacepComSucesso() {

        String cep = "26084-120";

        final ResponseEntity<AddressResponseBody> exchange = //
                restTemplate.exchange( //
                        "/addresses?cep="+cep,
                        HttpMethod.GET,
                        null,
                        AddressResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        AddressResponseBody addressResponseBody = exchange.getBody();
        Address address = addressResponseBody.getAddress();

        Assert.assertEquals("Rua da Abolição", address.getAddress());
        Assert.assertEquals("Riachão", address.getNeighborhood());
        Assert.assertEquals("Nova Iguaçu", address.getCity());
        Assert.assertEquals("RJ", address.getState());
    }

}
