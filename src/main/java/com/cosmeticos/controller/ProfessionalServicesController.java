package com.cosmeticos.controller;

import com.cosmeticos.commons.ProfessionalServicesResponseBody;
import com.cosmeticos.commons.ProfessionalservicesRequestBody;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.service.ProfessionalServicesBeanServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by Vinicius on 22/06/2017.
 */
@Slf4j
@RestController
public class ProfessionalServicesController {
    @Autowired
    private ProfessionalServicesBeanServices service;

    @RequestMapping(path = "/professionalservices", method = RequestMethod.POST)
    public HttpEntity<ProfessionalServicesResponseBody> create(@Valid @RequestBody ProfessionalservicesRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                ProfessionalServices s = service.create(request);
                log.info("Service adicionado com sucesso:  [{}]", s);

                ProfessionalServicesResponseBody responseBody = new ProfessionalServicesResponseBody();

                responseBody.setDescription("Success");
                responseBody.getProfessionalServicesList().add(s);

                return ok().body(responseBody);


            }
        }catch(Exception e){

            log.error("Falha no cadastro: {}", e.getMessage(), e);
            ProfessionalServicesResponseBody responseBody = new ProfessionalServicesResponseBody();
            responseBody.setDescription(e.getMessage());

            return ResponseEntity.status(500).body(responseBody);
        }
    }

    /*@RequestMapping(path = "/professionalservices", method = RequestMethod.PUT)
    public HttpEntity<ProfessionalServicesResponseBody> update(@Valid @RequestBody ProfessionalservicesRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {

                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else if (request.getEntity().getProfessionalServicesPK() == null) {

                ProfessionalServicesResponseBody responseBody = new ProfessionalServicesResponseBody();
                responseBody.setDescription("Entity ID must to be set!");
                return badRequest().body(responseBody);

            } else {
                Optional<ProfessionalServices> optional = service.update(request);

                if (optional.isPresent()) {
                    ProfessionalServices updatedService = optional.get();

                    ProfessionalServicesResponseBody responseBody = new ProfessionalServicesResponseBody();
                    responseBody.getProfessionalServicesList().add(updatedService);

                    log.info("Service atualizado com sucesso:  [{}]", updatedService);
                    return ok().body(responseBody);

                } else {

                    log.error("Service nao encontrada: idService[{}]", request.getEntity().getProfessionalServicesPK());
                    return notFound().build();

                }
            }
        } catch (Exception e) {
            log.error("Falha na atualizacao: {}", e.getMessage(), e);
            ProfessionalServicesResponseBody responseBody = new ProfessionalServicesResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }*/

    @RequestMapping(path = "/professionalservices", method = RequestMethod.GET)
    public HttpEntity<ProfessionalServicesResponseBody> findAll() {

        try {
            List<ProfessionalServices> entitylist = service.findAll();

            ProfessionalServicesResponseBody responseBody = new ProfessionalServicesResponseBody();
            responseBody.setProfessionalServicesList(entitylist);
            responseBody.setDescription("All Services retrieved.");

            log.info("{} ProfessionalServices successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            ProfessionalServicesResponseBody responseBody = new ProfessionalServicesResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/professionalservices/{id}", method = RequestMethod.GET)
    public HttpEntity<ProfessionalServicesResponseBody> findById(@PathVariable String id) {

        try {
            Optional<ProfessionalServices> optional = service.find(Long.valueOf(id));

            if (optional.isPresent()) {

                ProfessionalServices foundService = optional.get();
                ProfessionalServicesResponseBody response = new ProfessionalServicesResponseBody();
                response.setDescription("Service succesfully retrieved");
                response.getProfessionalServicesList().add(foundService);

                log.info("Busca de Service com exito: [{}]", foundService);

                return ok().body(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", id);
                return notFound().build();
            }
        } catch (Exception e) {
            log.error("Falha na busca por ID: {}", e.getMessage(), e);
            ProfessionalServicesResponseBody responseBody = new ProfessionalServicesResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    private ProfessionalServicesResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        ProfessionalServicesResponseBody responseBody = new ProfessionalServicesResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }
}
