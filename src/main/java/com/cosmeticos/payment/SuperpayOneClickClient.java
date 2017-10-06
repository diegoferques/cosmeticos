package com.cosmeticos.payment;

import com.cosmeticos.payment.superpay.ws.oneclick.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.JAXBElement;

@Component
@lombok.extern.slf4j.Slf4j
public class SuperpayOneClickClient {

	@Autowired
	@Qualifier("webServiceTemplateOneClick")
	private WebServiceTemplate webServiceTemplate;

	@Value("${superpay.senha}")
	private String password;

	@Value("${superpay.login}")
	private String user;

	@Value("${superpay.estabelecimento}")
	private String codigoEstabelecimento;

	/**
	 * 
	 * 
	 * @param dataValidadeCartao
	 * @param emailComprador
	 * @param formaPagamento
	 * @param nomeTitularCartaoCredito
	 * @param numeroCartaoCredito
	 * @return
	 */
	public String addCard(
			String dataValidadeCartao,
			String emailComprador,
			Long formaPagamento,
			String nomeTitularCartaoCredito,
			String numeroCartaoCredito) {
		
		ObjectFactory factory = new ObjectFactory();

		DadosCadastroPagamentoOneClickWSV2 dados = factory.createDadosCadastroPagamentoOneClickWSV2();
		dados.setCodigoEstabelecimento(codigoEstabelecimento);
		dados.setDataValidadeCartao(dataValidadeCartao);
		dados.setEmailComprador(emailComprador);
		dados.setFormaPagamento(formaPagamento);
		dados.setNomeTitularCartaoCredito(nomeTitularCartaoCredito);
		dados.setNumeroCartaoCredito(numeroCartaoCredito);

		CadastraPagamentoOneClickV2 pagamentoOneClickV2 = factory.createCadastraPagamentoOneClickV2();
		pagamentoOneClickV2.setDadosOneClick(dados);
		pagamentoOneClickV2.setSenha(password);
		pagamentoOneClickV2.setUsuario(user);

		log.info("Client sending pagamentoOneClickV2[user={},estabelecimento={}]", user, codigoEstabelecimento);

		JAXBElement<CadastraPagamentoOneClickV2> jaxbCadastraPagamento =
				factory.createCadastraPagamentoOneClickV2(pagamentoOneClickV2);

		JAXBElement<CadastraPagamentoOneClickV2Response> jaxbResponse =
				(JAXBElement<CadastraPagamentoOneClickV2Response>) webServiceTemplate.marshalSendAndReceive(jaxbCadastraPagamento);

		CadastraPagamentoOneClickV2Response oneclickResponse = jaxbResponse.getValue();
		
		log.info("Client received result='{}'", oneclickResponse.getReturn());
		
		return oneclickResponse.getReturn();
	}

