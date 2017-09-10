package com.cosmeticos;

import com.cosmeticos.controller.ControllersInterceptor;
import com.cosmeticos.payment.Charger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableScheduling
@SpringBootApplication
public class Application {

	@Value("${paymnet.charger}")
	private String chargerBeanName;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 *
	 * @param restTemplateBuilder Como RestTemplateBuilder ja eh um @Service internamente pelo spring, inclui-lo como argumento
	 *                            de um metodo @Bean faz o parametro restTemplateBuilder sofrer autowire.
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder)
	{
		return restTemplateBuilder.build();
	}

	/**
	 *
	 * @param controllersInterceptor Interceptador de requests.
	 * @return
	 */
	@Bean
	public WebMvcConfigurerAdapter webMvcConfigurerAdapter(ControllersInterceptor controllersInterceptor){
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {

				// TODO: Diego, nao precisamos mais dos testes e tratamentos de chamada a http DELETE
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT");
			}

			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				// Somos obrigados a dizer ao spring quais interceptors usaremos. Podem haver varios.
				registry.addInterceptor(controllersInterceptor);
			}
		};
	}

	@Bean
	public Charger charger(ApplicationContext springApplicationContext)
	{
		Object configuredPaymentBean = springApplicationContext.getBean(this.chargerBeanName);

		Charger charger = (Charger) configuredPaymentBean;

		return charger;
	}

}
