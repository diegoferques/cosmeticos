package com.cosmeticos.payment;

import static org.assertj.core.api.Assertions.assertThat;

import com.cosmeticos.payment.superpay.ws.ResultadoPagamentoWS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class SuperpayOneClickClientIntegrationTest {

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

	@Test
	public void testPagamento(){

		Long numeroTransacao = 123L;
		int idioma = 1;
		String ip = "1234-1233-1234-2123";
		String origemTransacao = "";
		int parcelas = 1;
		String token = "12345";
		String campainha = "";
		String urlRedirecionamentoNaoPago = "oiuoiu.com";
		String urlRedirecionamentoPago = "pooiuyu.com";
		long valor = 50L;
		long valorDesconto = 0;
		String bairroEnderecoComprador = "austin";
		String cepEnderecoComprador = "268723-897";
		String cidadeEnderecoCompra = "nova iguaçu";
		String codigoCliente = "0987";
		//long codigoTipoTelefoneComprador = ;
		//String complementoEnderecoComprador = "";
		String dataNascimentoComprador = "09/03/1991";
		String emailComprador = "email@email.com";
		String enderecoComprador = "rua do peixonalta";
		String estadoEnderecoComprador = "Rio d Janeiro";
		String nomeComprador = "Fulano";
		String numeroEnderecoComprador = "66";
		String paisComprar = "Brasil";
		String sexoComprador = "M";
		String telefoneAdicionalComprador = "98762-0987";
		String telefoneComprador = "94567-7654";
		long tipoCliente = 1;
		long codigoTipoTelefoneAdicionalComprador = 2;
		String codigoCategoria = "12345-123";
		String nomeCategoria = "nome";
		String codigoProduto = "Prdutocodigo";
		String nomeProduto = "Nomeproduto";
		int quantidadeProduto = 1;
		Long valorUnitarioProduto = 50L;
		String cvv = "948576";

		String result = (oneClickClient.pay(
				numeroTransacao,
				idioma,
				ip,
				origemTransacao,
				parcelas,
				token,
				campainha,
				urlRedirecionamentoNaoPago,
				urlRedirecionamentoPago,
				valor,
				valorDesconto,
				bairroEnderecoComprador,
				cepEnderecoComprador,
				cidadeEnderecoCompra,
				codigoCliente,
				dataNascimentoComprador,
				emailComprador,
				enderecoComprador,
				estadoEnderecoComprador,
				nomeComprador,
				numeroEnderecoComprador,
				paisComprar,
				sexoComprador,
				telefoneAdicionalComprador,
				telefoneComprador,
				tipoCliente,
				codigoTipoTelefoneAdicionalComprador,
				codigoCategoria,
				nomeCategoria,
				codigoProduto,
				nomeProduto,
				quantidadeProduto,
				valorUnitarioProduto,
				nomeComprador,
				estadoEnderecoComprador,
				numeroEnderecoComprador,
				sexoComprador,
				telefoneAdicionalComprador,
				telefoneComprador,
				cvv));

		System.out.println(result);

		assertThat(result);

		}

	@Test
	public void testUpdateCard() {
		String dataValidadeCartao = "01/10";
		String emailComprador = "deivison.2@gmail.com";
		Long formaPagamento = 1L;
		String nomeTitularCartaoCredito = "deivison";
		String numeroCartaoCredito = "1234567890";
		String token = "0987-0876564";

		ResultadoPagamentoWS result = (oneClickClient.updateCard(
				dataValidadeCartao,
				emailComprador,
				formaPagamento,
				nomeTitularCartaoCredito,
				numeroCartaoCredito, token));

		System.out.println(result);

		assertThat(result);
	}


}