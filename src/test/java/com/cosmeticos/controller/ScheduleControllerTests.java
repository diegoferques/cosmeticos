package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.repository.ScheduleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
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

import java.time.LocalDateTime;
import java.util.Calendar;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScheduleControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void setupTests()
	{

	}

	@Test
	public void testCreateScheduleOK() {

		ScheduleRequestBody billingRequest = new ScheduleRequestBody();
		billingRequest.setOwnerUser("garrydias@gmail.com");
		billingRequest.setScheduleDate(Calendar.getInstance().getTime());

		final ResponseEntity<ScheduleResponseBody> exchange = //
				restTemplate.exchange( //
						"/schedule/", //
						HttpMethod.POST, //
						new HttpEntity(billingRequest), // Body
						ScheduleResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
	}
}
