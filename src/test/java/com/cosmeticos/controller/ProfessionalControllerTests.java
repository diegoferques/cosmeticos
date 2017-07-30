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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
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

	private Professional returnOfCreateOK = null;

	private Professional returnOfCreateOKWithAddress = null;

	private String emailUsuario = null;

	/**
	 * Inicializa o H2 com dados iniciais.
	 */
	@Before
	public void setupTests() throws ParseException {

	}

	@Test
	public void testCreateOK() throws IOException, URISyntaxException {
		String email = "a@a.com";
		if(!StringUtils.isEmpty(emailUsuario)) {
			email = emailUsuario;
		}

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"address\": null,\n" +
				"    \"birthDate\": 1120705200000,\n" +
				"    \"cellPhone\": null,\n" +
				"    \"dateRegister\": null,\n" +
				"    \"genre\": null,\n" +
				"    \"status\": null,\n" +
				"    \"user\": {\n" +
				"      \"email\": \""+ email +"\",\n" +
				"      \"idLogin\": null,\n" +
				"      \"password\": \"123\",\n" +
				"      \"sourceApp\": null,\n" +
				"      \"username\": \""+ email +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalServicesCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"service\": {\n" +
				"          \"category\": \"HYDRATION\",\n" +
				"          \"idService\": 2\n" +
				"        }\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";

		
		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);
				
		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

		returnOfCreateOK = exchange.getBody().getProfessionalList().get(0);
	}

	@Test
	public void testCreateOKWithAddress() throws IOException, URISyntaxException {

		if(StringUtils.isEmpty(emailUsuario)) {
			emailUsuario = "b@a.com";
		}

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"address\": { \n" +
				"	    \"address\": \"Avenida dos Metalúrgicos, 22\",\n" +
				"	    \"cep\": \"26083-275\",\n" +
				"	    \"neighborhood\": \"Rodilândia\",\n" +
				"	    \"city\": \"Nova Iguaçu\",\n" +
				"	    \"state\": \"RJ\",\n" +
				"	    \"country\": \"BR\" \n" +
				"    },\n" +
				"    \"birthDate\": 1120705200000,\n" +
				"    \"cellPhone\": null,\n" +
				"    \"dateRegister\": null,\n" +
				"    \"genre\": null,\n" +
				"    \"status\": null,\n" +
				"    \"user\": {\n" +
				"      \"email\": \""+ emailUsuario +"\",\n" +
				"      \"idLogin\": null,\n" +
				"      \"password\": \"123\",\n" +
				"      \"sourceApp\": null,\n" +
				"      \"username\": \""+ emailUsuario +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalServicesCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"service\": {\n" +
				"          \"category\": \"HYDRATION\",\n" +
				"          \"idService\": 2\n" +
				"        }\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";

		System.out.println(json);


		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

		returnOfCreateOKWithAddress = exchange.getBody().getProfessionalList().get(0);
	}

	@Test
	public void testUpdateAddressFromCreateOKWithAddress() throws IOException, URISyntaxException {
		emailUsuario = "c@c.com";

		testCreateOKWithAddress();

		Professional professional = returnOfCreateOKWithAddress;
		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": "+ professional.getIdProfessional() +",\n" +
				"    \"address\": { \n" +
				"	    \"address\": \"Rua José Paulino, 152\",\n" +
				"	    \"cep\": \"26083-485\",\n" +
				"	    \"neighborhood\": \"Rodilândia\",\n" +
				"	    \"city\": \"Nova Iguaçu\",\n" +
				"	    \"state\": \"RJ\",\n" +
				"	    \"country\": \"BR\" \n" +
				"    }\n" +
				"  }\n" +
				"}";

		System.out.println(json);

		RequestEntity<String> entity =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
		Assert.assertEquals("26083-485", exchange.getBody().getProfessionalList().get(0).getAddress().getCep());

	}

	@Test
	public void testBadRequestOnUpdateAddressFromCreateOKWithAddress() throws IOException, URISyntaxException {
		emailUsuario = "d@d.com";
		testCreateOKWithAddress();

		Professional professional = returnOfCreateOKWithAddress;
		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": "+ professional.getIdProfessional() +",\n" +
				"    \"address\": { \n" +
				"	    \"address\": \"Rua José Paulino, 152\",\n" +
				"	    \"cep\": \"26083-485\",\n" +
				"	    \"neighborhood\": \"Rodilândia\",\n" +
				"	    \"city\": \"Nova Iguaçu\",\n" +
				"	    \"state\": \"RJ\",,\n" + //DEVE DAR ERRO POR TER UMA VIRGULA A MAIS
				"	    \"country\": \"BR\" \n" +
				"    }\n" +
				"  }\n" +
				"}";

		System.out.println(json);

		RequestEntity<String> entity =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());

	}

	@Test
	public void testNotFoundOnUpdate() throws IOException, URISyntaxException {

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": 45362,\n" + //DEVE DAR ERRO POR TER INFORMADO UM ID INEXISTENTE
				"    \"address\": { \n" +
				"	    \"address\": \"Rua José Paulino, 152\",\n" +
				"	    \"cep\": \"26083-485\",\n" +
				"	    \"neighborhood\": \"Rodilândia\",\n" +
				"	    \"city\": \"Nova Iguaçu\",\n" +
				"	    \"state\": \"RJ\",\n" +
				"	    \"country\": \"BR\" \n" +
				"    }\n" +
				"  }\n" +
				"}";

		System.out.println(json);

		RequestEntity<String> entity =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.NOT_FOUND, exchange.getStatusCode());

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

	@Test
	public void tesFindById() throws ParseException, IOException, URISyntaxException {
		emailUsuario = "e@e.com";
		testCreateOKWithAddress();

		Professional professional = returnOfCreateOKWithAddress;

		final ResponseEntity<ProfessionalResponseBody> exchange = //
				restTemplate.exchange( //
						"/professionals/" + professional.getIdProfessional(), //
						HttpMethod.GET, //
						null,
						ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
		Assert.assertEquals("aaa", exchange.getBody().getProfessionalList().get(0).getNameProfessional());
		Assert.assertEquals("e@e.com", exchange.getBody().getProfessionalList().get(0).getUser().getEmail());

	}

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

	/**
	 * Este teste na verdade testa duas coisas: o parametro ModelAttribute no metodo e o Example Api.
	 * @throws ParseException
	 */
	@Test
	public void testExampleApiFindByNameProfessional() throws ParseException {

		Address addres = createFakeAddress();
		User user = UserControllerTest.createFakeUser("111", "111@gmail");

		Professional professional = createFakeProfessional();
		professional.setUser(user);
		professional.setAddress(addres);
		professional.setNameProfessional("MyName");

		ProfessionalRequestBody requestBody = new ProfessionalRequestBody();
		requestBody.setProfessional(professional);

		final ResponseEntity<ProfessionalResponseBody> postExchange = //
				restTemplate.exchange( //
						"/professionals", //
						HttpMethod.POST, //
						new HttpEntity(requestBody), // Body
						ProfessionalResponseBody.class);


		final ResponseEntity<ProfessionalResponseBody> getExchange = //
				restTemplate.exchange( //
						"/professionals?nameProfessional=MyName", //
						HttpMethod.GET, //
						null,
						ProfessionalResponseBody.class);

		Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());

		ProfessionalResponseBody response = getExchange.getBody();
		List<Professional> professionals = response.getProfessionalList();

		Assert.assertTrue("Nao foram retornados profissionais.", professionals.size() > 0);

		for (int i = 0; i < professionals.size(); i++) {
			Professional p =  professionals.get(i);
			Assert.assertEquals("MyName", p.getNameProfessional());
		}


	}

	/**
	 * Este teste na verdade testa duas coisas: o parametro ModelAttribute no metodo e o Example Api.
	 * @throws ParseException
	 */
	@Test
	public void testExampleApiFindByUser_EmailAndPassword() throws ParseException {

		Address addres = createFakeAddress();
		User user = UserControllerTest.createFakeUser("111", "111@gmail");
		user.setPassword("123");

		Professional professional = createFakeProfessional();
		professional.setUser(user);
		professional.setAddress(addres);
		professional.setNameProfessional("MyName");

		ProfessionalRequestBody requestBody = new ProfessionalRequestBody();
		requestBody.setProfessional(professional);

		final ResponseEntity<ProfessionalResponseBody> postExchange = //
				restTemplate.exchange( //
						"/professionals", //
						HttpMethod.POST, //
						new HttpEntity(requestBody), // Body
						ProfessionalResponseBody.class);


		final ResponseEntity<ProfessionalResponseBody> getExchange = //
				restTemplate.exchange( //
						"/professionals?nameProfessional=MyName", //
						HttpMethod.GET, //
						null,
						ProfessionalResponseBody.class);

		Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());

		ProfessionalResponseBody response = getExchange.getBody();
		List<Professional> professionals = response.getProfessionalList();

		Assert.assertTrue("Nao foram retornados profissionais.", professionals.size() > 0);

		for (int i = 0; i < professionals.size(); i++) {
			Professional p =  professionals.get(i);
			Assert.assertEquals("MyName", p.getNameProfessional());
		}


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

	@Test
	public void testBadRequestWhenNewProfessionalOmmitsIdService() throws URISyntaxException {
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
				// id omitido. Se um request desse chega, ha risco de insercao em cascada, o q nao pode acontecer
				// pq apenas o admin insere service
				"\t\t\t\t\t\"category\":\"HAIR REMOVAL\""+
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
		Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
	}

	@Test
	public void testCreateUserEmailBadRequest() throws IOException, URISyntaxException {
		emailUsuario = "onemoretest@email.com";

		testCreateOK();

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"address\": null,\n" +
				"    \"birthDate\": 1120705200000,\n" +
				"    \"cellPhone\": null,\n" +
				"    \"dateRegister\": null,\n" +
				"    \"genre\": null,\n" +
				"    \"status\": null,\n" +
				"    \"user\": {\n" +
				"      \"email\": \""+ returnOfCreateOK.getUser().getEmail() +"\",\n" +
				"      \"idLogin\": null,\n" +
				"      \"password\": \"123\",\n" +
				"      \"sourceApp\": null,\n" +
				"      \"username\": \""+ returnOfCreateOK.getUser().getEmail() +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"0984068791\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"Repeated Email\",\n" +
				"    \"professionalServicesCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"service\": {\n" +
				"          \"category\": \"HYDRATION\",\n" +
				"          \"idService\": 2\n" +
				"        }\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";


		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
		Assert.assertEquals("E-mail já existente.", exchange.getBody().getDescription());
	}

	@Test
	public void testUpdateUserEmailBadRequest() throws IOException, URISyntaxException {
		emailUsuario = "onemorecheck@email.com";

		testCreateOK();

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": "+ returnOfCreateOK.getIdProfessional() +",\n" +
				"    \"user\": {\n" +
				"      \"idLogin\": "+ returnOfCreateOK.getUser().getIdLogin() +",\n" +
				"      \"email\": \""+ returnOfCreateOK.getUser().getEmail() +"\"\n" +
				"    },\n" +
				"    \"nameProfessional\": \"Another Repeated Email\"\n" +
				"  }\n" +
				"}";

		System.out.println(json);


		RequestEntity<String> entity =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
		Assert.assertEquals("E-mail já existente.", exchange.getBody().getDescription());
	}


	static Address createFakeAddress() {
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
		//c1.setOrderCollection(null);
		c1.setStatus(Professional.Status.ACTIVE);
		c1.setAddress(createFakeAddress());
		c1.setUser(UserControllerTest.createFakeUser("222", "222@2.com"));
		c1.getUser().setProfessional(c1);

		return c1;
	}

	@Test
	public void testCreateAttendanceOK() throws IOException, URISyntaxException {
		String email = "a@a.com";
		if(!StringUtils.isEmpty(emailUsuario)) {
			email = emailUsuario;
		}

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"address\": null,\n" +
				"    \"birthDate\": 1120705200000,\n" +
				"    \"cellPhone\": null,\n" +
				"    \"dateRegister\": null,\n" +
				"    \"genre\": null,\n" +
				"    \"status\": null,\n" +
				"    \"attendance\": \"HOME_CARE\",\n" +
				"    \"user\": {\n" +
				"      \"email\": \""+ email +"\",\n" +
				"      \"idLogin\": null,\n" +
				"      \"password\": \"123\",\n" +
				"      \"sourceApp\": null,\n" +
				"      \"username\": \""+ email +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalServicesCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"service\": {\n" +
				"          \"category\": \"HYDRATION\",\n" +
				"          \"idService\": 2\n" +
				"        }\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";

		System.out.println(json);

		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

		returnOfCreateOK = exchange.getBody().getProfessionalList().get(0);
	}

	@Test
	public void testUpdateAttendanceOK() throws IOException, URISyntaxException {

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"address\": null,\n" +
				"    \"birthDate\": 1120705200000,\n" +
				"    \"cellPhone\": null,\n" +
				"    \"dateRegister\": null,\n" +
				"    \"genre\": null,\n" +
				"    \"status\": null,\n" +
				"    \"attendance\": \"HOME_CARE\",\n" +
				"    \"user\": {\n" +
				"      \"email\": null,\n" +
				//"      \"idLogin\": 1,\n" +
				"      \"password\": \"123\",\n" +
				"      \"sourceApp\": null,\n" +
				"      \"username\": \"aaa\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				//"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaaa\"\n" +
				"  }\n" +
				"}";

		System.out.println(json);

		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

		Professional professional = exchange.getBody().getProfessionalList().get(0);

		String jsonUpdate = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
				"    \"attendance\": \"FULL\"\n" +
				"  }\n" +
				"}";

		System.out.println(jsonUpdate);


		RequestEntity<String> entityUpdate =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(jsonUpdate);

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate = restTemplate
				.exchange(entityUpdate, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchangeUpdate);
		Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
		Assert.assertEquals(Professional.Type.FULL, exchangeUpdate.getBody().getProfessionalList().get(0).getAttendance());
	}

	@Test
	public void testGetAttendanceOK() throws IOException, URISyntaxException {

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"address\": null,\n" +
				"    \"birthDate\": 1120705200000,\n" +
				"    \"cellPhone\": null,\n" +
				"    \"dateRegister\": null,\n" +
				"    \"genre\": null,\n" +
				"    \"status\": null,\n" +
				"    \"attendance\": \"ON_SITE\",\n" +
				"    \"user\": {\n" +
				"      \"email\": null,\n" +
				//"      \"idLogin\": 1,\n" +
				"      \"password\": \"123\",\n" +
				"      \"sourceApp\": null,\n" +
				"      \"username\": \"aaa\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				//"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaaa\"\n" +
				"  }\n" +
				"}";

		System.out.println(json);

		RequestEntity<String> entity = RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange = restTemplate
				.exchange(entity, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

		Professional professional = exchange.getBody().getProfessionalList().get(0);

		final ResponseEntity<ProfessionalResponseBody> getExchangeGet = //
				restTemplate.exchange( //
						"/professionals", //
						HttpMethod.GET, //
						null,
						ProfessionalResponseBody.class);

		Assert.assertEquals(HttpStatus.OK, getExchangeGet.getStatusCode());
	}

}
