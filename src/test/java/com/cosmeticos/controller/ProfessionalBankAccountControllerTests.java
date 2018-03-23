package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.model.BankAccount;
import com.cosmeticos.model.Professional;
import com.cosmeticos.repository.ProfessionalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfessionalBankAccountControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ProfessionalRepository professionalRepository;

	@Test
	public void testCreateBankAccountSuccess() throws IOException, URISyntaxException {

		Professional professional = ProfessionalControllerTests.createFakeProfessional();
		professionalRepository.save(professional);

		BankAccount ba = new BankAccount();
		ba.setOwnerName("seu ze");
		ba.setOwnerCPF("000.000.000-00");
		ba.setAccountNumber("123");
		ba.setFinancialInstitute("abc");
		ba.setAgency("321");
		ba.setType(BankAccount.Type.CONTA_CORRENTE);

		String json = new ObjectMapper().writeValueAsString(ba);

		System.out.println(json);


		ResponseEntity<Void> exchange = post(professional, json);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
	}


	@Test
	public void testCreateBankAccountBadRequestNoBody() throws IOException, URISyntaxException {

		Professional professional = ProfessionalControllerTests.createFakeProfessional();
		professionalRepository.save(professional);

		BankAccount ba = new BankAccount();
		ba.setAccountNumber("123");
		ba.setFinancialInstitute("abc");
		ba.setAgency("321");
		ba.setType(BankAccount.Type.CONTA_CORRENTE);

		String json = new ObjectMapper().writeValueAsString(ba);

		System.out.println(json);

		ResponseEntity<Void> exchange = post(professional, json);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
	}

	@Test
	public void testCreateBankAccountBadRequestNotFount() throws IOException, URISyntaxException {

		BankAccount ba = new BankAccount();
		ba.setOwnerName("seu ze");
		ba.setOwnerCPF("000.000.000-00");
		ba.setAccountNumber("123");
		ba.setFinancialInstitute("abc");
		ba.setAgency("321");
		ba.setType(BankAccount.Type.CONTA_CORRENTE);

		String json = new ObjectMapper().writeValueAsString(ba);

		System.out.println(json);


		Professional professional = new Professional();
		professional.setIdProfessional(0L);

		ResponseEntity<Void> exchange = post(professional, json);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.NOT_FOUND, exchange.getStatusCode());
	}

	@Test
	public void testCreateBankAccountAlreadyHaveAccount() throws IOException, URISyntaxException {

		Professional professional = ProfessionalControllerTests.createFakeProfessional();
		professionalRepository.save(professional);

		BankAccount ba = new BankAccount();
		ba.setOwnerName("seu ze");
		ba.setOwnerCPF("000.000.000-00");
		ba.setAccountNumber("123");
		ba.setFinancialInstitute("abc");
		ba.setAgency("321");
		ba.setType(BankAccount.Type.CONTA_CORRENTE);

		String json = new ObjectMapper().writeValueAsString(ba);

		System.out.println(json);

		post(professional, json);

		// Se nao fizer isso aqui, o segundo request nao ve o bank account criado no primeiro request. Ele vem nulo.
		professionalRepository.flush();

		BankAccount ba2 = new BankAccount();
		ba2.setOwnerName("seu ze");
		ba2.setOwnerCPF("000.000.000-00");
		ba2.setAccountNumber("123456");
		ba2.setFinancialInstitute("abcdef");
		ba2.setAgency("321012");
		ba2.setType(BankAccount.Type.CONTA_CORRENTE);

		json = new ObjectMapper().writeValueAsString(ba2);

		System.out.println(json);

		ResponseEntity<Void> exchange2 = post(professional, json);

		Assert.assertNotNull(exchange2);
		Assert.assertEquals(HttpStatus.CONFLICT, exchange2.getStatusCode());
		Assert.assertEquals("BANK_ACCOUNT_ALREADY_CREATED", exchange2.getHeaders().getFirst("badRequestDetail"));

	}

	@Test
	public void testFindBankAccountSuccess() throws IOException, URISyntaxException {

		Professional professional = ProfessionalControllerTests.createFakeProfessional();
		professionalRepository.save(professional);

		BankAccount ba = new BankAccount();
		ba.setOwnerName("seu ze");
		ba.setOwnerCPF("000.000.000-00");
		ba.setAccountNumber("123");
		ba.setFinancialInstitute("abc");
		ba.setAgency("321");
		ba.setType(BankAccount.Type.CONTA_CORRENTE);

		String json = new ObjectMapper().writeValueAsString(ba);

		System.out.println(json);

		post(professional, json);

		// Se nao fizer isso aqui, o segundo request nao ve o bank account criado no primeiro request. Ele vem nulo.
		professionalRepository.flush();

		ResponseEntity<BankAccount> exchange = restTemplate.getForEntity(
				new URI("/professionals/" + professional.getIdProfessional() + "/bankAccount"),
				BankAccount.class
		);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
		Assert.assertTrue(exchange.getBody() != null);
		Assert.assertTrue(exchange.getBody().getFinancialInstitute() != null);
		Assert.assertTrue(exchange.getBody().getAgency() != null);
		Assert.assertTrue(exchange.getBody().getAccountNumber() != null);
		Assert.assertTrue(exchange.getBody().getType() != null);
		Assert.assertTrue(exchange.getBody().getOwnerName() != null);
		Assert.assertTrue(exchange.getBody().getOwnerCPF() != null);
	}

	@Test
	public void testFindBankAccountWithoutAccount() throws IOException, URISyntaxException {

		Professional professional = ProfessionalControllerTests.createFakeProfessional();
		professionalRepository.save(professional);

		ResponseEntity<BankAccount> exchange = restTemplate.getForEntity(
				new URI("/professionals/" + professional.getIdProfessional() + "/bankAccount"),
				BankAccount.class
		);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
	}

	@Test
	public void testFindBankAccountInvalidProfessionalId() throws IOException, URISyntaxException {

		ResponseEntity<BankAccount> exchange = restTemplate.getForEntity(
				new URI("/professionals/0/bankAccount"),
				BankAccount.class
		);

		Assert.assertNotNull(exchange);
		Assert.assertEquals(HttpStatus.NOT_FOUND, exchange.getStatusCode());
	}

	private ResponseEntity<Void> post(Professional professional, String json) throws URISyntaxException {
		RequestEntity<String> entity =  RequestEntity
				.post(new URI("/professionals/"+ professional.getIdProfessional() + "/bankAccount"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(json);

		return restTemplate.exchange(entity, Void.class);
	}

}
