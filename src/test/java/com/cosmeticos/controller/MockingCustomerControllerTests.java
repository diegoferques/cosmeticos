package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import com.cosmeticos.service.CustomerService;
import org.junit.Assert;
import org.junit.Before;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingCustomerControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private CustomerService service;

	/**
	 * Inicializa o H2 com dados iniciais.
	 */
	@Before
	public void setupTests() throws ParseException {

	}

	@Test
	public void testCreateError500() throws IOException {
		/**/
		Mockito.when(
				service.create(Mockito.anyObject())
		).thenThrow(new RuntimeException());

		CustomerRequestBody requestBody = createFakeRequestBody();

		final ResponseEntity<CustomerResponseBody> exchange = //
				restTemplate.exchange( //
						"/customers", //
						HttpMethod.POST, //
						new HttpEntity(requestBody), // Body
						CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
	}

	private CustomerRequestBody createFakeRequestBody() {
		Customer customer = createFakeCustomer();
		Address address = createFakeAddress(customer);
		User user = createFakeLogin(customer);

		CustomerRequestBody requestBody = new CustomerRequestBody();
		requestBody.setAddress(address);
		requestBody.setUser(user);
		requestBody.setCustomer(customer);

		return requestBody;
	}

	public User createFakeLogin(Customer c) {
		User u = new User();
		u.setEmail("diego@bol.com");
		//u.setIdLogin(1234L);
		u.setPassword("123qwe");
		u.setSourceApp("google+");
		u.setUsername("diegoferques");
		//u.getCustomerCollection().add(c);
		return u;
	}

	public Address createFakeAddress(Customer customer) {
		Address a = new Address();
		a.setAddress("Rua Perlita");
		a.setCep("0000000");
		a.setCity("RJO");
		a.setCountry("BRA");
		a.setNeighborhood("Austin");
		a.setState("RJ");
		//a.getCustomerCollection().add(customer);
		return a;
	}

	public Customer createFakeCustomer() {
		Customer c1 = new Customer();
		c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
		c1.setCellPhone("(21) 98877-6655");
		c1.setCpf("098.765.432-10");
		c1.setDateRegister(Calendar.getInstance().getTime());
		c1.setGenre('M');
		c1.setNameCustomer("João da Silva");
		//c1.setOrderCollection(null);
		c1.setStatus(Customer.Status.ACTIVE.ordinal());
		c1.setIdAddress(this.createFakeAddress(c1));
		c1.setIdLogin(this.createFakeLogin(c1));

		return c1;
	}
}
