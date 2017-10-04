package com.cosmeticos.controller;

import com.cosmeticos.commons.ExceptionRequestBody;
import com.cosmeticos.commons.ExceptionResponseBody;
import com.cosmeticos.model.Exception;
import com.cosmeticos.repository.ExceptionRepository;
import com.cosmeticos.service.ExceptionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpEntity;


import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;

/**
 * Created by Vinicius on 02/10/2017.
 */
@Slf4j
@RestController
public class ExceptionController {

    @Autowired
    private ExceptionRepository exceptionRepository;

    @Autowired
    private ExceptionService exceptionService;

    @RequestMapping(path = "/exceptions", method = RequestMethod.POST)
    public HttpEntity<ExceptionResponseBody> create(@Valid @RequestBody ExceptionRequestBody request, BindingResult bindingResult) {


        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {


                MDC.put("stackTrace", String.valueOf(request.getEntity().getStackTrace()));
                MDC.put("email", String.valueOf(request.getEntity().getEmail()));
                MDC.put("deviceModel", String.valueOf(request.getEntity().getDeviceModel()));
                MDC.put("osVersion", String.valueOf(request.getEntity().getOsVersion()));

                Exception exception = exceptionService.create(request);

                log.info("Service adicionado com sucesso:  [{}]", exception);

                return ok(new ExceptionResponseBody(exception));

            }
        }catch(java.lang.Exception e){

            log.error("Falha na exception: {}", e.getMessage(), e);
            ExceptionResponseBody responseBody = new ExceptionResponseBody();
            responseBody.setDescription(e.getMessage());

            return ResponseEntity.status(500).body(responseBody);
        }
    }

    private ExceptionResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        ExceptionResponseBody responseBody = new ExceptionResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }

}
