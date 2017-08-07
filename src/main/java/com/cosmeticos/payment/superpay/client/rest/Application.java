package com.cosmeticos.payment.superpay.client.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cosmeticos.payment.superpay.client.rest.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cosmeticos.payment.superpay.client.rest.model.TransacaoRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {

		log.info(">>>> Teste RESTv2");
		TransacaoRequest request = getRequest();

		/*
		Gson mapper = new Gson();
		String jsonHeader = mapper.toJson(new Usuario("superpay", "superpay"));

		Map<String, String> usuarioMap = new HashMap<>();
		usuarioMap.put("usuario", jsonHeader);

		String jsonRequest = mapper.toJson(request);
		*/

		ObjectMapper om = new ObjectMapper();
		String jsonHeader = om.writeValueAsString(new Usuario("superpay", "superpay"));

		Map<String, String> usuarioMap = new HashMap<>();
		usuarioMap.put("usuario", jsonHeader);

		String jsonRequest = om.writeValueAsString(request);

		log.info(jsonHeader);
		log.info(jsonRequest);
		String response = postJson("https://homologacao.superpay.com.br/checkout/api/v2/transacao", usuarioMap,
				jsonRequest);
		log.info(response);
		log.info("<<<<");
	}

	private TransacaoRequest getRequest() {
		TransacaoRequest result = new TransacaoRequest();
		result.setCodigoEstabelecimento("INCLUIR CODIGO ESTABELECIMENTO SUPERPAY");
		result.setCodigoFormaPagamento(120);

		Transacao transacao = new Transacao();
		transacao.setParcelas(1);
		transacao.setIdioma(1);
		transacao.setUrlCampainha(
				"http://campainha.com.br");
		transacao.setUrlResultado(
				"http://retorno.com.br");
		transacao.setValorDesconto(0);
		transacao.setValor(100);
		transacao.setNumeroTransacao(1);
		result.setTransacao(transacao);
		
		DadosCartao dadosCartao = new DadosCartao();
		dadosCartao.setNomePortador("Teste");
		dadosCartao.setNumeroCartao("44443333222211111");
		dadosCartao.setCodigoSeguranca("123");
		dadosCartao.setDataValidade("10/2017");

		List<ItemPedido> itensDoPedido = new ArrayList<>();
		ItemPedido item = new ItemPedido();
		item.setCodigoProduto("22338");
		item.setNomeProduto("Produto de teste");
		item.setCodigoCategoria("1");
		item.setNomeCategoria("Categoria de testes");
		item.setQuantidadeProduto(1);
		item.setValorUnitarioProduto(100);
		itensDoPedido.add(item);
		result.setItensDoPedido(itensDoPedido);

		DadosCobranca dadosCobranca = new DadosCobranca();
		dadosCobranca.setCodigoCliente(1);
		dadosCobranca.setTipoCliente(1);
		dadosCobranca.setNome("Teste SuperPay");
		dadosCobranca.setEmail("teste@superpay.com.br");
		dadosCobranca.setDataNascimento("24/02/1988");
		dadosCobranca.setSexo("M");
		dadosCobranca.setDocumento("12312312312");
		dadosCobranca.setDocumento2(null);
		Endereco endereco = new Endereco();
		endereco.setLogradouro("R Teste");
		endereco.setNumero("224");
		endereco.setComplemento(null);
		endereco.setCep("1232212");
		endereco.setBairro("JARDIM TESTE");
		endereco.setCidade("SAO PAULO");
		endereco.setEstado("SP");
		endereco.setPais("BR");
		dadosCobranca.setEndereco(endereco);
		List<Telefone> telefone = new ArrayList<>();
		Telefone tel = new Telefone();
		tel.setTipoTelefone(1);
		tel.setDdi("55");
		tel.setDdd("11");
		tel.setTelefone("12123213123");
		telefone.add(tel);
		dadosCobranca.setTelefone(telefone);
		result.setDadosCobranca(dadosCobranca);

		DadosEntrega dadosEntrega = new DadosEntrega();
		dadosEntrega.setNome("Teste SuperPay");
		dadosEntrega.setEmail("teste@superpay.com.br");
		dadosEntrega.setDataNascimento("24/02/1988");
		dadosEntrega.setSexo("M");
		dadosEntrega.setDocumento("12312312312");
		dadosEntrega.setDocumento2(null);
		endereco = new Endereco();
		endereco.setLogradouro("R Teste");
		endereco.setNumero("224");
		endereco.setComplemento(null);
		endereco.setCep("1232212");
		endereco.setBairro("JARDIM TESTE");
		endereco.setCidade("São Paulo");
		endereco.setEstado("SP");
		endereco.setPais("BR");
		dadosEntrega.setEndereco(endereco);
		dadosEntrega.setTelefone(telefone);
		result.setDadosEntrega(dadosEntrega);

		return result;
	}

	private String postJson(String url, Map<String, String> headers, String data) {
		RestTemplate restTemplate = restTemplateBuilder.build();

		try {

			RequestEntity<String> entity = RequestEntity
					.post(new URI(url))
					.contentType(MediaType.APPLICATION_JSON)
					.header("usuario", headers.get("usuario"))
					.accept(MediaType.APPLICATION_JSON)
					.body(data);

			ResponseEntity<String> result = restTemplate
					.exchange(entity, String.class);

		} catch (Exception e) {
			log.error(e.toString());
			result = e.toString();

		}

			/*
			HttpPost post = new HttpPost(url);

			post.setEntity(new BufferedHttpEntity(
					new InputStreamEntity(new ByteArrayInputStream(data.getBytes(Charset.forName("UTF-8"))))));

			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json;charset=UTF-8");
			if (headers != null) {
				for (Entry<String, String> h : headers.entrySet()) {
					post.setHeader(h.getKey(), h.getValue());
				}
			}

			int connectionTimeout = 63_000;
			int readTimeout = 10 * connectionTimeout;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(connectionTimeout)
					.setSocketTimeout(readTimeout).build();
			try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
				HttpResponse response = client.execute(post);
				log.info("Response status code: "
						+ (response.getStatusLine() == null ? 0 : response.getStatusLine().getStatusCode()));

				result = EntityUtils.toString(response.getEntity());
			}

		} catch (Exception e) {
			log.error(e.toString());
			result = e.toString();
		}
		*/

		return result;
	}
}
