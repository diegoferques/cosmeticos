package com.cosmeticos.controller;

import com.cosmeticos.Application;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HabilityControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testCreateOK() throws IOException, URISyntaxException {

		String content = "{\n" +
				"  \"hability\" : {\n" +
				"    \"name\" : \"h\",\n" +
				"\t\n" +
				"    \"service\" : {\n" +
				"      \"idService\" : 1\n" +
				"    },\n" +
				"\t\n" +
				"    \"professionalCollection\" : [ {\n" +
				"      \"idProfessional\" : 1\n" +
				"    } ]\n" +
				"\t\n" +
				"  }\n" +
				"}";

		 RequestEntity<String> entity =  RequestEntity
				 .post(new URI("/habilities"))
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 .body(content);

		ResponseEntity<String> rsp = restTemplate
				.exchange(entity, String.class);

		Assert.assertNotNull(rsp);
		Assert.assertNotNull(rsp.getBody());
		Assert.assertEquals(HttpStatus.OK, rsp.getStatusCode());

		System.out.println(rsp.getBody());
	}

}
