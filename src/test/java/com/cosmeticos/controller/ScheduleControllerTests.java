package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ServiceRepository;
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


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScheduleControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProfessionalRepository professionalRepository;

	@Autowired
	private ServiceRepository serviceRepository;

	@Test
	public void testCreateheduleOK() {

		ScheduleRequestBody scheduleRequest = new ScheduleRequestBody();
		scheduleRequest.setScheduleDate(Calendar.getInstance().getTime());
		scheduleRequest.setIdCustomer(1L);
		scheduleRequest.setIdProfessional(1L);
		scheduleRequest.setIdService(1L);

		final ResponseEntity<ScheduleResponseBody> exchange = //
				restTemplate.exchange( //
						"/schedules/", //
						HttpMethod.POST, //
						new HttpEntity(scheduleRequest), // Body
						ScheduleResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
	}
}
