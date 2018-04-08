package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Responsavel por circundar as execucoes de todos os endpoints REST de todos os Controllers da aplicacao.
 */
@Component
@Slf4j
public class ControllersInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Quando um cliente chama um endpoint, este metodo eh executado antes do metodo do Controller anotado com @RequestMapping
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		log.debug("{}.{}", getClass().getSimpleName(), "preHandle");

		// TODO: Vinicius, catar dentro do parametro request o path da url que foi chamada (soh o que vem depois do localhost:8080
		MDC.put("urlPath", request.getRequestURL().toString());

		applyPrincipalEmail(request);

		//applyFirebaseUserToken(request); MUITO CUSTOSO

		return super.preHandle(request, response, handler);
	}

	/**
	 * Quando um cliente chama um endpoint, este metodo eh executado apos a execucao do metodo do Controller anotado
	 * com @RequestMapping
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

		if(log.isDebugEnabled())
		{
			log.debug("{}.{} . RESPONSE DATA: {}", getClass().getSimpleName(), "postHandle", "");
		}
		MDC.clear();
	}


	private void applyPrincipalEmail(HttpServletRequest request) {
		Object principalEmailObject = request.getHeader(Application.PRINCIPAL_EMAIL_HEADER_KEY);
		String principalEmailHeaderValue = String.valueOf(principalEmailObject);
		MDC.put(Application.PRINCIPAL_EMAIL_HEADER_KEY, principalEmailHeaderValue);

		if(principalEmailObject != null) {
			HttpSession session = request.getSession();
			session.setAttribute(Application.PRINCIPAL_EMAIL_HEADER_KEY, principalEmailHeaderValue);
		}
	}

}
