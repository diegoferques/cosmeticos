package com.cosmeticos.payment;

import static org.assertj.core.api.Assertions.assertThat;

import com.cosmeticos.payment.superpay.ws.oneclick.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore //TODO: Ver pq executar os testes sem profile esta executando esta classe.
@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class SuperpayOneClickClientIntegrationTest {

	@Autowired
	private SuperpayOneClickClient oneClickClient;

	@Test
	public void testSayHello() {
		// vo rodar de novo

		String dataValidadeCartao = "10/10";
		String emailComprador = "deivison.1@gmail.com";
		Long formaPagamento = 1L;
		String nomeTitularCartaoCredito = "deivison";
		String numeroCartaoCredito = "1234567890";

		String result = addCard(dataValidadeCartao, emailComprador, formaPagamento, nomeTitularCartaoCredito, numeroCartaoCredito);

		assertThat(result).isNotBlank();
	}

	@Test
	public void testPagamento(){

		String dataValidadeCartao = "10/10";
		String emailComprador = "deivison.1@gmail.com";
		Long formaPagamento = 1L;
		String nomeTitularCartaoCredito = "deivison";
		String numeroCartaoCredito = "1234567890";

		String token = addCard(dataValidadeCartao, emailComprador, formaPagamento, nomeTitularCartaoCredito, numeroCartaoCredito);

		Long numeroTransacao = 123L;
		int idioma = 1;
		String ip = "1234-1233-1234-2123";
		String origemTransacao = "";//
		int parcelas = 1;
		//String token = "12345"; // Ja eh definido no addcard
		String campainha = "";
		String urlRedirecionamentoNaoPago = "oiuoiu.com";//travou?
		String urlRedirecionamentoPago = "pooiuyu.com";
		long valor = 50L;
		long valorDesconto = 0;
		String bairroEnderecoComprador = "austin";// pensando aki.. deixa eu ver se caso eu coloque na ordem se vai funfar
		String cepEnderecoComprador = "268723-897";
		String cidadeEnderecoCompra = "nova iguaçu";
		String codigoCliente = "0987";
		//long codigoTipoTelefoneComprador = ;
		//String complementoEnderecoComprador = "";
		String dataNascimentoComprador = "09/03/1991";
		//String emailComprador = "email@email.com"; // Ja eh definido no addcard
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

		ResultadoPagamentoWS result = pay(numeroTransacao, idioma, ip, origemTransacao, parcelas,

				token, // Gerado pelo addcard la no inicio deste teste.

				campainha, urlRedirecionamentoNaoPago, urlRedirecionamentoPago, valor, valorDesconto, bairroEnderecoComprador, cepEnderecoComprador, cidadeEnderecoCompra, codigoCliente, dataNascimentoComprador, emailComprador, enderecoComprador, estadoEnderecoComprador, nomeComprador, numeroEnderecoComprador, paisComprar, sexoComprador, telefoneAdicionalComprador, telefoneComprador, tipoCliente, codigoTipoTelefoneAdicionalComprador, codigoCategoria, nomeCategoria, codigoProduto, nomeProduto, quantidadeProduto, valorUnitarioProduto, cvv);

		assertThat(result.getStatusTransacao())
				.isIn(1, 2, 5); // 5=Transacao em Andamento. Acho q tbm vale como OK.


		}

	@Test
	public void testUpdateCard() {

		String dataValidadeCartao = "01/10";
		String emailComprador = "deivison.2@gmail.com";
		Long formaPagamento = 1L;
		String nomeTitularCartaoCredito = "deivison";
		String numeroCartaoCredito = "1234567899990";

		String token = addCard(dataValidadeCartao, emailComprador, formaPagamento, nomeTitularCartaoCredito, numeroCartaoCredito);

		String result = (oneClickClient.updateCard(
				dataValidadeCartao,
				emailComprador,
				formaPagamento,
				nomeTitularCartaoCredito,
				numeroCartaoCredito,
				token));

		System.out.println(result);

		assertThat(result).isNotBlank();
	}

	@Test
	public void testReadCard() {

		String dataValidadeCartao = "10/10";
		String emailComprador = "deivison.1@gmail.com";
		Long formaPagamento = 1L;
		String nomeTitularCartaoCredito = "deivison";
		String numeroCartaoCredito = "1234567890";

		String token = addCard(dataValidadeCartao, emailComprador, formaPagamento, nomeTitularCartaoCredito, numeroCartaoCredito);


		DadosCadastroPagamentoOneClickWS result = (oneClickClient.readCard(
				dataValidadeCartao,
				emailComprador,
				formaPagamento,
				nomeTitularCartaoCredito,
				numeroCartaoCredito,
				token));

		System.out.println(result);

		assertThat(result).isNotNull();
	}

	private String addCard(String dataValidadeCartao, String emailComprador, Long formaPagamento, String nomeTitularCartaoCredito, String numeroCartaoCredito) {
		String result = String.valueOf(oneClickClient.addCard(
				dataValidadeCartao,
				emailComprador,
				formaPagamento,
				nomeTitularCartaoCredito,
				numeroCartaoCredito));

		System.out.println(result);
		return String.valueOf(result);
	}

	private ResultadoPagamentoWS pay(Long numeroTransacao,
									 int idioma, String ip,
									 String origemTransacao,
									 int parcelas, String token,
									 String campainha,
									 String urlRedirecionamentoNaoPago,
									 String urlRedirecionamentoPago,
									 long valor,
									 long valorDesconto,
									 String bairroEnderecoComprador,
									 String cepEnderecoComprador,
									 String cidadeEnderecoCompra,
									 String codigoCliente,
									 String dataNascimentoComprador,
									 String emailComprador,
									 String enderecoComprador,
									 String estadoEnderecoComprador,
									 String nomeComprador,
									 String numeroEnderecoComprador,
									 String paisComprar,
									 String sexoComprador,
									 String telefoneAdicionalComprador,
									 String telefoneComprador,
									 long tipoCliente,
									 long codigoTipoTelefoneAdicionalComprador,
									 String codigoCategoria, String nomeCategoria,
									 String codigoProduto, String nomeProduto,
									 int quantidadeProduto,
									 Long valorUnitarioProduto, String cvv) {


		SuperpayOneClickClient.RequestWrapper requestWrapper = new SuperpayOneClickClient.RequestWrapper();
		requestWrapper.setBairroEnderecoComprador(bairroEnderecoComprador);
		requestWrapper.setCampainha(campainha);
		requestWrapper.setCepEnderecoComprador(cepEnderecoComprador);
		requestWrapper.setCidadeEnderecoCompra(cidadeEnderecoCompra);
		requestWrapper.setCvv(cvv);
		requestWrapper.setEstadoEnderecoComprador(estadoEnderecoComprador);
		requestWrapper.setEnderecoComprador(enderecoComprador);
		requestWrapper.setEmailComprador(emailComprador);
		requestWrapper.setNomeCategoria(nomeCategoria);
		requestWrapper.setNomeComprador(nomeComprador);
		requestWrapper.setNomeProduto(nomeProduto);
		requestWrapper.setNumeroEnderecoComprador(numeroEnderecoComprador);
		requestWrapper.setNumeroTransacao(numeroTransacao);
		requestWrapper.setPaisComprador(paisComprar);
		requestWrapper.setSexoComprador(sexoComprador);
		requestWrapper.setTelefoneAdicionalComprador(telefoneComprador);
		requestWrapper.setTelefoneComprador(telefoneComprador);
		requestWrapper.setTipoCliente(tipoCliente);
		requestWrapper.setToken(token);
		requestWrapper.setValor(valor);
		requestWrapper.setValorUnitarioProduto(valorUnitarioProduto);
		requestWrapper.setUrlRedirecionamentoNaoPago(urlRedirecionamentoNaoPago);
		requestWrapper.setUrlRedirecionamentoPago(urlRedirecionamentoPago);

		ResultadoPagamentoWS result = (oneClickClient.pay(requestWrapper));

		System.out.println(result);
		return result;
	}
}