package com.cosmeticos.controller;

import com.cosmeticos.commons.CreditCardRequestBody;
import com.cosmeticos.commons.CreditCardResponseBody;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.service.CreditCardService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.*;

/**
 * Created by Vinicius on 07/07/2017.
 */
@AllArgsConstructor
@Slf4j
@RestController
public class CreditCardController {

    private CreditCardService service;

    @JsonView(ResponseJsonView.CreditCardFindAll.class)
    @RequestMapping(path = "/creditCard", method = RequestMethod.GET)
    public HttpEntity<CreditCardResponseBody> findAll(@ModelAttribute CreditCard ccAtributeAUX) {

        try {
            List<CreditCard> entitylist = service.findAllBy(ccAtributeAUX);
            CreditCardResponseBody responseBody = new CreditCardResponseBody();

            if (entitylist.isEmpty()) {
                log.warn("CreditCard nao encontrado para {}.", ccAtributeAUX.getUser().getEmail());

                return status(HttpStatus.NOT_FOUND).body(responseBody);
            } else {
                //CreditCardResponseBody responseBody = new CreditCardResponseBody();
                responseBody.setCreditCardList(entitylist);
                responseBody.setDescription("Success");

                log.info("{} CreditCard successfully retrieved.", entitylist.size());

                return ok().body(responseBody);
            }

        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            CreditCardResponseBody responseBody = new CreditCardResponseBody();
            responseBody.setDescription(e.getMessage());
            return status(500).body(responseBody);
        }

    }

    @RequestMapping(path = "/creditCard", method = RequestMethod.POST)
    public HttpEntity<CreditCardResponseBody> postCC(@Valid @RequestBody CreditCardRequestBody body,
                                                     BindingResult bindingResult) {

        CreditCard ccRequest = body.getEntity();
        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else {

                CreditCard cc = service.create(ccRequest);

                CreditCardResponseBody responseBody = new CreditCardResponseBody(cc);
                responseBody.setDescription("Success");

                log.info("CreditCard successfully created for {}.", cc.getUser().getEmail());

                return ok().body(responseBody);
            }
        }catch (IllegalStateException e)
        {
            log.error("Falha criando creditCard.", e);
            CreditCardResponseBody responseBody = new CreditCardResponseBody();
            responseBody.setDescription(e.getMessage());
            return status(BAD_REQUEST).body(responseBody);
        } catch (Exception e) {
            String email = null;
            if(ccRequest != null && ccRequest.getUser() != null)
            {
                email = ccRequest.getUser().getEmail();
            }

            log.error("Falha criando creditCard para {}", String.valueOf(email), e);
            CreditCardResponseBody responseBody = new CreditCardResponseBody();
            responseBody.setDescription(e.getMessage());
            return status(INTERNAL_SERVER_ERROR).body(responseBody);
        }

    }

    @RequestMapping(path = "/creditCard", method = RequestMethod.PUT)
    public HttpEntity<CreditCardResponseBody> putCC(@Valid @RequestBody CreditCardRequestBody body,
                                                     BindingResult bindingResult) {

        CreditCard ccRequest = body.getEntity();

        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());

                return badRequest().body(buildErrorResponse(bindingResult));

            } else {

                CreditCard cc = service.update(ccRequest);

                CreditCardResponseBody responseBody = new CreditCardResponseBody(cc);
                responseBody.setDescription("Success");

                log.info("CreditCard successfully updated.");

                return ok().body(responseBody);
            }
        } catch (IllegalStateException e) {
            log.error("Falha atualizando creditCard.", e);
            CreditCardResponseBody responseBody = new CreditCardResponseBody();
            responseBody.setDescription(e.getMessage());

            return status(BAD_REQUEST).body(responseBody);

        } catch (Exception e) {

            log.error("Falha atualizando creditCard");

            CreditCardResponseBody responseBody = new CreditCardResponseBody();
            responseBody.setDescription(e.getMessage());

            return status(INTERNAL_SERVER_ERROR).body(responseBody);
        }

    }

    private CreditCardResponseBody buildErrorResponse(BindingResult bindingResult) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                    .collect(Collectors.toList());

        CreditCardResponseBody responseBody = new CreditCardResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }

}