	public ResultadoPagamentoWS pay(SuperpayOneClickClient.RequestWrapper requestWrapper){

		ObjectFactory factory = new ObjectFactory();

		DadosUsuarioTransacaoCompletaWS dadosUsuarioTransacao = factory.createDadosUsuarioTransacaoCompletaWS();
		dadosUsuarioTransacao.setBairroEnderecoComprador(requestWrapper.getBairroEnderecoComprador());
		dadosUsuarioTransacao.setCepEnderecoComprador(requestWrapper.getCepEnderecoComprador());
		dadosUsuarioTransacao.setCidadeEnderecoComprador(requestWrapper.getCidadeEnderecoCompra());
		dadosUsuarioTransacao.setCodigoCliente(requestWrapper.getCodigoCliente());
		dadosUsuarioTransacao.setComplementoEnderecoComprador(requestWrapper.getComplementoEnderecoComprador());
		dadosUsuarioTransacao.setDataNascimentoComprador(requestWrapper.getDataNascimentoComprador());
		dadosUsuarioTransacao.setDddAdicionalComprador(requestWrapper.getDddAdicionalComprador());
		dadosUsuarioTransacao.setDddComprador(requestWrapper.getDddComprador());
		dadosUsuarioTransacao.setDdiAdicionalComprador(requestWrapper.getDdiAdicionalComprador());
		dadosUsuarioTransacao.setDdiComprador(requestWrapper.getDdiComprador());
		dadosUsuarioTransacao.setDocumento2Comprador(requestWrapper.getDocumento2Comprador());
		dadosUsuarioTransacao.setEstadoEnderecoComprador(requestWrapper.getEstadoEnderecoComprador());
		dadosUsuarioTransacao.setNomeComprador(requestWrapper.getNomeComprador());
		dadosUsuarioTransacao.setNumeroEnderecoComprador(requestWrapper.getNumeroEnderecoComprador());
		dadosUsuarioTransacao.setPaisComprador(requestWrapper.getPaisComprador());
		dadosUsuarioTransacao.setSexoComprador(requestWrapper.getSexoComprador());
		dadosUsuarioTransacao.setTelefoneAdicionalComprador(requestWrapper.getTelefoneAdicionalComprador());
		dadosUsuarioTransacao.setTipoCliente(requestWrapper.getTipoCliente());
		//dadosUsuarioTransacao.setCidadeEnderecoEntrega();
		//dadosUsuarioTransacao.setCepEnderecoEntrega();
		//dadosUsuarioTransacao.setCodigoTipoTelefoneAdicionalEntrega();
		//dadosUsuarioTransacao.setCodigoTipoTelefoneEntrega();
		//dadosUsuarioTransacao.setBairroEnderecoEntrega();
		//dadosUsuarioTransacao.setComplementoEnderecoEntrega();
		//dadosUsuarioTransacao.setDddAdicionalEntrega();
		//dadosUsuarioTransacao.setDdiAdicionalEntrega();
		//dadosUsuarioTransacao.setDddEntrega();
		//dadosUsuarioTransacao.setDdiEntrega();
		//dadosUsuarioTransacao.setEnderecoEntrega();
		//dadosUsuarioTransacao.setEstadoEnderecoEntrega();
		//dadosUsuarioTransacao.setNumeroEnderecoEntrega();
		//dadosUsuarioTransacao.setPaisEntrega();
		//dadosUsuarioTransacao.setTelefoneAdicionalEntrega();
		//dadosUsuarioTransacao.setTelefoneEntrega();

		TransacaoOneClickWSV2 transacaoOneClickWSV2 = factory.createTransacaoOneClickWSV2();
		transacaoOneClickWSV2.setCvv(requestWrapper.getCvv());
		transacaoOneClickWSV2.setNumeroTransacao(requestWrapper.getNumeroTransacao());
		transacaoOneClickWSV2.setCodigoEstabelecimento(codigoEstabelecimento);
		transacaoOneClickWSV2.setDadosUsuarioTransacao(dadosUsuarioTransacao);
		//transacaoOneClickWSV2.setIdioma(idioma);
		transacaoOneClickWSV2.setIP(requestWrapper.getIp());
		transacaoOneClickWSV2.setOrigemTransacao(requestWrapper.getOrigemTransacao());
		//transacaoOneClickWSV2.setParcelas(parcelas);
		//transacaoOneClickWS.setTaxaEmbarque(taxaEmbarque);
		transacaoOneClickWSV2.setToken(requestWrapper.getToken());
		transacaoOneClickWSV2.setUrlCampainha(requestWrapper.getCampainha());
		//transacaoOneClickWSV2.setUrlRedirecionamentoNaoPago(urlRedirecionamentoNaoPago);
		//transacaoOneClickWSV2.setUrlRedirecionamentoPago(urlRedirecionamentoPago);
		transacaoOneClickWSV2.setValor(requestWrapper.getValor());
		//transacaoOneClickWSV2.setValorDesconto(valorDesconto);
		//transacaoOneClickWSV2.setVencimentoBoleto(vencimentoBoleto);


		ItemPedidoTransacaoWS item = factory.createItemPedidoTransacaoWS();
		item.setCodigoCategoria(requestWrapper.getCodigoCategoria());
		item.setNomeCategoria(requestWrapper.getNomeCategoria());
		//item.setCodigoProduto(codigoProduto);
		item.setNomeProduto(requestWrapper.getNomeProduto());
		//item.setQuantidadeProduto(quantidadeProduto);
		item.setValorUnitarioProduto(requestWrapper.getValorUnitarioProduto());

		transacaoOneClickWSV2.getItensDoPedido().add(item);

		PagamentoOneClickV2 pagamentoOneClickV2 = factory.createPagamentoOneClickV2();
		pagamentoOneClickV2.setTransacao(transacaoOneClickWSV2);
		pagamentoOneClickV2.setSenha(password);
		pagamentoOneClickV2.setUsuario(user);


		JAXBElement<PagamentoOneClickV2> requestBody =
				factory.createPagamentoOneClickV2(pagamentoOneClickV2);

		JAXBElement<PagamentoOneClickV2Response> jaxbResponse =
				(JAXBElement<PagamentoOneClickV2Response>) webServiceTemplate.marshalSendAndReceive(requestBody);


		PagamentoOneClickV2Response oneclickResponse = jaxbResponse.getValue();


		log.info("Client reeceived result='{}'", oneclickResponse.getReturn());

 		return oneclickResponse.getReturn();// vo mudar o tipo de retorno do metodo. tbm fiz isso.. rsrsrsrsrsrs

	}

