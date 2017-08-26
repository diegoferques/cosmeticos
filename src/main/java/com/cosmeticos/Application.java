package com.cosmeticos;

import com.cosmeticos.controller.ControllersInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableScheduling
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
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
}
