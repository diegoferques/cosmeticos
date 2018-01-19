package com.cosmeticos.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ResponseFacade;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Responsavel por circundar as execucoes de todos os endpoints REST de todos os Controllers da aplicacao.
 */
@Component
@Slf4j
public class ControllersInterceptor extends HandlerInterceptorAdapter {

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
}
