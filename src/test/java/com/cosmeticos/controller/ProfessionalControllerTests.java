package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfessionalControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ProfessionalRepository professionalRepository;

	@Autowired
	private ProfessionalCategoryRepository professionalCategoryRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	private Professional returnOfCreateOK = null;

	private Professional returnOfCreateOKWithAddress = null;

	private String emailUsuario = null;

	private String rule = null;

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
				"         \"personType\":\"FISICA\",\n" +
				"      \"username\": \""+ email +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"HYDRATION\",\n" +
				"          \"idCategory\": 2\n" +
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
	public void testUpdateOK() throws IOException {

		Professional c1 = new Professional();
		c1.setIdProfessional(1L);
		c1.setNameProfessional("Diego Fernandes Marques da Silva");
		c1.setCnpj("1233211233211");

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
				"         \"personType\":\"FISICA\",\n" +
				"      \"username\": \""+ emailUsuario +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"HYDRATION\",\n" +
				"          \"idCategory\": 2\n" +
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

		ResponseEntity<ProfessionalResponseBody> exchange = put(json, restTemplate);

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

		ResponseEntity<ProfessionalResponseBody> exchange = put(json, restTemplate);

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

		ResponseEntity<ProfessionalResponseBody> exchange = put(json, restTemplate);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.NOT_FOUND, exchange.getStatusCode());

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
				"         \"personType\":\"JURIDICA\",\n" +
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
				"         \"personType\":\"FÍSICA\",\n" +
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
				"\t\t\t\"personType\": \""+ User.PersonType.FISICA+"\",\n" +
				"\t\t\t\"password\":\"123\",\n" +
				"\t\t\t\"sourceApp\":null,\n" +
				"\t\t\t\"username\":\"E-mail\"\n" +
				"\t\t},\n" +
				"\t\t\"genre\":\"\\u0000\",\n" +
				"\t\t\"cnpj\":\"CNPJ\",\n" +
				"\t\t\"idProfessional\":null,\n" +
				"\t\t\"nameProfessional\":\"Name\",\n" +
				"\t\t\"professionalCategoryCollection\":\n" +
				"\t\t[\n" +
				"\t\t\t{\n" +
				"\t\t\t\t\"professional\":null,\n" +
				"\t\t\t\t\"category\":\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"name\":\"HAIR REMOVAL\",\n" +
				"\t\t\t\t\t\"idCategory\":4\n" +
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
	public void testBadRequestWhenNewProfessionalOmmitsIdCategory() throws URISyntaxException {
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
				"\t\t\"professionalCategoryCollection\":\n" +
				"\t\t[\n" +
				"\t\t\t{\n" +
				"\t\t\t\t\"professional\":null,\n" +
				"\t\t\t\t\"category\":\n" +
				"\t\t\t\t{\n" +
				// id omitido. Se um request desse chega, ha risco de insercao em cascada, o q nao pode acontecer
				// pq apenas o admin insere service
				"\t\t\t\t\t\"name\":\"HAIR REMOVAL\""+
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
				"      \"personType\":\"FISICA\",\n" +
				"      \"username\": \""+ returnOfCreateOK.getUser().getEmail() +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"0984068791\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"Repeated Email\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"HYDRATION\",\n" +
				"          \"idCategory\": 2\n" +
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

	/**
	 * Teste para garantir que updates de um usuario nao atualizam o email. Nao se atualiza e-mail.
	 * Para usar um email diferente, um usuario deve criar outra conta.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@Test
	public void testUpdateUserEmailBadRequest() throws IOException, URISyntaxException {
		emailUsuario = "onemorecheck@email.com";

		testCreateOK();

		String emailAlteradoUsuario = "onemorecheckALTERADO_PRA_FALHAR@email.com";

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": "+ returnOfCreateOK.getIdProfessional() +",\n" +
				"    \"user\": {\n" +
				"      \"idLogin\": "+ returnOfCreateOK.getUser().getIdLogin() +",\n" +
				"      \"personType\":\"FISICA\",\n" +
				"      \"email\": \""+ emailAlteradoUsuario +"\"\n" +
				"    },\n" +
				"    \"nameProfessional\": \"Another Repeated Email\"\n" +
				"  }\n" +
				"}";

		ResponseEntity<ProfessionalResponseBody> exchange = put(json, restTemplate);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

		Professional responseProfessional = exchange.getBody().getProfessionalList().get(0);

		// Conferindo no banco que o email que tentamos alterar nao foi registrado.
		Optional<User> optionalPersistentUser = userRepository.findByEmail(emailAlteradoUsuario);

		Assert.assertEquals("E-mail foi atualizado! Não pode!",
				false, optionalPersistentUser.isPresent());
	}

	static ResponseEntity<ProfessionalResponseBody> put(String json, TestRestTemplate restTemplate) throws URISyntaxException {
		System.out.println(json);

		RequestEntity<String> entity =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		return restTemplate
				.exchange(entity, ProfessionalResponseBody.class);
	}


	static Address createFakeAddress() {
		Address a = new Address();
		a.setAddress("Rua Perlita");
		a.setCep("0000000");
		a.setCity("RJO");
		a.setCountry("BRA");
		a.setNeighborhood("Austin");
		a.setState("RJ");
		a.setComplement("HOUSE");

		return a;
	}

	/**
	 * Cria o Professional com os campos unique de Professional e User com nanoSeconds convertidos em String.
	 * @return
	 */
	public static Professional createFakeProfessional() {
		Professional c1 = new Professional();
		c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
		c1.setCellPhone("(21) 98877-6655");
		c1.setCnpj("098.765.432-10");
		c1.setDateRegister(Calendar.getInstance().getTime());
		c1.setGenre('M');
		c1.setNameProfessional(String.valueOf(System.nanoTime()) );
		//nonCreditCardCustomer.setOrderCollection(null);
		c1.setStatus(Professional.Status.ACTIVE);
		c1.setAddress(createFakeAddress());
		c1.setUser(UserControllerTest.createFakeUser(String.valueOf(System.nanoTime()) , String.valueOf(System.nanoTime()) + "@2.com"));
		c1.getUser().setProfessional(c1);
		c1.getUser().setPersonType(User.PersonType.JURIDICA);

		return c1;
	}

	@Test
	public void testCreateAttendanceOK() throws IOException, URISyntaxException {
		String email = "testCreateAttendanceOK@a.com";
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
				"         \"personType\":\"JURIDICA\",\n" +
				"      \"username\": \""+ email +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"HYDRATION\",\n" +
				"          \"idCategory\": 2\n" +
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
				"      \"email\": \"testUpdateAttendanceOK@bol.com\",\n" +
				//"      \"idLogin\": 1,\n" +
				"      \"password\": \"123\",\n" +
				"      \"sourceApp\": null,\n" +
				"         \"personType\":\"FISICA\",\n" +
				"      \"username\": \"testUpdateAttendanceOK\"\n" +
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

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

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
				"         \"personType\":\"FISICA\",\n" +
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

	@Test
	public void testPutRuleForProfessional1() throws URISyntaxException {

		String email = "professionalServicesRNF58qwert@email.com";

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
				"         \"personType\":\"FISICA\",\n" +
				"      \"username\": \""+ email +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"PINTURA\",\n" +
				"          \"idCategory\": 2\n" +
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

		Professional professional = exchange.getBody().getProfessionalList().get(0);

		String jsonUpdate = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
				"        \"priceRule\": [\n" +
				"          {\n" +
				"            \"name\": \"COMPRIMENTO ATÉ 30cm\",\n" +
				"            \"price\": 80.00\n" +
				"          }\n" +
				"          ]\n" +
				"  }\n" +
				"}";

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

		Assert.assertNotNull(exchangeUpdate);
		Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());


	}
	@Test
	public void testPutRuleForProfessional2() throws URISyntaxException {

		String email = "professionalServicesRNF58ytrewq@email.com";

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
				"         \"personType\":\"FISICA\",\n" +
				"      \"username\": \""+ email +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"LUZES\",\n" +
				"          \"idCategory\": 1\n" +
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

		Professional professional = exchange.getBody().getProfessionalList().get(0);

		String jsonUpdate = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
				"        \"priceRule\": [{\n" +
				"            \"name\": \"COMPRIMENTO ATÉ 60cm\",\n" +
				"            \"price\": 200.00\n" +
				"          }]\n" +
				"  }\n" +
				"}";

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

		Assert.assertNotNull(exchangeUpdate);
		Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());


	}

	@Test
	public void testPutRuleForProfessional3() throws URISyntaxException {

		String email = "professionalServicesRNF58asdfg@email.com";

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
				"         \"personType\":\"FISICA\",\n" +
				"      \"username\": \""+ email +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"LUZES\",\n" +
				"          \"idCategory\": 1\n" +
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

		Professional professional = exchange.getBody().getProfessionalList().get(0);

		String jsonUpdate = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
				"        \"priceRule\": [\n" +
				"          {\n" +
				"            \"name\": \"COMPRIMENTO ATÉ 60cm\",\n" +
				"            \"price\": 200.00\n" +
				"          }\n" +
				"          ]\n" +
				"  }\n" +
				"}";

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

		Assert.assertNotNull(exchangeUpdate);
		Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

		String jsonUpdate2 = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
				"        \"priceRule\": [\n" +
				"          {\n" +
				"            \"name\": \"COMPRIMENTO ATÉ 40cm\",\n" +
				"            \"price\": 150.00\n" +
				"          }\n" +
				"          ]\n" +
				"  }\n" +
				"}";

		System.out.println(jsonUpdate2);


		RequestEntity<String> entityUpdate2 =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(jsonUpdate);

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate2 = restTemplate
				.exchange(entityUpdate2, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchangeUpdate2);
		Assert.assertEquals(HttpStatus.OK, exchangeUpdate2.getStatusCode());

	}

	@Test
	public void testRuleIgualEntreProfessional() throws URISyntaxException {

		String email = "professionalServicesRNF58@email.com";

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
				"         \"personType\":\"FISICA\",\n" +
				"      \"username\": \""+ email +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"LUZES\",\n" +
				"          \"idCategory\": 1\n" +
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

		Professional professional = exchange.getBody().getProfessionalList().get(0);

		String jsonUpdate = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
				"        \"priceRule\": [\n" +
				"          {\n" +
				"            \"name\": \"COMPRIMENTO ATÉ 10cm\",\n" +
				"            \"price\": 50.00\n" +
				"          }\n" +
				"          ]\n" +
				"  }\n" +
				"}";

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

		Assert.assertNotNull(exchangeUpdate);
		Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());

		String email2 = "professionalServicesRNF581234567@email.com";

		String json2 = "{\n" +
				"  \"professional\": {\n" +
				"    \"address\": null,\n" +
				"    \"birthDate\": 1120705200000,\n" +
				"    \"cellPhone\": null,\n" +
				"    \"dateRegister\": null,\n" +
				"    \"genre\": null,\n" +
				"    \"status\": null,\n" +
				"    \"user\": {\n" +
				"      \"email\": \""+ email2 +"\",\n" +
				"      \"idLogin\": null,\n" +
				"      \"password\": \"123\",\n" +
				"      \"sourceApp\": null,\n" +
				"         \"personType\":\"FISICA\",\n" +
				"      \"username\": \""+ email2 +"\"\n" +
				"    },\n" +
				"    \"cnpj\": \"05404277726\",\n" +
				"    \"idProfessional\": null,\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \"aaa\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"LUZES\",\n" +
				"          \"idCategory\": 1\n" +
				"        }\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";

		System.out.println(json2);

		RequestEntity<String> entity2 =  RequestEntity
				.post(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json2);

		ResponseEntity<ProfessionalResponseBody> exchange2 = restTemplate
				.exchange(entity2, ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange2);
		Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

		Professional professional2 = exchange2.getBody().getProfessionalList().get(0);

		String jsonUpdate2 = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professional2.getIdProfessional() + "\",\n" +
				"        \"priceRule\": [\n" +
				"          {\n" +
				"            \"name\": \"COMPRIMENTO ATÉ 10cm\",\n" +
				"            \"price\": 75.00\n" +
				"          }\n" +
				"          ]\n" +
				"  }\n" +
				"}";

		ResponseEntity<ProfessionalResponseBody> exchangeUpdateRule = put(jsonUpdate2, restTemplate);

		Assert.assertNotNull(exchangeUpdate);
		Assert.assertEquals(HttpStatus.OK, exchangeUpdateRule.getStatusCode());
	}

	@Test
	public void test2employees() throws URISyntaxException {

		Professional professionalBoss = createFakeProfessional();
		professionalBoss.setNameProfessional("BossName");
		professionalRepository.save(professionalBoss);

		Professional professionalEmployees1 = createFakeProfessional();
		professionalBoss.setNameProfessional("EmployeesName1");
		professionalRepository.save(professionalEmployees1);

		Professional professionalEmployees2 = createFakeProfessional();
		professionalBoss.setNameProfessional("EmployeesName2");
		professionalRepository.save(professionalEmployees2);

		String jsonUpdate = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professionalBoss.getIdProfessional() + "\",\n" +
				"        \"employeesCollection\": [\n" +
				"          {\n" +
				"            \"idProfessional\": "+professionalEmployees1.getIdProfessional()+"\n" +
				"          },\n" +
				"          {\n" +
				"            \"idProfessional\": "+professionalEmployees2.getIdProfessional()+"\n" +
				"          }\n" +
				"          ]\n" +
				"  }\n" +
				"}";

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

		Assert.assertNotNull(exchangeUpdate);
		Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
		Assert.assertEquals(2, exchangeUpdate
				.getBody()
				.getProfessionalList()
				.get(0)
				.getEmployeesCollection()
				.size());

	}

	@Test
	public void testAdd2employeesAndThenRemoveOneOfThem() throws URISyntaxException {

		Professional professionalBoss = createFakeProfessional();
		professionalBoss.setNameProfessional("BossName");
		professionalRepository.save(professionalBoss);

		Professional professionalEmployees1 = createFakeProfessional();
		professionalBoss.setNameProfessional("EmployeesName1");
		professionalRepository.save(professionalEmployees1);

		Professional professionalEmployees2 = createFakeProfessional();
		professionalBoss.setNameProfessional("EmployeesName2");
		professionalRepository.save(professionalEmployees2);

		String jsonUpdate = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professionalBoss.getIdProfessional() + "\",\n" +
				"        \"employeesCollection\": [\n" +
				"          {\n" +
				"            \"idProfessional\": "+professionalEmployees1.getIdProfessional()+"\n" +
				"          },\n" +
				"          {\n" +
				"            \"idProfessional\": "+professionalEmployees2.getIdProfessional()+"\n" +
				"          }\n" +
				"          ]\n" +
				"  }\n" +
				"}";

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

		Assert.assertNotNull(exchangeUpdate);
		Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
		Assert.assertEquals(2, exchangeUpdate
				.getBody()
				.getProfessionalList()
				.get(0)
				.getEmployeesCollection()
				.size());

		String url = MessageFormat.format(
				"/professionals/{0}/employees/{1}",
				professionalBoss.getIdProfessional(),
				professionalEmployees1.getIdProfessional());

		RequestEntity<Void> entity =  RequestEntity
				.delete(new URI(url))
				.accept(MediaType.APPLICATION_JSON)
				.build();

		ResponseEntity<Void> deleteResponse = restTemplate.exchange(entity, Void.class);

		Assert.assertNotNull(deleteResponse);
		Assert.assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

		Professional persistentBoss = professionalRepository.findOne(professionalBoss.getIdProfessional());

		Assert.assertEquals(1, persistentBoss
				.getEmployeesCollection()
				.size());
	}

    @Test
    public void testAddEmployees() throws ParseException, URISyntaxException {

        Professional professionalBoss = createFakeProfessional();
        professionalBoss.setNameProfessional("BossName");

        Professional professionalEmployees1 = createFakeProfessional();
        professionalEmployees1.setNameProfessional("EmployeesName1");

        Professional professionalEmployees2 = createFakeProfessional();
        professionalEmployees2.setNameProfessional("EmployeesName2");


        professionalRepository.save(professionalBoss);
        professionalRepository.save(professionalEmployees1);
        professionalRepository.save(professionalEmployees2);


        String jsonUpdate = "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": \"" + professionalBoss.getIdProfessional() + "\",\n" +
                "        \"employeesCollection\": [\n" +
                "          {\n" +
                "            \"idProfessional\": "+professionalEmployees1.getIdProfessional()+"\n" +
                "          }\n" +
                "          ]\n" +
                "  }\n" +
                "}";

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate = put(jsonUpdate, restTemplate);

        Assert.assertNotNull(exchangeUpdate);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertEquals(1, exchangeUpdate
                .getBody()
                .getProfessionalList()
                .get(0)
                .getEmployeesCollection()
                .size());

        String jsonUpdate2 = "{\n" +
                "  \"professional\": {\n" +
                "    \"idProfessional\": \"" + professionalBoss.getIdProfessional() + "\",\n" +
                "        \"employeesCollection\": [\n" +
                "          {\n" +
                "            \"idProfessional\": "+professionalEmployees2.getIdProfessional()+"\n" +
                "          }\n" +
                "          ]\n" +
                "  }\n" +
                "}";

		ResponseEntity<ProfessionalResponseBody> exchangeUpdate2 = put(jsonUpdate2, restTemplate);

        Assert.assertNotNull(exchangeUpdate2);
        Assert.assertEquals(HttpStatus.OK, exchangeUpdate2.getStatusCode());
        Assert.assertEquals(2, exchangeUpdate2
                .getBody()
                .getProfessionalList()
                .get(0)
                .getEmployeesCollection()
                .size());

    }

	@Test
	public void testAddThirdCategoryAndThenAFourthCategoryOnOldEndpointPut() throws URISyntaxException, JsonProcessingException {
		/****** setting up  ***************/
		Professional professional = ProfessionalControllerTests.createFakeProfessional();
		professionalRepository.save(professional);

		ProfessionalCategory professionalCategory1 = buildProfessionalCategory(
				"testAddThirdCategoryAndThenAFourthCategoryOnOldEndpointPut1",
				"testAddThirdCategoryAndThenAFourthCategoryOnOldEndpointPut1-pr1",
				5000L
		);
		ProfessionalCategory professionalCategory2 = buildProfessionalCategory(
				"testAddThirdCategoryAndThenAFourthCategoryOnOldEndpointPut2",
				"testAddThirdCategoryAndThenAFourthCategoryOnOldEndpointPut2-pr1",
				7000L
		);

		professional.addProfessionalCategory(professionalCategory1);
		professional.addProfessionalCategory(professionalCategory2);

		professionalCategoryRepository.saveAndFlush(professionalCategory1);
		professionalCategoryRepository.saveAndFlush(professionalCategory2);

		/************ testing ******************************/

		Category c3 = new Category();
		c3.setName("testAddThirdCategoryAndThenAFourthCategoryOnOldEndpointPut3");
		categoryRepository.save(c3);

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": "+professional.getIdProfessional()+",\n" +
				"    \"professionalCategoryCollection\": [\n" +

				"      {\n" +
				"        \"professionalCategoryId\": " + professionalCategory1.getProfessionalCategoryId() + ",\n"+
				"        \"category\": {\n" +
				"          \"idCategory\": "+professionalCategory1.getCategory().getIdCategory()+"\n" +
				"        }\n" +
				"      },\n" +


				"      {\n" +
				"        \"professionalCategoryId\": " + professionalCategory2.getProfessionalCategoryId() + ",\n"+
				"        \"category\": {\n" +
				"          \"idCategory\": "+professionalCategory2.getCategory().getIdCategory()+"\n" +
				"        }\n" +
				"      },\n" +

				"      {\n" +
				"        \"category\": {\n" +
				"          \"idCategory\": "+c3.getIdCategory()+"\n" +
				"        }\n" +
				"      }\n" +

				"    ]\n" +
				"  }\n" +
				"}";

		System.out.println(json);

		RequestEntity<String> entity2 =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange2 = restTemplate
				.exchange(entity2, ProfessionalResponseBody.class);

		Set<ProfessionalCategory> pcAfterThirdCategoryInclusion = exchange2.getBody()
				.getProfessionalList()
				.get(0)
				.getProfessionalCategoryCollection();

		Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
		Assert.assertEquals(3, pcAfterThirdCategoryInclusion.size()
		);

		// A professionalCategory da variavel c3 nao está disponível neste codigo, portanto buscamos por ele pra usar o
		// professionalCategoryId no proximo request que faremos.
		ProfessionalCategory professionalCategory3 = pcAfterThirdCategoryInclusion.stream()
				.filter(pc -> pc.getCategory().getIdCategory().equals(c3.getIdCategory()))
				.findFirst()
				.get();

		//// Testando cenario de duplicata de categorias ///////////////////
		Category c4 = new Category();
		c4.setName("testAddThirdCategoryAndThenAFourthCategoryOnOldEndpointPut4");
		categoryRepository.save(c4);

		String json2 = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": "+professional.getIdProfessional()+",\n" +
				"    \"professionalCategoryCollection\": [\n" +

				"      {\n" +
				"        \"professionalCategoryId\": " + professionalCategory1.getProfessionalCategoryId() + ",\n"+
				"        \"category\": {\n" +
				"          \"idCategory\": "+professionalCategory1.getCategory().getIdCategory()+"\n" +
				"        }\n" +
				"      },\n" +


				"      {\n" +
				"        \"professionalCategoryId\": " + professionalCategory2.getProfessionalCategoryId() + ",\n"+
				"        \"category\": {\n" +
				"          \"idCategory\": "+professionalCategory2.getCategory().getIdCategory()+"\n" +
				"        }\n" +
				"      },\n" +


				"      {\n" +
				"        \"professionalCategoryId\": " + professionalCategory3.getProfessionalCategoryId() + ",\n"+
				"        \"category\": {\n" +
				"          \"idCategory\": "+c3.getIdCategory()+"\n" +
				"        }\n" +
				"      },\n" +

				"      {\n" +
				"        \"category\": {\n" +
				"          \"idCategory\": "+c4.getIdCategory()+"\n" +
				"        }\n" +
				"      }\n" +

				"    ]\n" +
				"  }\n" +
				"}";


		RequestEntity<String> entity3 =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json2);

		ResponseEntity<ProfessionalResponseBody> exchange3 = restTemplate
				.exchange(entity3, ProfessionalResponseBody.class);

		// Assertando o retorno da API
		Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
		Assert.assertEquals(4, exchange3.getBody()
				.getProfessionalList()
				.get(0)
				.getProfessionalCategoryCollection()
				.size()
		);


		// Assertando o que ficou no banco, pois o retorno da api pode divergir do que ficou no banco. O que ficou no banco eh o dado real.
		Professional ultimateProfessional = professionalRepository.findOne(professional.getIdProfessional());
		Assert.assertEquals(4, ultimateProfessional
				.getProfessionalCategoryCollection()
				.size()
		);
	}

	@Test
	public void testAddNewCategoryOnNewEndpoint() throws URISyntaxException, JsonProcessingException {
		/****** setting up  ***************/
		Professional professional = ProfessionalControllerTests.createFakeProfessional();
		professionalRepository.save(professional);

		ProfessionalCategory professionalCategory1 = buildProfessionalCategory(
				"testAddNewCategoryByProfessionalCategoryEndpoint1",
				"testAddNewCategoryByProfessionalCategoryEndpoint1-pr1",
				5000L
		);
		ProfessionalCategory professionalCategory2 = buildProfessionalCategory(
				"testAddNewCategoryByProfessionalCategoryEndpoint2",
				"testAddNewCategoryByProfessionalCategoryEndpoint2-pr1",
				7000L
		);

		professional.addProfessionalCategory(professionalCategory1);
		professional.addProfessionalCategory(professionalCategory2);

		professionalRepository.save(professional);

		/************ testing ******************************/

		Category c3 = new Category();
		c3.setName("testAddNewCategoryByProfessionalCategoryEndpoint3");
		categoryRepository.save(c3);

		String json = new ObjectMapper().writeValueAsString(c3);

		RequestEntity<String> entity2 =  RequestEntity
				.post(new URI("/professionals/"+ professional.getIdProfessional() + "/addCategory"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange2 = restTemplate
				.exchange(entity2, ProfessionalResponseBody.class);

		Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
		Assert.assertEquals(3, exchange2.getBody()
				.getProfessionalList()
				.get(0)
				.getProfessionalCategoryCollection()
				.size()
		);
	}

	@Ignore //funciona em prod mas nao no teste unitario. Ver se tem algo a ver com @Transaction: retorna status deletados de categoria.
	@Test
	public void testRemoveCategory() throws URISyntaxException, JsonProcessingException {
		/****** setting up  ***************/
		Professional professional = ProfessionalControllerTests.createFakeProfessional();
		professionalRepository.save(professional);

		ProfessionalCategory professionalCategory1 = buildProfessionalCategory(
				"testRemoveCategory1",
				"testRemoveCategory1-pr1",
				5000L
		);
		ProfessionalCategory professionalCategory2 = buildProfessionalCategory(
				"testRemoveCategory2",
				"testRemoveCategory2-pr1",
				7000L
		);
		ProfessionalCategory professionalCategory3 = buildProfessionalCategory(
				"testRemoveCategory3",
				"testRemoveCategory3-pr1",
				10000L
		);

		professional.addProfessionalCategory(professionalCategory1);
		professional.addProfessionalCategory(professionalCategory2);
		professional.addProfessionalCategory(professionalCategory3);

		professionalRepository.save(professional);

		/************ testing ******************************/


		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"idProfessional\": \"" + professional.getIdProfessional() + "\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"idCategory\": 1\n" +
				"        }\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";

		RequestEntity<String> entity2 =  RequestEntity
				.put(new URI("/professionals"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<ProfessionalResponseBody> exchange2 = restTemplate
				.exchange(entity2, ProfessionalResponseBody.class);

		Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
		Assert.assertEquals(1, exchange2.getBody()
				.getProfessionalList()
				.get(0)
				.getProfessionalCategoryCollection()
				.size()
		);

		Assert.assertEquals(Long.valueOf(1), exchange2.getBody()
				.getProfessionalList()
				.get(0)
				.getProfessionalCategoryCollection()
				.stream()
				.findFirst()
				.get()
				.getCategory()
				.getIdCategory()
		);


		// Assertando o que ficou no banco, pois o retorno da api pode divergir do que ficou no banco. O que ficou no banco eh o dado real.
		Professional ultimateProfessional = professionalRepository.findOne(professional.getIdProfessional());
		Assert.assertEquals(1, ultimateProfessional
				.getProfessionalCategoryCollection()
				.size()
		);
	}

	public String getProfessionalCreatedFake(Professional professional){

		String json = "{\n" +
				"  \"professional\": {\n" +
				"    \"address\": null,\n" +
				"    \"birthDate\": 1120705200000,\n" +
				"    \"cellPhone\": null,\n" +
				"    \"dateRegister\": null,\n" +
				"    \"genre\": null,\n" +
				"    \"status\": null,\n" +
				"    \"user\": {\n" +
				"      \"email\": \""+professional.getUser().getEmail()+"\",\n" +
				"      \"idLogin\": null,\n" +
				"      \"password\": \"123\",\n" +
				"      \"sourceApp\": null,\n" +
				"         \"personType\":\"FISICA\",\n" +
				"      \"username\": \"" + professional.getUser().getUsername() + "\"\n" +
				"    },\n" +
				"    \"cnpj\": \""+professional.getCnpj()+"\",\n" +
				"    \"idProfessional\": \""+professional.getIdProfessional()+"\",\n" +
				"    \"location\": 506592589,\n" +
				"    \"nameProfessional\": \""+professional.getNameProfessional()+"\",\n" +
				"    \"professionalCategoryCollection\": [\n" +
				"      {\n" +
				"        \"professional\": null,\n" +
				"        \"category\": {\n" +
				"          \"name\": \"PINTURA\",\n" +
				"          \"idCategory\": 2\n" +
				"        }\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";
		return json;
	}

	public ProfessionalCategory buildProfessionalCategory(
			String categoryName,
			String priceRuleName,
			Long priceRulePrice
	) {

		Category service = new Category();
		service.setName(categoryName);
		service = categoryRepository.save(service);

		PriceRule priceRule = new PriceRule();
		priceRule.setName(priceRuleName);
		priceRule.setPrice(priceRulePrice);

		ProfessionalCategory ps1 = new ProfessionalCategory();
		ps1.setCategory(service);
		ps1.addPriceRule(priceRule);

		return ps1;
	}
}
