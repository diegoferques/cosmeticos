package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.commons.ProfessionalServicesResponseBody;
import com.cosmeticos.commons.WalletResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WalletControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;


	@Autowired
	private WalletRepository walletRepository;


	@Autowired
	private ProfessionalRepository professionalRepository;

	@Autowired
	private CustomerRepository customerRepository;

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
	public void testExampleApiFindWalletWithTwoCustomersByProfessionalUsername() throws ParseException {

		/* Preparando o cenario para testar */

		// Clientes existentes procuram o profissional pela segunda vez
		Customer c1 = CustomerControllerTests.createFakeCustomer();
		c1.setNameCustomer("c1");
		Customer c2 = CustomerControllerTests.createFakeCustomer();
		c2.setNameCustomer("c2");

		customerRepository.save(c1);
		customerRepository.save(c2);


		User user5;
		Professional s5 = new Professional();
		s5.setNameProfessional("Habib");
		s5.setAddress(new Address());
		s5.setUser(user5 = new User("Habib", "123qwe", "Habib@bol"));
		user5.setProfessional(s5);

		// apagueme

		// Criamos a wallet para adicionar os clientes
		Wallet wallet = new Wallet();
		wallet.getCustomers().add(c1);
		wallet.getCustomers().add(c2);

		//s5.getUser().setUsername("userWithWallet");

		// bi-direcional association
		s5.setWallet(wallet);
		wallet.setProfessional(s5);

		// Criamos um profissional com dois clientes na carteira
		professionalRepository.save(s5);


		// Aqui comeca o teste...

		final ResponseEntity<WalletResponseBody> getExchange = //
				restTemplate.exchange( //
						"/wallets?professional.user.username=Habib", //
						HttpMethod.GET, //
						null,
						WalletResponseBody.class);

		Assert.assertEquals(HttpStatus.OK, getExchange.getStatusCode());


		List<Wallet> entityList = getExchange.getBody().getWalletList();
		Assert.assertFalse("Foi adicionada wallet, nao deveria estar vazio.", entityList.isEmpty());
		Assert.assertEquals("Deveria haver apenas 1 wallet por profissional.", 1, entityList.size());


		Wallet receivedWallet = entityList.stream().findFirst().get();
		Assert.assertEquals("Nao foram retornados os cclientes adicionados na wallets.",
				2,
				receivedWallet.getCustomers().size());
	}
}