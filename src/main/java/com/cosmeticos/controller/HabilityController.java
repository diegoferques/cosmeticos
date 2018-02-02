package com.cosmeticos.controller;

import com.cosmeticos.commons.HabilityRequestBody;
import com.cosmeticos.commons.HabilityResponseBody;
import com.cosmeticos.model.Hability;
import com.cosmeticos.service.HabilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RestController
public class HabilityController {

    private static final String ENDPOINT_PATH = "/habilities";

    @Autowired
    private HabilityService service;

    @RequestMapping(path = ENDPOINT_PATH, method = RequestMethod.POST)
    public HttpEntity<HabilityResponseBody> create(@Valid @RequestBody HabilityRequestBody request,
                                                   BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Hability hability = service.create(request.getHability());
                log.info("Hability adicionado com sucesso:  [{}]", hability);
                //return ok().build();
                return ok(new HabilityResponseBody(hability));
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            HabilityResponseBody b = new HabilityResponseBody();
            b.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(b);
        }
    }

    @RequestMapping(path = ENDPOINT_PATH, method = RequestMethod.PUT)
    public HttpEntity<HabilityResponseBody> update(@Valid @RequestBody HabilityRequestBody request, BindingResult bindingResult) {

        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            }
            else if(request.getHability() == null || request.getHability().getId() == null)
            {
                log.error("Entidade a ser alterada esta nula.");
                return badRequest().body(buildErrorResponse(bindingResult));
            }
            else {
                Optional<Hability> optional = service.update(request);

                if (optional.isPresent()) {
                    Hability Hability = optional.get();

                    HabilityResponseBody responseBody = new HabilityResponseBody(Hability);
                    log.info("Hability atualizado com sucesso:  [{}] responseJson[{}]",
                            Hability,
                            new ObjectMapper().writeValueAsString(responseBody));
                    return ok(responseBody);
                }
                else
                {
                    log.info("Hability inexistente:  [{}]",
                            request.getHability());
                    return ResponseEntity.notFound().build();
                }
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            HabilityResponseBody response = new HabilityResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na atualização do Hability: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @RequestMapping(path = ENDPOINT_PATH + "/{idHability}", method = RequestMethod.GET)
    public HttpEntity<HabilityResponseBody> findById(@PathVariable String idHability) {

        try {

            Optional<Hability> Hability = service.find(Long.valueOf(idHability));

            if (Hability.isPresent()) {
                log.info("Busca de Hability com exito: [{}]", Hability.get());
                HabilityResponseBody response = new HabilityResponseBody(Hability.get());

                //return ok().body(response);
                return ok(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", idHability);
                return notFound().build();
            }

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            HabilityResponseBody response = new HabilityResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição do Hability: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping(path = ENDPOINT_PATH + "/{idHability}", method = RequestMethod.DELETE)
    public HttpEntity<HabilityResponseBody> delete(@PathVariable String idHability) {

        String errorCode = String.valueOf(System.nanoTime());

        HabilityResponseBody response = new HabilityResponseBody();
        response.setDescription("Ação não permitida: Atualize o status do Hability para desativado: " + errorCode);

        log.warn("Ação não permitida para delete o Hability: {}. Atualize o status do Hability para desativado. - {}", idHability, errorCode);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @RequestMapping(path =  ENDPOINT_PATH, method = RequestMethod.GET)
    public HttpEntity<HabilityResponseBody> findAll() {

        try {
            List<Hability> entitylist = service.listAll();

            HabilityResponseBody responseBody = new HabilityResponseBody();
            responseBody.setHabilityList(entitylist);
            responseBody.setDescription(entitylist.size() + " itens successfully retrieved.");

            log.info("{} Habilitys successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            HabilityResponseBody response = new HabilityResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição da Lista de Hability: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private HabilityResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        HabilityResponseBody responseBody = new HabilityResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }


}
