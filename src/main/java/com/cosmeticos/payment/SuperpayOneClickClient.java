package com.cosmeticos.payment;

import com.cosmeticos.payment.superpay.ws.CadastraPagamentoOneClickV2;
import com.cosmeticos.payment.superpay.ws.CadastraPagamentoOneClickV2Response;
import com.cosmeticos.payment.superpay.ws.DadosCadastroPagamentoOneClickWSV2;
import com.cosmeticos.payment.superpay.ws.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.JAXBElement;

@Component
@lombok.extern.slf4j.Slf4j
public class SuperpayOneClickClient {

	@Autowired
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

		JAXBElement<CadastraPagamentoOneClickV2Response> jaxbResponse =
				(JAXBElement<CadastraPagamentoOneClickV2Response>) webServiceTemplate.marshalSendAndReceive(person);

		CadastraPagamentoOneClickV2Response oneclickResponse = jaxbResponse.getValue();
		
		log.info("Client received result='{}'", oneclickResponse.getReturn());
		
		return oneclickResponse.getReturn();
	}




}
