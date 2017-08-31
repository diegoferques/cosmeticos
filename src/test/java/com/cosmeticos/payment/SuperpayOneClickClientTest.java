package com.cosmeticos.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class SuperpayOneClickClientTest {

	@Autowired
	private SuperpayOneClickClient oneClickClient;

	@Test
	public void testSayHello() {
		String dataValidadeCartao = "10/10";
		String emailComprador = "deivison.1@gmail.com";
		Long formaPagamento = 1L;
		String nomeTitularCartaoCredito = "deivison";
		String numeroCartaoCredito = "1234567890";

		String result = oneClickClient.addCard(
				dataValidadeCartao, 
				emailComprador, 
				formaPagamento,
				nomeTitularCartaoCredito, 
				numeroCartaoCredito);

		System.out.println(result);
		
		assertThat(result).isNotBlank();
	}
}