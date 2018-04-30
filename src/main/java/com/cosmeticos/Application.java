package com.cosmeticos;

import com.cosmeticos.controller.ControllersInterceptor;
import com.cosmeticos.payment.Charger;
import feign.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class Application {

	public static final String ACTIVE_PROFILE_KEY = "spring.profiles.active";
	public static final String PROFILE_TESTING_INTEGRATION_VALUE = "integrationTest";
	public static final String PRINCIPAL_EMAIL_HEADER_KEY = "principal.email";
	public static final String FIREBASE_USER_TOKEN_HEADER_KEY = "firebaseUserToken";

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

	/**
	 * Habilita que a apicacao faça log do http: url, headers, body, response, etc..
	 * @return
	 */
	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(false);
		filter.setAfterMessagePrefix("REQUEST DATA : ");
		return filter;
	}

	/**
	 * Habilita o log http do feign
	 * @return
	 */
	@Bean
	public Logger.Level feignLoggerLevel(){
		return Logger.Level.FULL;
	}

}
