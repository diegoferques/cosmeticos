package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ExceptionResponseBody;
import org.junit.Assert;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Vinicius on 02/10/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExceptionControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateExceptionOK() throws URISyntaxException {
        try{
            try{
                throw new IllegalStateException("dei erro");
            } catch(java.lang.Exception e){
                throw new RuntimeException("dei erro de novo");
            }
        }catch(java.lang.Exception e){

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();

            System.out.println(sStackTrace);

            sStackTrace = sStackTrace.replaceAll("\\R", "\\\\n")
                    .replaceAll("\t", "\\t");
            String json = "{\n" +
                    "  \"entity\" : {\n" +
                    "    \"email\" : \"teste@exception\",\n" +
                    "    \"stackTrace\" : \"" + sStackTrace + "\",\n" +
                    "    \"deviceModel\" : \"XING-LING\",\n" +
                    "    \"osVersion\" : \"android\"\n" +
                    "    }\n" +
                    "}";

            System.out.println(json);

            RequestEntity<String> entity =  RequestEntity
                    .post(new URI("/exceptions"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(json);

            ResponseEntity<ExceptionResponseBody> exchange = restTemplate
                    .exchange(entity, ExceptionResponseBody.class);

            Assert.assertNotNull(exchange);
            Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
            Assert.assertNotNull(exchange.getBody().getExceptionList().get(0).getId());

        }


    }
}
