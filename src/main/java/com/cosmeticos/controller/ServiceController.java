package com.cosmeticos.controller;

import com.cosmeticos.commons.ServiceRequestBody;
import com.cosmeticos.commons.ServiceResponseBody;
import com.cosmeticos.model.Service;
import com.cosmeticos.service.ServiceBeanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;

/**
 * Created by Vinicius on 31/05/2017.
 */
@Slf4j
@RestController
public class ServiceController {

    @Autowired
    private ServiceBeanService service;

    @RequestMapping(path = "/service", method = RequestMethod.POST, consumes = "application/json")
    public HttpEntity<ServiceResponseBody> create(@Valid @RequestBody ServiceRequestBody request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        }
        else
        {
            Service s = service.create(request);
            log.info("Service adicionado com sucesso:  [{}]", s);
            return ok().build();
        }
    }

    @RequestMapping(path = "/service", method = RequestMethod.PUT)
    public HttpEntity<ServiceResponseBody> update(@Valid @RequestBody ServiceRequestBody request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        }
        else
        {
            Service s = service.update(request);
            log.info("service atualizado com sucesso:  [{}]", s);
            return ok(new ServiceResponseBody(s));
        }
    }

    @RequestMapping(path = "/service/{id}", method = RequestMethod.GET)
    public HttpEntity<ServiceResponseBody> findById(@PathVariable String id) {

        Optional<Service> s = service.find(Long.valueOf(id));

        if(s.isPresent()) {
            log.info("Busca de Service com exito: [{}]", s.get());
            ServiceResponseBody response = new ServiceResponseBody(s.get());

            return ok().body(response);
        }
        else
        {
            log.error("Nenhum registro encontrado para o id: {}", id);
            return notFound().build();
        }
    }

    @RequestMapping(path = "/service", method = RequestMethod.GET)
    public HttpEntity<ServiceResponseBody> findLastest10() {

        List<Service> entitylist = service.findAll();

        ServiceResponseBody responseBody = new ServiceResponseBody();
        responseBody.setServiceList(entitylist);
        responseBody.setDescription("TOP 10 successfully retrieved.");

        log.info("{} service successfully retrieved.", entitylist.size());

        return ok().body(responseBody);
    }

    private ServiceResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        ServiceResponseBody responseBody = new ServiceResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }
}
