package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import com.cosmeticos.service.CustomerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private CustomerService service;

	@Test
	public void testCreateOK() throws IOException {


		String content = new String(Files.readAllBytes(Paths.get("C:\\dev\\_freelas\\Deivison\\projetos\\cosmeticos\\src\\test\\resources\\custumerPostRequest.json")));

		Customer customer = createFakeCustomer();
		Address addres = createFakeAddress(customer);
		User user = createFakeLogin(customer);

		CustomerRequestBody requestBody = new CustomerRequestBody();
		requestBody.setAddress(addres);
		requestBody.setUser(user);
		requestBody.setCustomer(customer);

		CustomerResponseBody rsp = restTemplate.postForObject("/customers", content, CustomerResponseBody.class);

		final ResponseEntity<ScheduleResponseBody> exchange = //
				restTemplate.exchange( //
						"/customers", //
						HttpMethod.POST, //
						new HttpEntity(requestBody), // Body
						ScheduleResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
	}


	@Test
	public void testCreateError500() throws IOException {

		Mockito.when(
				service.create(Mockito.anyObject())
		).thenThrow(new RuntimeException());

		Customer customer = createFakeCustomer();
		Address addres = createFakeAddress(customer);
		User user = createFakeLogin(customer);

		CustomerRequestBody requestBody = new CustomerRequestBody();
		requestBody.setAddress(addres);
		requestBody.setUser(user);
		requestBody.setCustomer(customer);

		final ResponseEntity<CustomerResponseBody> exchange = //
				restTemplate.exchange( //
						"/customers", //
						HttpMethod.POST, //
						new HttpEntity(requestBody), // Body
						CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
	}




	private User createFakeLogin(Customer c) {
		User u = new User();
		u.setEmail("diego@bol.com");
		u.setIdLogin(1234L);
		u.setPassword("123qwe");
		u.setSourceApp("google+");
		u.setUsername("diegoferques");

		u.setCustomerCollection(new ArrayList<>());
		u.getCustomerCollection().add(c);
		return u;
	}

	private Address createFakeAddress(Customer customer) {
		Address a = new Address();
		a.setAddress("Rua Perlita");
		a.setCep("0000000");
		a.setCity("RJO");
		a.setCountry("BRA");
		a.setNeighborhood("Austin");
		a.setState("RJ");


		a.setCustomerCollection(new ArrayList<>());
		a.getCustomerCollection().add(customer);
		return a;
	}


	private Customer createFakeCustomer() {
		Customer c4 = new Customer();
		c4.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1991, 10, 21, 0, 0, 0)));
		c4.setCellPhone("(21) 99887-7665");
		c4.setCpf("816.810.695-68");
		c4.setDateRegister(Calendar.getInstance().getTime());
		c4.setGenre('F');
		c4.setNameCustomer("Fernanda Cavalcante");
		c4.setServiceRequestCollection(null);
		c4.setStatus(Customer.Status.INACTIVE.ordinal());
		return c4;
	}
}
