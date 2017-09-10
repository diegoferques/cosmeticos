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
	  Jaxb2Marshaller jaxb2MarshallerOneclick() {
	    Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
	    jaxb2Marshaller.setContextPath(
	    		"com.cosmeticos.payment.superpay.ws.oneclick"
		);

	    return jaxb2Marshaller;
	  }

	  @Bean
	  Jaxb2Marshaller jaxb2MarshallerCompleto() {
	    Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
	    jaxb2Marshaller.setContextPath(
				"com.cosmeticos.payment.superpay.ws.completo"
		);

	    return jaxb2Marshaller;
	  }

	  @Bean
	  public WebServiceTemplate webServiceTemplateOneClick() {
	    WebServiceTemplate webServiceTemplateOneClick = new WebServiceTemplate();
	    webServiceTemplateOneClick.setMarshaller(jaxb2MarshallerOneclick());
	    webServiceTemplateOneClick.setUnmarshaller(jaxb2MarshallerOneclick());
	    webServiceTemplateOneClick.setDefaultUri("https://homologacao.superpay.com.br/checkout/servicosPagamentoOneClickWS.Services");

	    return webServiceTemplateOneClick;
	  }

	@Bean
	public WebServiceTemplate webServiceTemplateCompleto() {
		WebServiceTemplate webServiceTemplateCompleto = new WebServiceTemplate();
		webServiceTemplateCompleto.setMarshaller(jaxb2MarshallerCompleto());
		webServiceTemplateCompleto.setUnmarshaller(jaxb2MarshallerCompleto());
		webServiceTemplateCompleto.setDefaultUri("https://homologacao.superpay.com.br/checkout/servicosPagamentoCompletoWS.Services?wsdl");

		return webServiceTemplateCompleto;
	}
}
