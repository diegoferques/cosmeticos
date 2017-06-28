package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.commons.ServiceRequestBody;
import com.cosmeticos.commons.ServiceResponseBody;
import com.cosmeticos.model.Service;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;

/**
 * Created by Vinicius on 31/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateServiceOK() {

        Service s = new Service();

        s.setCategory("CONTENTMANAGER");

        ServiceRequestBody request = new ServiceRequestBody();
        request.setEntity(s);

        final ResponseEntity<ServiceResponseBody> exchange = //
                restTemplate.exchange( //
                        "/service/", //
                        HttpMethod.POST, //
                        new HttpEntity(request), // Body
                        ServiceResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }
}
