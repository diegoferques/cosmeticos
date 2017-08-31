package com.cosmeticos.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.config.annotation.EnableWs;

/**
 * 
 * @author magarrett.dias
 *
 */
@EnableWs
@Configuration
public class SoapClientConfig {

	  @Bean
	  Jaxb2Marshaller jaxb2Marshaller() {
	    Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
	    jaxb2Marshaller.setContextPath("br.com.ernet.superpay.webservices.pagamentos");

	    return jaxb2Marshaller;
	  }

	  @Bean
	  public WebServiceTemplate webServiceTemplate() {
	    WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
	    webServiceTemplate.setMarshaller(jaxb2Marshaller());
	    webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
	    webServiceTemplate.setDefaultUri("https://homologacao.superpay.com.br/checkout/servicosPagamentoOneClickWS.Services");

	    return webServiceTemplate;
	  }
}
