package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import org.junit.Assert;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	private Customer testCreateOK;

	private String emailTeste = "";

	/**
	 * Inicializa o H2 com dados iniciais.
	 */
	@Test
	public void testCreateOK() throws IOException, URISyntaxException {

		String email;

		if(emailTeste.isEmpty()) {
			email = "b@b.com";
		} else {
			email = emailTeste;
		}

		System.out.println("Email em uso: " + email);

		String content = "{\n" +
				"   \"customer\":{\n" +
				"      \"address\":{\n" +
				"         \"address\":null,\n" +
				"         \"cep\":null,\n" +
				"         \"city\":null,\n" +
				"         \"country\":null,\n" +
				"         \"idAddress\":null,\n" +
				"         \"neighborhood\":null,\n" +
				"         \"complement\":null,\n" +
				"         \"state\":null\n" +
				"      },\n" +
				"      \"birthDate\":1310353200000,\n" +
				"      \"cellPhone\":null,\n" +
				"      \"dateRegister\":null,\n" +
				"      \"genre\":null,\n" +
				"      \"status\":null,\n" +
				"      \"user\":{\n" +
				"         \"email\":\""+ email +"\",\n" +
				"         \"idLogin\":null,\n" +
				"         \"password\":\"123\",\n" +
				"         \"sourceApp\":null,\n" +
				"         \"username\":\""+ email +"\"\n" +
				"      },\n" +
				"      \"cpf\":\"05404577726\",\n" +
				"      \"idAddress\":null,\n" +
				"      \"idCustomer\":null,\n" +
				"      \"idLogin\":null,\n" +
				"      \"nameCustomer\":\"foo bar\"\n" +
				"   }\n" +
				"}";

		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/customers"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(content);

		ResponseEntity<CustomerResponseBody> exchange = restTemplate
				.exchange(entity, CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
		Assert.assertEquals("foo bar", exchange.getBody().getCustomerList().get(0).getNameCustomer());

		testCreateOK = exchange.getBody().getCustomerList().get(0);
	}

	//TODO - FIZ PULL DE DEV PARA INICIAR UM NOVO CARD E COMECOU A APRESENTAR ERRO ABAIXO
	//TESTEI DAS DUAS FORMAS (COMENTADA E DESCOMENTADA), FIQUEI MAIS DE 1 HORA TENTANDO RESOLVER
	//PAREI PARA DAR CONTINUIDADE NO MEU CARD RFN42
	// TODO: corrigir este teste pq esta chegando customer null no controller
	//@Ignore
	@Test
	public void testUpdateOK() throws IOException, URISyntaxException {
		emailTeste = "emailUpdateOk@teste.com";
		this.testCreateOK();

		String content = "{\n" +
				"   \"customer\":{\n" +
				"      \"idCustomer\":"+ testCreateOK.getIdCustomer() +",\n" +
				"      \"nameCustomer\":\"Diego Fernandes Marques da Silva\",\n" +
				"      \"birthDate\":\""+Calendar.getInstance().getTime().getTime()+"\",\n" +
				"      \"cpf\":\"05406688898\"\n" +
				"   }\n" +
				"}";

		System.out.println(content);

		RequestEntity<String> entity =  RequestEntity
				.put(new URI("/customers"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(content);

		ResponseEntity<CustomerResponseBody> exchange = restTemplate
				.exchange(entity, CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
		Assert.assertEquals("Diego Fernandes Marques da Silva", exchange.getBody().getCustomerList().get(0).getNameCustomer());

/*
		Customer c1 = testCreateOK;
		c1.setNameCustomer("Diego Fernandes Marques da Silva");

		CustomerRequestBody cr = new CustomerRequestBody();
		cr.setCustomer(c1);

		final ResponseEntity<CustomerResponseBody> exchange = //
				restTemplate.exchange( //
						"/customers", //
						HttpMethod.PUT, //
						new HttpEntity(cr), // Body
						CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
		Assert.assertEquals("Diego Fernandes Marques da Silva", exchange.getBody().getCustomerList().get(0).getNameCustomer());
		*/
	}

	@Test
	public void testFindById() throws ParseException {

		final ResponseEntity<CustomerResponseBody> exchange = //
				restTemplate.exchange( //
						"/customers/1", //
						HttpMethod.GET, //
						null,
						CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

	}

	@Test
	public void testDeleteForbiden() throws ParseException {

		final ResponseEntity<CustomerResponseBody> exchange = //
				restTemplate.exchange( //
						"/customers/1", //
						HttpMethod.DELETE, //
						null,
						CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

	}

	// TODO - Aparentemente o erro é por conta do retorno infinito de CustomerCollection, o mesmo erro de testFindById
	@Test
	public void testLastest10OK() throws ParseException {

		final ResponseEntity<CustomerResponseBody> exchange = //
				restTemplate.exchange( //
						"/customers", //
						HttpMethod.GET, //
						null,
						CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

	}

	@Test
	public void testCreateUserEmailBadRequest() throws IOException, URISyntaxException {

		emailTeste = "emailcreateteste@email.com";
		testCreateOK();

		System.out.println("Email em uso: " + emailTeste);

		String content = "{\n" +
				"   \"customer\":{\n" +
				"      \"address\":{\n" +
				"         \"address\":null,\n" +
				"         \"cep\":null,\n" +
				"         \"city\":null,\n" +
				"         \"country\":null,\n" +
				"         \"idAddress\":null,\n" +
				"         \"neighborhood\":null,\n" +
				"         \"complement\":null,\n" +
				"         \"state\":null\n" +
				"      },\n" +
				"      \"birthDate\":1310353200000,\n" +
				"      \"cellPhone\":null,\n" +
				"      \"dateRegister\":null,\n" +
				"      \"genre\":null,\n" +
				"      \"status\":null,\n" +
				"      \"user\":{\n" +
				"         \"email\":\""+ emailTeste +"\",\n" +
				"         \"idLogin\":null,\n" +
				"         \"password\":\"a1b2c3\",\n" +
				"         \"sourceApp\":null,\n" +
				"         \"username\":\""+ emailTeste +"\"\n" +
				"      },\n" +
				"      \"cpf\":\"09840387913\",\n" +
				"      \"idAddress\":null,\n" +
				"      \"idCustomer\":null,\n" +
				"      \"idLogin\":null,\n" +
				"      \"nameCustomer\":\"Doug\"\n" +
				"   }\n" +
				"}";

		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/customers"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(content);

		ResponseEntity<CustomerResponseBody> exchange = restTemplate
				.exchange(entity, CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
		Assert.assertEquals("E-mail já existente.", exchange.getBody().getDescription());
	}


	//RNF68
	@Test
	public void testUpdateUserEmailBadRequest() throws IOException, URISyntaxException {
		emailTeste = "emailteste@email.com";
		testCreateOK();

		Customer c1 = testCreateOK;

		String json = "{\n" +
				"   \"customer\":{\n" +
				"      \"idCustomer\":"+ c1.getIdCustomer() +",\n" +
				"      \"user\":{\n" +
				"         \"email\":\""+ emailTeste +"\",\n" +
				"         \"idLogin\":"+c1.getUser().getIdLogin()+"\n" +
				"      },\n" +
				"      \"nameCustomer\":\"Usuario Alterado\"\n" +
				"   }\n" +
				"}";

		System.out.println(json);

		RequestEntity<String> entity =  RequestEntity
				.put(new URI("/customers"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		ResponseEntity<CustomerResponseBody> exchange = restTemplate
				.exchange(entity, CustomerResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
		Assert.assertEquals("E-mail já existente.", exchange.getBody().getDescription());


	}

	static User createFakeLogin(Customer c) {
		User u = new User();
		u.setEmail("diego@bol.com");
		//u.setUser(1234L);
		u.setPassword("123qwe");
		u.setSourceApp("google+");
		u.setUsername("diegoferques");
		//u.getCustomerCollection().add(c);
		return u;
	}

	static Address createFakeAddress(Customer customer) {
		Address a = new Address();
		a.setAddress("Rua Perlita");
		a.setCep("0000000");
		a.setCity("RJO");
		a.setCountry("BRA");
		a.setNeighborhood("Austin");
		a.setState("RJ");
		a.setComplement("HOUSE");
		//a.getCustomerCollection().add(customer);
		return a;
	}

	public static Customer createFakeCustomer() {
		Customer c1 = new Customer();
		c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
		c1.setCellPhone("(21) 98877-6655");
		c1.setCpf("098.765.432-10");
		c1.setDateRegister(Calendar.getInstance().getTime());
		c1.setGenre('M');
		c1.setNameCustomer("João da Silva");
		//c1.setOrderCollection(null);
		c1.setStatus(Customer.Status.ACTIVE.ordinal());
		c1.setAddress(createFakeAddress(c1));
		c1.setUser(createFakeLogin(c1));

		return c1;
	}
}
