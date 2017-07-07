package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Hability;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
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
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfessionalControllerTests {

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

	@Test
	public void testCreateOK() throws IOException {


		String content = new String(Files.readAllBytes(Paths.get("C:\\dev\\_freelas\\Deivison\\projetos\\cosmeticos\\src\\test\\resources\\custumerPostRequest.json")));

		Address addres = createFakeAddress();
		User user = createFakeUser();

		Professional professional = createFakeProfessional();
		professional.setUser(user);
		professional.setAddress(addres);

		ProfessionalRequestBody requestBody = new ProfessionalRequestBody();
		requestBody.setProfessional(professional);

		ProfessionalResponseBody rsp = restTemplate.postForObject("/professionals", content, ProfessionalResponseBody.class);

		final ResponseEntity<ScheduleResponseBody> exchange = //
				restTemplate.exchange( //
						"/professionals", //
						HttpMethod.POST, //
						new HttpEntity(requestBody), // Body
						ScheduleResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
	}

	@Test
	public void testUpdateOK() throws IOException {

		Professional c1 = new Professional();
		c1.setIdProfessional(1L);
		c1.setNameProfessional("Diego Fernandes Marques da Silva");

		ProfessionalRequestBody cr = new ProfessionalRequestBody();
		cr.setProfessional(c1);

		final ResponseEntity<ScheduleResponseBody> exchange = //
				restTemplate.exchange( //
						"/professionals", //
						HttpMethod.PUT, //
						new HttpEntity(cr), // Body
						ScheduleResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
	}

	@Test
	public void testDeleteForbiden() throws ParseException {

		final ResponseEntity<ProfessionalResponseBody> exchange = //
				restTemplate.exchange( //
						"/professionals/1", //
						HttpMethod.DELETE, //
						null,
						ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

	}

	// TODO - Aparentemente o erro é por conta do retorno infinito de ProfessionalCollection, o mesmo erro de testFindById
	@Test
	public void testLastest10OK() throws ParseException {

		final ResponseEntity<ProfessionalResponseBody> exchange = //
				restTemplate.exchange( //
						"/professionals", //
						HttpMethod.GET, //
						null,
						ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

	}

	@Test
	public void testInsertNewHAbilityInCreateProfessionalRequest() throws ParseException, URISyntaxException {

		String jsonRequest = "{\n" +
				"  \"professional\" : {\n" +
				"    \"nameProfessional\" : \"João da Silva\",\n" +
				"    \"cnpj\" : \"098.765.432-10\",\n" +
				"    \"genre\" : \"M\",\n" +
				"    \"birthDate\" : 317185200000,\n" +
				"    \"cellPhone\" : \"(21) 98877-6655\",\n" +
				"    \"dateRegister\" : 1498145793560,\n" +
				"    \"status\" :0,\n" +
				"    \"habilityCollection\" : [ {\n" +
				"      \"name\" : \"Escova Progressiva\"\n" + // Ver HabilityPreLoadConfiguration
				"    }, {\n" +
				"      \"name\" : \"Relaxamento\"\n" + // Ver HabilityPreLoadConfiguration
				"    }, {\n" +
				"      \"name\" : \"Nova Habilidade\"\n" + // Colocamos uma habilidade que HabilityPreLoadConfiguration nao insere.
				"    } ],\n" +
				"    \"user\" : {\n" +
				"      \"username\" : \"profissional1\",\n" +
				"      \"password\" : \"123qwe\",\n" +
				"      \"email\" : \"profissional1@gmail.con\"\n" +
				"    },\n" +
				"    \"address\" : { }\n" +
				"  }\n" +
				"}";

		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(jsonRequest);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

		Optional<Hability> habilityOptional = exchange.getBody().getProfessionalList().get(0).getHabilityCollection()
				.stream()
				.filter(h -> "Nova Habilidade".equals(h.getName()))
				.findFirst();

		if(habilityOptional.isPresent())
		{
			Hability hability = habilityOptional.get();
			Assert.assertNotNull("O ID da nova habilidade deveria vir preenchido.", hability.getId());
		}
		else
		{
			Assert.fail("A resposta deveria trazer a habilidade que foi informada no request.");
		}
	}

	@Test
	public void testBadRequestAtCascadeInsertingNewHabilityDuringProfessionalCreation() throws ParseException, URISyntaxException {

		String jsonRequest = "{\n" +
				"  \"professional\" : {\n" +
				"    \"nameProfessional\" : \"João da Silva\",\n" +
				"    \"cnpj\" : \"098.765.432-10\",\n" +
				"    \"genre\" : \"M\",\n" +
				"    \"birthDate\" : 317185200000,\n" +
				"    \"cellPhone\" : \"(21) 98877-6655\",\n" +
				"    \"dateRegister\" : 1498145793560,\n" +
				"    \"status\" : \"ACTIVE\",\n" +
				"    \"habilityCollection\" : [ {\n" +
				"      \"name\" : \"\"\n" + // Deve dar erro devido a este campo vazio aqui.
				"    }, {\n" +
				"      \"name\" : \"Relaxamento\"\n" +
				"    }, {\n" +
				"      \"name\" : \"Nova Habilidade\"\n" +
				"    } ],\n" +
				"    \"user\" : {\n" +
				"      \"username\" : \"profissional1\",\n" +
				"      \"password\" : \"123qwe\",\n" +
				"      \"email\" : \"profissional1@gmail.con\"\n" +
				"    },\n" +
				"    \"address\" : { }\n" +
				"  }\n" +
				"}";

		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(jsonRequest);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
	}

	@Test
	public void testNewProfessionalWithServicesHeDoes() throws URISyntaxException {
		String jsonBody = "{\n" +
				"\t\"professional\":\n" +
				"\t{\n" +
				"\t\t\"address\":null,\n" +
				"\t\t\"birthDate\":350535600000,\n" +
				"\t\t\"cellPhone\":null,\"dateRegister\":null,\n" +
				"\t\t\"status\":null,\n" +
				"\t\t\"user\":\n" +
				"\t\t{\n" +
				"\t\t\t\"email\":\"E-mail\",\n" +
				"\t\t\t\"idLogin\":null,\n" +
				"\t\t\t\"password\":\"123\",\n" +
				"\t\t\t\"sourceApp\":null,\n" +
				"\t\t\t\"username\":\"E-mail\"\n" +
				"\t\t},\n" +
				"\t\t\"genre\":\"\\u0000\",\n" +
				"\t\t\"cnpj\":\"CNPJ\",\n" +
				"\t\t\"idProfessional\":null,\n" +
				"\t\t\"nameProfessional\":\"Name\",\n" +
				"\t\t\"professionalServicesCollection\":\n" +
				"\t\t[\n" +
				"\t\t\t{\n" +
				"\t\t\t\t\"professional\":null,\n" +
				"\t\t\t\t\"service\":\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"category\":\"HAIR REMOVAL\",\n" +
				"\t\t\t\t\t\"idService\":4\n" +
				"\t\t\t\t}\n" +
				"\t\t\t}\n" +
				"\t\t]\n" +
				"\t}\n" +
				"}";

		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(jsonBody);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
		Assert.assertNotNull(exchange.getBody().getProfessionalList());
		Assert.assertTrue(!exchange.getBody().getProfessionalList().isEmpty());

		Professional p = exchange.getBody().getProfessionalList().get(0);

		// Assegura que foi gerado ID para o profissional
		Assert.assertNotNull(p.getIdProfessional());

		// Assegura que foi gerado ID para o User do profissional
		Assert.assertNotNull(p.getUser().getIdLogin());
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

	public static User createFakeUser() {
		User u = new User();
		u.setEmail("diego@bol.com");
		u.setPassword("123qwe");
		u.setSourceApp("google+");
		u.setUsername("diegoferques");

		return u;
	}

	public static Address createFakeAddress() {
		Address a = new Address();
		a.setAddress("Rua Perlita");
		a.setCep("0000000");
		a.setCity("RJO");
		a.setCountry("BRA");
		a.setNeighborhood("Austin");
		a.setState("RJ");

		return a;
	}

	public static Professional createFakeProfessional() {
		Professional c1 = new Professional();
		c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
		c1.setCellPhone("(21) 98877-6655");
		c1.setCnpj("098.765.432-10");
		c1.setDateRegister(Calendar.getInstance().getTime());
		c1.setGenre('M');
		c1.setNameProfessional("João da Silva");
		//c1.setServiceRequestCollection(null);
		c1.setStatus(Professional.Status.ACTIVE);
		c1.setAddress(createFakeAddress());
		c1.setUser(createFakeUser());

		return c1;
	}
}
