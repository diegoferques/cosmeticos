package com.cosmeticos.payment;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.commons.SuperpayFormaPagamento;
import com.cosmeticos.payment.superpay.ws.oneclick.*;
import com.cosmeticos.validation.OrderValidationException;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.soap.SOAPFaultException;
import java.lang.Exception;
import java.net.UnknownHostException;

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
			SuperpayFormaPagamento formaPagamento,
			String nomeTitularCartaoCredito,
			String numeroCartaoCredito) {
		
		ObjectFactory factory = new ObjectFactory();

		DadosCadastroPagamentoOneClickWSV2 dados = factory.createDadosCadastroPagamentoOneClickWSV2();
		dados.setCodigoEstabelecimento(codigoEstabelecimento);
		dados.setDataValidadeCartao(dataValidadeCartao);
		dados.setEmailComprador(emailComprador);
		dados.setFormaPagamento(formaPagamento.getCodigoFormaPagamento().longValue());
		dados.setNomeTitularCartaoCredito(nomeTitularCartaoCredito);
		dados.setNumeroCartaoCredito(numeroCartaoCredito);

		CadastraPagamentoOneClickV2 pagamentoOneClickV2 = factory.createCadastraPagamentoOneClickV2();
		pagamentoOneClickV2.setDadosOneClick(dados);
		pagamentoOneClickV2.setSenha(password);
		pagamentoOneClickV2.setUsuario(user);

		JAXBElement<CadastraPagamentoOneClickV2> jaxbCadastraPagamento =
				factory.createCadastraPagamentoOneClickV2(pagamentoOneClickV2);

		JAXBElement<CadastraPagamentoOneClickV2Response> jaxbResponse =
				null;
		try {
			jaxbResponse = (JAXBElement<CadastraPagamentoOneClickV2Response>) webServiceTemplate.marshalSendAndReceive(jaxbCadastraPagamento);
		} catch (WebServiceIOException e) {
			if(e.getCause() instanceof UnknownHostException)
			{
				// Sabemos que essa falha eh por causa de falha de conexao do nosso servidor (sem acesso internet)
				throw new OrderValidationException(ResponseCode.INTERNAL_ERROR, e);
			}
			else{
				throw e;
			}
		}

		CadastraPagamentoOneClickV2Response oneclickResponse = jaxbResponse.getValue();
		
		MDC.put("superpayStatusStransacao", oneclickResponse.getReturn());
		
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
				null;

		try {
			/*
			Se nao usarmos este token 15067741784287d403b60-58e7-4ff3-ac10-e86cf9917295 em homolog, fica dando este erro:
			SoapFaultClientException: id to load is required for loading

			Acho que esse erro ocorre quando o addcard cria token no superpay sem passar dados de cartao corretos.
			 */
			jaxbResponse = (JAXBElement<PagamentoOneClickV2Response>) webServiceTemplate.marshalSendAndReceive(requestBody);
		} catch (SOAPFaultException e) {
			if("id to load is required for loading".equals(e.getMessage()))
			{
				throw new IllegalStateException("Ocorreu um erro na reserva de pagamento. Este erro eh conhecido por " +
						"ocorrer quando usamos um token de cartao que foi cadastrado (addCard) com dados incorretos. " +
						"A superpay nao tratou os dados de cartao passados (formaPagamento=0, por exemplo) e entregou " +
						"um token que nao eh valido. Este erro foi identificado, ate o momento da escrita deste resporte, " +
						"apenas no ambiente de homologação.", e);
			}
			else
			{
				throw e;
			}
		}


		PagamentoOneClickV2Response oneclickResponse = jaxbResponse.getValue();


		log.info("Client reeceived result='{}'", oneclickResponse.getReturn());

 		return oneclickResponse.getReturn();// vo mudar o tipo de retorno do metodo. tbm fiz isso.. rsrsrsrsrsrs

	}

	public String updateCard(
			String dataValidadeCartao,
			String emailComprador,
			SuperpayFormaPagamento formaPagamento,
			String nomeTitularCartaoCredito,
			String numeroCartaoCredito, String token) {

		ObjectFactory factory = new ObjectFactory();

		DadosCadastroPagamentoOneClickWSV2 dados = factory.createDadosCadastroPagamentoOneClickWSV2();
		dados.setCodigoEstabelecimento(codigoEstabelecimento);
		dados.setDataValidadeCartao(dataValidadeCartao);
		dados.setEmailComprador(emailComprador);
		dados.setFormaPagamento(formaPagamento.getCodigoFormaPagamento().longValue());
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
			SuperpayFormaPagamento formaPagamento,
			String nomeTitularCartaoCredito,
			String numeroCartaoCredito, String token) {

		ObjectFactory factory = new ObjectFactory();

		DadosCadastroPagamentoOneClickWSV2 dados = factory.createDadosCadastroPagamentoOneClickWSV2();
		dados.setCodigoEstabelecimento(codigoEstabelecimento);
		dados.setDataValidadeCartao(dataValidadeCartao);
		dados.setEmailComprador(emailComprador);
		dados.setFormaPagamento(formaPagamento.getCodigoFormaPagamento().longValue());
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
