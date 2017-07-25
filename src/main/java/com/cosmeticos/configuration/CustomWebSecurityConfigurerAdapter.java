package com.cosmeticos.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Created by matto on 19/07/2017.
 */
/*
    EXEMPLOS DE AUTENTICACAO ENCONTRADOS NAS PESQUISAS:

    Spring Boot and OAuth2
    https://spring.io/guides/tutorials/spring-boot-oauth2/

    Secure Spring REST API using OAuth2
    http://websystique.com/spring-security/secure-spring-rest-api-using-oauth2/

    Configurando em yaml/properties
    https://stackoverflow.com/questions/36972549/spring-boot-only-secure-actuator-endpoints

    How to secure REST API with Spring Boot and Spring Security?
    https://stackoverflow.com/questions/32548372/how-to-secure-rest-api-with-spring-boot-and-spring-security

    Spring Security using @Secured Annotation
    http://www.concretepage.com/spring/spring-security/spring-security-using-secured-annotation

    Spring Security with Maven
    http://www.baeldung.com/spring-security-with-maven

    Spring Security Basic Authentication
    http://www.baeldung.com/spring-security-basic-authentication
 */
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter extends org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter {
    @Autowired
    private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("123")
                .authorities("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        http.authorizeRequests()
                .antMatchers("/**").permitAll();
        */

        http.authorizeRequests()
               //.antMatchers("/**").permitAll()
                .antMatchers("/secure/**").authenticated()
                .and()
                .csrf().disable() //Referencia: https://github.com/spring-projects/spring-data-examples/tree/master/rest/security
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);


        /*
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .antMatchers("/secure/**").hasRole("USER")
                .and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);
        */

        /*
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);
        */

        //TRECHO ABAIXO COMENTADO PARA SERVIR DE EXEMPLO COM LOGIN E LOGOUT
        /*
        //https://spring.io/guides/gs/securing-web/
        http.authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();

        //http://www.baeldung.com/spring-security-login#web_xml
        http.authorizeRequests()
                  .antMatchers("/login*").anonymous()
                  .anyRequest().authenticated()
                  .and()
              .formLogin()
                  .loginPage("/login.html")
                  .defaultSuccessUrl("/homepage.html")
                  .failureUrl("/login.html?error=true")
                  .and()
              .logout().logoutSuccessUrl("/login.html");
        */

        http.addFilterAfter(new CustomFilter(),
                BasicAuthenticationFilter.class);
    }
}
