package com.cosmeticos.payment;

import com.cosmeticos.payment.superpay.ws.oneclick.*;
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

		CadastraPagamentoOneClickV2 person = factory.createCadastraPagamentoOneClickV2();
		person.setDadosOneClick(dados);
		person.setSenha(password);
		person.setUsuario(user);

		log.info("Client sending person[user={},estabelecimento={}]", user, codigoEstabelecimento);

		JAXBElement<CadastraPagamentoOneClickV2> jaxbCadastraPagamento =
				factory.createCadastraPagamentoOneClickV2(person);

		JAXBElement<CadastraPagamentoOneClickV2Response> jaxbResponse =
				(JAXBElement<CadastraPagamentoOneClickV2Response>) webServiceTemplate.marshalSendAndReceive(jaxbCadastraPagamento);

		CadastraPagamentoOneClickV2Response oneclickResponse = jaxbResponse.getValue();
		
		log.info("Client received result='{}'", oneclickResponse.getReturn());
		
		return oneclickResponse.getReturn();
	}

	public ResultadoPagamentoWS pay(Long numeroTransacao,
									int idioma, String ip,
									String origemTransacao,
									int parcelas,
									String token,
									String campainha,
									String urlRedirecionamentoNaoPago,
									String urlRedirecionamentoPago,
									long valor, long valorDesconto,
									String bairroEnderecoComprador,
									String cepEnderecoComprador,
									String cidadeEnderecoCompra,
									String codigoCliente,
									String complementoEnderecoComprador,
									String dataNascimentoComprador,
									String dddAdicionalComprador,
									String dddComprador,
									String ddiAdicionalComprador,
									String ddiComprador,
									String documento2Comprador,
									String documentoComprador,
									String emailComprador,
									String enderecoComprador,
									long tipoCliente,
									long codigoTipoTelefoneAdicionalComprador,
									String codigoCategoria,
									String nomeCategoria,
									String codigoProduto,
									String nomeProduto,
									int quantidadeProduto,
									Long valorUnitarioProduto,
									String nomeComprador,
									String estadoEnderecoComprador,
									String numeroEnderecoComprador,
									String paisComprador,
									String sexoComprador,
									String telefoneAdicionalComprador, String cvv){

		ObjectFactory factory = new ObjectFactory();

		DadosUsuarioTransacaoCompletaWS dadosUsuarioTransacao = factory.createDadosUsuarioTransacaoCompletaWS();
		dadosUsuarioTransacao.setBairroEnderecoComprador(bairroEnderecoComprador);
		dadosUsuarioTransacao.setCepEnderecoComprador(cepEnderecoComprador);
		dadosUsuarioTransacao.setCidadeEnderecoComprador(cidadeEnderecoCompra);
		dadosUsuarioTransacao.setCodigoCliente(codigoCliente);
		dadosUsuarioTransacao.setComplementoEnderecoComprador(complementoEnderecoComprador);
		dadosUsuarioTransacao.setDataNascimentoComprador(dataNascimentoComprador);
		dadosUsuarioTransacao.setDddAdicionalComprador(dddAdicionalComprador);
		dadosUsuarioTransacao.setDddComprador(dddComprador);
		dadosUsuarioTransacao.setDdiAdicionalComprador(ddiAdicionalComprador);
		dadosUsuarioTransacao.setDdiComprador(ddiComprador);
		dadosUsuarioTransacao.setDocumento2Comprador(documento2Comprador);
		dadosUsuarioTransacao.setDocumentoComprador(documentoComprador);
		dadosUsuarioTransacao.setEmailComprador(emailComprador);
		dadosUsuarioTransacao.setEnderecoComprador(enderecoComprador);
		dadosUsuarioTransacao.setEstadoEnderecoComprador(estadoEnderecoComprador);
		dadosUsuarioTransacao.setNomeComprador(nomeComprador);
		dadosUsuarioTransacao.setNumeroEnderecoComprador(numeroEnderecoComprador);
		dadosUsuarioTransacao.setPaisComprador(paisComprador);
		dadosUsuarioTransacao.setSexoComprador(sexoComprador);
		dadosUsuarioTransacao.setTelefoneAdicionalComprador(telefoneAdicionalComprador);
		dadosUsuarioTransacao.setTipoCliente(tipoCliente);
		dadosUsuarioTransacao.setCodigoTipoTelefoneAdicionalComprador(codigoTipoTelefoneAdicionalComprador);
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
		transacaoOneClickWSV2.setCvv(cvv);
		transacaoOneClickWSV2.setNumeroTransacao(numeroTransacao);
		transacaoOneClickWSV2.setCodigoEstabelecimento(codigoEstabelecimento);
		transacaoOneClickWSV2.setDadosUsuarioTransacao(dadosUsuarioTransacao);
		transacaoOneClickWSV2.setIdioma(idioma);
		transacaoOneClickWSV2.setIP(ip);
		transacaoOneClickWSV2.setOrigemTransacao(origemTransacao);
		transacaoOneClickWSV2.setParcelas(parcelas);
		//transacaoOneClickWS.setTaxaEmbarque(taxaEmbarque);
		transacaoOneClickWSV2.setToken(token);
		transacaoOneClickWSV2.setUrlCampainha(campainha);
		transacaoOneClickWSV2.setUrlRedirecionamentoNaoPago(urlRedirecionamentoNaoPago);
		transacaoOneClickWSV2.setUrlRedirecionamentoPago(urlRedirecionamentoPago);
		transacaoOneClickWSV2.setValor(valor);
		transacaoOneClickWSV2.setValorDesconto(valorDesconto);
		//transacaoOneClickWSV2.setVencimentoBoleto(vencimentoBoleto);


		ItemPedidoTransacaoWS item = factory.createItemPedidoTransacaoWS();
		item.setCodigoCategoria(codigoCategoria);
		item.setNomeCategoria(nomeCategoria);
		item.setCodigoProduto(codigoProduto);
		item.setNomeProduto(nomeProduto);
		item.setQuantidadeProduto(quantidadeProduto);
		item.setValorUnitarioProduto(valorUnitarioProduto);

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



}
