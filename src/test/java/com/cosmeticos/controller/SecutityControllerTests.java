package com.cosmeticos.controller;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CustomerResponseBody;

/**
 * Created by matto on 19/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecutityControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testUnsecureApiCustomersGetOk() throws ParseException {

        final ResponseEntity<CustomerResponseBody> exchange = //
                restTemplate.exchange( //
                        "/customers", //
                        HttpMethod.GET, //
                        null,
                        CustomerResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void testUnsecureWebStaticHomeOk() throws ParseException {

        final ResponseEntity<String> exchange = //
                restTemplate.exchange( //
                        "/home.html", //
                        HttpMethod.GET, //
                        null,
                        String.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void testSecureWebTemplatesSecureOk() throws ParseException {

        final ResponseEntity<String> exchange = //
                restTemplate.withBasicAuth("user", "123").exchange( //
                        "/secure/teste", //
                        HttpMethod.GET, //
                        null,
                        String.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Ignore // ignorado ate descobrirmos como fazer o h2 funcionar com spring-security
    @Test
    public void testSecureWebTemplatesSecureUnauthorized() throws ParseException {

        final ResponseEntity<String> exchange = //
                restTemplate.exchange( //
                        "/secure/teste", //
                        HttpMethod.GET, //
                        null,
                        String.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange.getStatusCode());
    }

    @Ignore // ignorado ate descobrirmos como fazer o h2 funcionar com spring-security
    @Test
    public void testSecureWebTemplatesSecureAuthenticateError() throws ParseException {

        final ResponseEntity<String> exchange = //
                restTemplate.withBasicAuth("user", "1234").exchange( //
                        "/secure/teste", //
                        HttpMethod.GET, //
                        null,
                        String.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange.getStatusCode());
    }
}
