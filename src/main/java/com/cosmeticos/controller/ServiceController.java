package com.cosmeticos.controller;

import com.cosmeticos.commons.ServiceRequestBody;
import com.cosmeticos.commons.ServiceResponseBody;
import com.cosmeticos.model.Service;
import com.cosmeticos.service.ServiceBeanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(path = "/service", method = RequestMethod.POST)
    public HttpEntity<ServiceResponseBody> create(@Valid @RequestBody ServiceRequestBody request, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Service s = service.create(request);
                log.info("Service adicionado com sucesso:  [{}]", s);

                ServiceResponseBody responseBody = new ServiceResponseBody();

                responseBody.setDescription("Success");
                responseBody.getServiceList().add(s);

                return ok().body(responseBody);


            }
        }catch(Exception e){

            log.error("Falha no cadastro: {}", e.getMessage(), e);
            ServiceResponseBody responseBody = new ServiceResponseBody();
            responseBody.setDescription(e.getMessage());

            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/service", method = RequestMethod.PUT)
    public HttpEntity<ServiceResponseBody> update(@Valid @RequestBody ServiceRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {

                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else if (request.getEntity().getIdService() == null) {

                ServiceResponseBody responseBody = new ServiceResponseBody();
                responseBody.setDescription("Entity ID must to be set!");
                return badRequest().body(responseBody);

            } else {
                Optional<Service> optional = service.update(request);

                if (optional.isPresent()) {
                    Service updatedService = optional.get();

                    ServiceResponseBody responseBody = new ServiceResponseBody();
                    responseBody.getServiceList().add(updatedService);

                    log.info("Service atualizado com sucesso:  [{}]", updatedService);
                    return ok().body(responseBody);

                } else {

                    log.error("Service nao encontrada: idService[{}]", request.getEntity().getIdService());
                    return notFound().build();

                }
            }
        } catch (Exception e) {
            log.error("Falha na atualizacao: {}", e.getMessage(), e);
            ServiceResponseBody responseBody = new ServiceResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/service", method = RequestMethod.GET)
    public HttpEntity<ServiceResponseBody> findAll() {

        try {
            List<Service> entitylist = service.findAll();

            ServiceResponseBody responseBody = new ServiceResponseBody();
            responseBody.setServiceList(entitylist);
            responseBody.setDescription("All Services retrieved.");

            log.info("{} Services successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            ServiceResponseBody responseBody = new ServiceResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/service/{id}", method = RequestMethod.GET)
    public HttpEntity<ServiceResponseBody> findById(@PathVariable String id) {

        try {
            Optional<Service> optional = service.find(Long.valueOf(id));

            if (optional.isPresent()) {

                Service foundService = optional.get();
                ServiceResponseBody response = new ServiceResponseBody();
                response.setDescription("Service succesfully retrieved");
                response.getServiceList().add(foundService);

                log.info("Busca de Service com exito: [{}]", foundService);

                return ok().body(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", id);
                return notFound().build();
            }
        } catch (Exception e) {
            log.error("Falha na busca por ID: {}", e.getMessage(), e);
            ServiceResponseBody responseBody = new ServiceResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
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
