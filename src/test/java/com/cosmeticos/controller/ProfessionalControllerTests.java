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
import com.cosmeticos.service.ProfessionalService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
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

<<<<<<< HEAD
		//TODO: pesquisar como gravar apenas dia mes e ano.
		//c1.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1980, 01, 20, 0, 0, 0)));
		//Date birthDate1 = new SimpleDateFormat("yyyy-MM-dd").parse("1980-01-20");

		Professional c1 = new Professional();
		c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
		c1.setCellPhone("(21) 98877-6655");
		c1.setCnpj("098.765.432-10");
		c1.setDateRegister(Calendar.getInstance().getTime());
		c1.setGenre('M');
		c1.setNameProfessional("João da Silva");
		//c1.setOrderCollection(null);
		c1.setStatus(Professional.Status.ACTIVE);
		c1.setIdAddress(this.createFakeAddress(c1));
		c1.setIdLogin(this.createFakeLogin(c1));

		Date birthDate2 = new SimpleDateFormat("yyyy-MM-dd").parse("1981-01-20");
		Professional c2 = new Professional();
		c2.setBirthDate(birthDate2);
		c2.setCellPhone("(21) 98807-2756");
		c2.setCnpj("098.330.987-62");
		c2.setDateRegister(Calendar.getInstance().getTime());
		c2.setGenre('M');
		c2.setNameProfessional("Diego Fernandes");
		//c2.setOrderCollection(null);
		c2.setStatus(Professional.Status.ACTIVE);
		c2.setIdAddress(this.createFakeAddress(c2));
		c2.setIdLogin(this.createFakeLogin(c2));

		Date birthDate3 = new SimpleDateFormat("yyyy-MM-dd").parse("1982-01-20");
		Professional c3 = new Professional();
		c3.setBirthDate(birthDate3);
		c3.setCellPhone("(21) 99988-7766");
		c3.setCnpj("831.846.135-15");
		c3.setDateRegister(Calendar.getInstance().getTime());
		c3.setGenre('F');
		c3.setNameProfessional("Maria das Dores");
		//c3.setOrderCollection(null);
		c3.setStatus(Professional.Status.ACTIVE);
		c3.setIdAddress(this.createFakeAddress(c3));
		c3.setIdLogin(this.createFakeLogin(c3));

		Date birthDate4 = new SimpleDateFormat("yyyy-MM-dd").parse("1983-01-20");
		Professional c4 = new Professional();
		c4.setBirthDate(birthDate4);
		c4.setCellPhone("(21) 99887-7665");
		c4.setCnpj("816.810.695-68");
		c4.setDateRegister(Calendar.getInstance().getTime());
		c4.setGenre('F');
		c4.setNameProfessional("Fernanda Cavalcante");
		//c4.setOrderCollection(null);
		c4.setStatus(Professional.Status.INACTIVE);
		c4.setIdAddress(this.createFakeAddress(c4));
		c4.setIdLogin(this.createFakeLogin(c4));

		Date birthDate5 = new SimpleDateFormat("yyyy-MM-dd").parse("1984-01-20");
		Professional c5 = new Professional();
		c5.setBirthDate(birthDate5);
		c5.setCellPhone("(21) 97766-5544");
		c5.setCnpj("541.913.254-81");
		c5.setDateRegister(Calendar.getInstance().getTime());
		c5.setGenre('M');
		c5.setNameProfessional("José das Couves");
		//c5.setOrderCollection(null);
		c5.setStatus(Professional.Status.ACTIVE);
		c5.setIdAddress(this.createFakeAddress(c5));
		c5.setIdLogin(this.createFakeLogin(c5));

		customerRepository.save(c1);
		customerRepository.save(c2);
		customerRepository.save(c3);
		customerRepository.save(c4);
		customerRepository.save(c5);
=======
>>>>>>> dev
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
				"    \"status\" : \"ACTIVE\",\n" +
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
