package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.commons.RoleResponseBody;
import com.cosmeticos.model.Role;
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


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoleControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testCreateOK() {

		Role r = new Role();
		r.setName("CONTENTMANAGER");

		RoleRequestBody request = new RoleRequestBody();
		request.setEntity(r);

		final ResponseEntity<RoleResponseBody> exchange = //
				restTemplate.exchange( //
						"/roles", //
						HttpMethod.POST, //
						new HttpEntity(request), // Body
						RoleResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
	}
}
