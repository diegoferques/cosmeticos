package com.cosmeticos;

import com.cosmeticos.commons.CustomerResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;

/**
 * Created by matto on 28/06/2017.
 */
@ActiveProfiles("prod")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JUnitTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testFindCustomerById() throws ParseException {

        final ResponseEntity<CustomerResponseBody> exchange = //
                restTemplate.exchange( //
                        "/customers/1", //
                        HttpMethod.GET, //
                        null,
                        CustomerResponseBody.class);

        //Assert.assertNull(exchange.getBody().getCustomerList().get(0).getIdCustomer());
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());

    }
}
