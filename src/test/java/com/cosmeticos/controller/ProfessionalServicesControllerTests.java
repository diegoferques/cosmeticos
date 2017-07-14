package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.commons.ProfessionalServicesResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfessionalServicesControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ProfessionalRepository customerRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Inicializa o H2 com dados iniciais.
	 */
	@Before
	public void setupTests() throws ParseException {

	}

	/**
	 * Depende dos inserts feitos no ServicePreLoadConfiguration e ProfessionalServicesPreLoadConfiguration.
	 * Ignore este teste se o profile de execucao usado nao for o default.
	 * Este endopint testa o ModelAttribute do controller e a api Example do spring-data.
	 * Ver card https://trello.com/c/OMGE90IV
	 * TODO: indenpendentizar este teste.
	 * @throws ParseException
	 */
	@Test
	public void testExampleApiFindByProfessionalServicesServiceCategoryMadeInPreLoads() throws ParseException {

		final ResponseEntity<ProfessionalServicesResponseBody> getExchange = //
				restTemplate.exchange( //
						"/professionalservices?service.category=BRUSH",
						HttpMethod.GET, //
						null,
						ProfessionalServicesResponseBody.class);

		Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());

		ProfessionalServicesResponseBody response = getExchange.getBody();

		List<ProfessionalServices> entityList = response.getProfessionalServicesList();

		Assert.assertTrue("Nao foram retornados profissionais.", entityList.size() > 0);

		for (int i = 0; i < entityList.size(); i++) {
			ProfessionalServices ps =  entityList.get(i);

			Professional p = ps.getProfessional();
			Service s = ps.getService();

			Assert.assertNotNull("ProfessionalServices deve ter Servico e Profissional", p);
			Assert.assertEquals("BRUSH", s.getCategory());

		}


	}

	@Test
	public void testNearby() throws ParseException {

		final ResponseEntity<ProfessionalServicesResponseBody> getExchange = //
				restTemplate.exchange( //
						"/professionalservices/nearby?service.category=teste&sourceCoord=1235,-65422&radius=6000",
						HttpMethod.GET, //
						null,
						ProfessionalServicesResponseBody.class);

		Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());

		ProfessionalServicesResponseBody response = getExchange.getBody();

		List<ProfessionalServices> entityList = response.getProfessionalServicesList();

		Assert.assertTrue("Nao foram retornados profissionais.", entityList.size() > 0);

		for (int i = 0; i < entityList.size(); i++) {
			ProfessionalServices ps =  entityList.get(i);

			Professional p = ps.getProfessional();
			Service s = ps.getService();

			Assert.assertNotNull("ProfessionalServices deve ter Servico e Profissional", p);
			Assert.assertEquals("BRUSH", s.getCategory());

		}


	}

	private ProfessionalRequestBody createFakeRequestBody() {
		Address address = createFakeAddress();
		User user = createFakeUser();

		Professional professional = createFakeProfessional();
		professional.setAddress(address);
		professional.setUser(user);

		ProfessionalRequestBody requestBody = new ProfessionalRequestBody();
		requestBody.setProfessional(professional);

		return requestBody;
	}

	public User createFakeUser() {
		User u = new User();
		u.setEmail("diego@bol.com");
		u.setPassword("123qwe");
		u.setSourceApp("google+");
		u.setUsername("diegoferques");

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

	private Professional createFakeProfessional() {
		Professional c1 = new Professional();
		c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
		c1.setCellPhone("(21) 98877-6655");
		c1.setCnpj("098.765.432-10");
		c1.setDateRegister(Calendar.getInstance().getTime());
		c1.setGenre('M');
		c1.setNameProfessional("João da Silva");
		//c1.setOrderCollection(null);
		c1.setStatus(Professional.Status.ACTIVE);
		c1.setAddress(this.createFakeAddress());
		c1.setUser(this.createFakeUser());

		return c1;
	}
}
