package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.model.Address;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
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

	@MockBean
	private ProfessionalService mockedService;

	/**
	 * Inicializa o H2 com dados iniciais.
	 */
	@Before
	public void setupTests() throws ParseException {

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
		//c1.setServiceRequestCollection(null);
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
		//c2.setServiceRequestCollection(null);
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
		//c3.setServiceRequestCollection(null);
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
		//c4.setServiceRequestCollection(null);
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
		//c5.setServiceRequestCollection(null);
		c5.setStatus(Professional.Status.ACTIVE);
		c5.setIdAddress(this.createFakeAddress(c5));
		c5.setIdLogin(this.createFakeLogin(c5));

		customerRepository.save(c1);
		customerRepository.save(c2);
		customerRepository.save(c3);
		customerRepository.save(c4);
		customerRepository.save(c5);
	}

	@Test
	public void testCreateOK() throws IOException {


		String content = new String(Files.readAllBytes(Paths.get("C:\\dev\\_freelas\\Deivison\\projetos\\cosmeticos\\src\\test\\resources\\custumerPostRequest.json")));

		Professional customer = createFakeProfessional();
		Address addres = createFakeAddress(customer);
		User user = createFakeLogin(customer);

		ProfessionalRequestBody requestBody = new ProfessionalRequestBody();
		requestBody.setAddress(addres);
		requestBody.setUser(user);
		requestBody.setProfessional(customer);

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
	public void testCreateError500() throws IOException {
		/**/
		Mockito.when(
				mockedService.create(Mockito.anyObject())
		).thenThrow(new RuntimeException());

		ProfessionalRequestBody requestBody = createFakeRequestBody();

		final ResponseEntity<ProfessionalResponseBody> exchange = //
				restTemplate.exchange( //
						"/professionals", //
						HttpMethod.POST, //
						new HttpEntity(requestBody), // Body
						ProfessionalResponseBody.class);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
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
	public void testFindById() throws ParseException {

		Mockito.when(
				mockedService.find(Long.valueOf(1))
		).thenReturn(Optional.of(new Professional()));

		final ResponseEntity<ProfessionalResponseBody> exchange = //
				restTemplate.exchange( //
						"/professionals/1", //
						HttpMethod.GET, //
						null,
						ProfessionalResponseBody.class);

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

	private ProfessionalRequestBody createFakeRequestBody() {
		Professional customer = createFakeProfessional();
		Address address = createFakeAddress(customer);
		User user = createFakeLogin(customer);

		ProfessionalRequestBody requestBody = new ProfessionalRequestBody();
		requestBody.setAddress(address);
		requestBody.setUser(user);
		requestBody.setProfessional(customer);

		return requestBody;
	}

	public User createFakeLogin(Professional c) {
		User u = new User();
		u.setEmail("diego@bol.com");
		u.setPassword("123qwe");
		u.setSourceApp("google+");
		u.setUsername("diegoferques");

		return u;
	}

	private Address createFakeAddress(Professional customer) {
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
		//c1.setServiceRequestCollection(null);
		c1.setStatus(Professional.Status.ACTIVE);
		c1.setIdAddress(this.createFakeAddress(c1));
		c1.setIdLogin(this.createFakeLogin(c1));

		return c1;
	}
}
