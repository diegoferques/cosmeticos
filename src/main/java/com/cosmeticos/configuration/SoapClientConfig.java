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
	    jaxb2Marshaller.setContextPath("com.cosmeticos.payment.superpay.ws");

	    return jaxb2Marshaller;
	  }

	  @Bean
	  public WebServiceTemplate webServiceTemplateOneClick() {
	    WebServiceTemplate webServiceTemplateOneClick = new WebServiceTemplate();
	    webServiceTemplateOneClick.setMarshaller(jaxb2Marshaller());
	    webServiceTemplateOneClick.setUnmarshaller(jaxb2Marshaller());
	    webServiceTemplateOneClick.setDefaultUri("https://homologacao.superpay.com.br/checkout/servicosPagamentoOneClickWS.Services");

	    return webServiceTemplateOneClick;
	  }

	@Bean
	public WebServiceTemplate webServiceTemplateCompleto() {
		WebServiceTemplate webServiceTemplateCompleto = new WebServiceTemplate();
		webServiceTemplateCompleto.setMarshaller(jaxb2Marshaller());
		webServiceTemplateCompleto.setUnmarshaller(jaxb2Marshaller());
		webServiceTemplateCompleto.setDefaultUri("https://homologacao.superpay.com.br/checkout/servicosPagamentoCompletoWS.Services?wsdl");

		return webServiceTemplateCompleto;
	}
}
