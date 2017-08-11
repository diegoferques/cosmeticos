package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ServiceRepository;
import com.cosmeticos.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;


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
