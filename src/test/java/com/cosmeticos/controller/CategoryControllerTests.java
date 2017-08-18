package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CategoryRequestBody;
import com.cosmeticos.commons.CategoryResponseBody;
import com.cosmeticos.model.Category;
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

/**
 * Created by Vinicius on 31/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateServiceOK() {

        Category s = new Category();

        s.setName("CONTENTMANAGER");

        CategoryRequestBody request = new CategoryRequestBody();
        request.setEntity(s);

        final ResponseEntity<CategoryResponseBody> exchange = //
                restTemplate.exchange( //
                        "/categories/", //
                        HttpMethod.POST, //
                        new HttpEntity(request), // Body
                        CategoryResponseBody.class);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }
}