	public String updateCard(
			String dataValidadeCartao,
			String emailComprador,
			Long formaPagamento,
			String nomeTitularCartaoCredito,
			String numeroCartaoCredito, String token) {

		ObjectFactory factory = new ObjectFactory();

		DadosCadastroPagamentoOneClickWSV2 dados = factory.createDadosCadastroPagamentoOneClickWSV2();
		dados.setCodigoEstabelecimento(codigoEstabelecimento);
		dados.setDataValidadeCartao(dataValidadeCartao);
		dados.setEmailComprador(emailComprador);
		dados.setFormaPagamento(formaPagamento);
		dados.setNomeTitularCartaoCredito(nomeTitularCartaoCredito);
		dados.setNumeroCartaoCredito(numeroCartaoCredito);

		CadastraPagamentoOneClickV2 person = factory.createCadastraPagamentoOneClickV2();
		person.setDadosOneClick(dados);
		person.setSenha(password);
		person.setUsuario(user);

		log.info("Client sending person[user={},estabelecimento={}]", user, codigoEstabelecimento);

		JAXBElement<CadastraPagamentoOneClickV2> requestBody =
				factory.createCadastraPagamentoOneClickV2(person);

		JAXBElement<CadastraPagamentoOneClickV2Response> jaxbResponse =
				(JAXBElement<CadastraPagamentoOneClickV2Response>) webServiceTemplate.marshalSendAndReceive(requestBody);

		CadastraPagamentoOneClickV2Response oneclickResponse = jaxbResponse.getValue();

		log.info("Client received result='{}'", oneclickResponse.getReturn());

		return oneclickResponse.getReturn();
	}

	public DadosCadastroPagamentoOneClickWS readCard(
			String dataValidadeCartao,
			String emailComprador,
			Long formaPagamento,
			String nomeTitularCartaoCredito,
			String numeroCartaoCredito, String token) {

		ObjectFactory factory = new ObjectFactory();

		DadosCadastroPagamentoOneClickWSV2 dados = factory.createDadosCadastroPagamentoOneClickWSV2();
		dados.setCodigoEstabelecimento(codigoEstabelecimento);
		dados.setDataValidadeCartao(dataValidadeCartao);
		dados.setEmailComprador(emailComprador);
		dados.setFormaPagamento(formaPagamento);
		dados.setNomeTitularCartaoCredito(nomeTitularCartaoCredito);
		dados.setNumeroCartaoCredito(numeroCartaoCredito);

		ConsultaDadosOneClick person = factory.createConsultaDadosOneClick();
		person.setSenha(password);
		person.setUsuario(user);
		person.setToken(token);

		JAXBElement<ConsultaDadosOneClick> requestBody =
				factory.createConsultaDadosOneClick(person);

		JAXBElement<ConsultaDadosOneClickResponse> jaxbResponse =
				(JAXBElement<ConsultaDadosOneClickResponse>) webServiceTemplate.marshalSendAndReceive(requestBody);


		ConsultaDadosOneClickResponse oneclickResponse = jaxbResponse.getValue();

		log.info("Client received result='{}'", oneclickResponse.getReturn());

		return oneclickResponse.getReturn();
	}

	@Data
	public static class RequestWrapper {
		private Long numeroTransacao;
		private String ip;
		private String origemTransacao;
		private String token;
		private String campainha;
		private long valor;
		private String bairroEnderecoComprador;
		private String cepEnderecoComprador;
		private String cidadeEnderecoCompra;
		private String codigoCliente;
		private String complementoEnderecoComprador;
		private String dataNascimentoComprador;
		private String dddAdicionalComprador;
		private String dddComprador;
		private String ddiAdicionalComprador;
		private String ddiComprador;
		private String documento2Comprador;
		private long tipoCliente;
		private String codigoCategoria;
		private String nomeCategoria;
		private Long valorUnitarioProduto;
		private String nomeComprador;
		private String estadoEnderecoComprador;
		private String numeroEnderecoComprador;
		private String paisComprador;
		private String sexoComprador;
		private String telefoneAdicionalComprador;
		private String nomeProduto;
		private String cvv;
		private String enderecoComprador;
		private String emailComprador;
		private String telefoneComprador;
		private String urlRedirecionamentoNaoPago;
		private String urlRedirecionamentoPago;
	}
}
