package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.HabilityRequestBody;
import com.cosmeticos.commons.HabilityResponseBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Hability;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.service.HabilityService;
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
import java.text.ParseException;

/**
 * Classe exclusiva para trabalhar com Mockito pois mocar um bean numa classe de testes que tem testes nao mocados, pode
 * gerar resultados inesperados no teste nao mocado.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingHabilityControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private HabilityService mockedHabilityService;

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
				mockedHabilityService.create(Mockito.anyObject())
		).thenThrow(new RuntimeException());

		HabilityRequestBody requestBody = createFakeRequestBody();

		final ResponseEntity<HabilityResponseBody> exchange = //
				restTemplate.exchange( //
						"/habilities", //
						HttpMethod.POST, //
						new HttpEntity(requestBody), // Body
						HabilityResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
	}

	private HabilityRequestBody createFakeRequestBody() {
		Address address = createFakeAddress();
		User user = createFakeUser();

		Professional p = new Professional();
		p.setAddress(address);
		p.setUser(user);

		Hability hability = new Hability("test hability");
		hability.getProfessionalCollection().add(p);

		HabilityRequestBody requestBody = new HabilityRequestBody();
		requestBody.setHability(hability);

		return requestBody;
	}

	public User createFakeUser() {
		User u = new User();
		u.setEmail("diego@bol.com");
		u.setPassword("123qwe");
		u.setSourceApp("google+");

		return u;
	}

	private Address createFakeAddress() {
		Address a = new Address();
		a.setAddress("Rua Perlita");
		a.setCep("0000000");
		a.setCity("RJO");
		a.setCountry("BRA");
		a.setNeighborhood("Austin");
		a.setState("RJ");

		return a;
	}
}
