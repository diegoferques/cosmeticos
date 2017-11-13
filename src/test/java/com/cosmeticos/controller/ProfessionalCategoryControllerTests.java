package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalCategoryResponseBody;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.CategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.lang.Exception;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfessionalCategoryControllerTests {

	@Rule
	public TestName currentTest = new TestName();

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private CategoryRepository serviceRepository;

	@Autowired
	private ProfessionalRepository professionalRepository;

	private Professional professional;

	private String serviceName = "FOOBAR";

	/**
	 * Inicializa o H2 com dados iniciais.
	 */
	@Before
	public void setupTests() throws ParseException, URISyntaxException {

			Category s1 = new Category();
			s1.setName(currentTest.getMethodName());

			serviceRepository.save(s1);

			String email = "professional-" + currentTest.getMethodName() + "@bol.com";

			String json = "{\n" +
					"  \"professional\": {\n" +
					"    \"address\": null,\n" +
					"    \"birthDate\": 1120705200000,\n" +
					"    \"cellPhone\": null,\n" +
					"    \"dateRegister\": null,\n" +
					"    \"genre\": null,\n" +
					"    \"status\": null,\n" +
					"    \"user\": {\n" +
					"      \"email\": \"" + email + "\",\n" +
					"      \"idLogin\": null,\n" +
					"      \"password\": \"123\",\n" +
					"      \"sourceApp\": null,\n" +
					"      \"personType\": \"" + User.PersonType.JURIDICA + "\",\n" +
					"      \"username\": \"" + email + "\"\n" +
					"    },\n" +
					"    \"cnpj\": \"05404277726\",\n" +
					"    \"idProfessional\": null,\n" +
					"    \"location\": 506592589,\n" +
					"    \"nameProfessional\": \"aaa\",\n" +
					"    \"professionalCategoryCollection\": [\n" +
					"      {\n" +
					"        \"professional\": null,\n" +
					"        \"category\": {\n" +
					"          \"idCategory\": " + s1.getIdCategory() + "\n" +
					"        }\n" +
					"      }\n" +
					"    ]\n" +
					"  }\n" +
					"}";


			RequestEntity<String> entity = RequestEntity
					.post(new URI("/professionals"))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.body(json);

			ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
					.exchange(entity, ProfessionalResponseBody.class);

			Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

			professional = exchange.getBody().getProfessionalList().get(0);

	}

	@Transactional
	@Test
	public void delete() throws Exception {

		Set<ProfessionalCategory> pcListBefore = professional.getProfessionalCategoryCollection();

		int professionalCateogrySizeBefore = pcListBefore.size();

		ProfessionalCategory pc = pcListBefore.stream().findFirst().get();

		restTemplate.delete("/professionalcategories/" + pc.getProfessionalCategoryId());


		professional = professionalRepository.findOne(professional.getIdProfessional());

		Set<ProfessionalCategory> pcListAfter = professional.getProfessionalCategoryCollection();

		int professionalCateogrySizeAfter = pcListAfter.size();

		Assert.assertTrue(professionalCateogrySizeAfter < professionalCateogrySizeBefore);

	}

	/**
	 * Depende dos inserts feitos no ServicePreLoadConfiguration e ProfessionalCategoryPreLoadConfiguration.
	 * Ignore este teste se o profile de execucao usado nao for o default.
	 * Este endopint testa o ModelAttribute do controller e a api Example do spring-data.
	 * Ver card https://trello.com/c/OMGE90IV
	 * TODO: indenpendentizar este teste.
	 * @throws ParseException
	 */
	@Test
	public void testExampleApiFindByProfessionalServicesServiceCategoryMadeInPreLoads() throws ParseException, URISyntaxException {

		final ResponseEntity<ProfessionalCategoryResponseBody> getExchange = //
				restTemplate.exchange( //
						"/professionalcategories?category.name="+currentTest.getMethodName(),
						HttpMethod.GET, //
						null,
						ProfessionalCategoryResponseBody.class);

		Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());

		ProfessionalCategoryResponseBody response = getExchange.getBody();

		List<ProfessionalCategory> entityList = response.getProfessionalCategoryList();

		Assert.assertTrue("Nao foram retornados profissionais.", entityList.size() > 0);

		for (int i = 0; i < entityList.size(); i++) {
			ProfessionalCategory ps =  entityList.get(i);

			Professional p = ps.getProfessional();
			Category s = ps.getCategory();

			Assert.assertNotNull("ProfessionalServices deve ter Servico e Profissional", p);
			//Assert.assertEquals("FOOBAR", s.getName());

		}


	}


	public User createFakeUser() {
		User u = new User();
		u.setEmail("diego@bol.com");
		u.setPassword("123qwe");
		u.setSourceApp("google+");
		u.setUsername("diegoferques");
		u.setPersonType(User.PersonType.JURIDICA);

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
