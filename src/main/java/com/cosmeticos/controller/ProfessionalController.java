package com.cosmeticos.controller;

import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.model.Professional;
import com.cosmeticos.service.ProfessionalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RestController
public class ProfessionalController {

    @Autowired
    private ProfessionalService service;

    @RequestMapping(path = "/professionals", method = RequestMethod.POST)
    public HttpEntity<ProfessionalResponseBody> create(@Valid @RequestBody ProfessionalRequestBody request,
                                                   BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Professional professional = service.create(request);

                ProfessionalResponseBody responseBody = new ProfessionalResponseBody(professional);
                responseBody.setDescription("sucess");

                log.info("Professional adicionado com sucesso:  [{}]", professional);

                return ok(responseBody);
            }
        } catch (ConstraintViolationException e) {
            /**
             * Alguns dados de requisicao invalidos podem nao ser processados no request devido ao nivel de aninhamento
             * dos objetos. Entretanto, esses erros de validacao podem ser pegos em outras partes do codigo e acabam lancando
             * ConstraintViolationException. Por isso mantemos este catch.
             */
            log.error("Erro no insert: {}", e.getMessage(), e.getConstraintViolations());

            return badRequest().body(buildErrorResponse(e));

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            ProfessionalResponseBody b = new ProfessionalResponseBody();
            b.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(b);
        }
    }

    @RequestMapping(path = "/professionals", method = RequestMethod.PUT)
    public HttpEntity<ProfessionalResponseBody> update(@Valid @RequestBody ProfessionalRequestBody request, BindingResult bindingResult) {

        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            }
            else if(request.getProfessional() == null || request.getProfessional().getIdProfessional() == null)
            {
                log.error("Entidade a ser alterada esta nula.");
                return badRequest().body(buildErrorResponse(bindingResult));
            }
            else {
                Optional<Professional> optional = service.update(request);

                if (optional.isPresent()) {
                    Professional Professional = optional.get();

                    ProfessionalResponseBody responseBody = new ProfessionalResponseBody(Professional);
                    log.info("Professional atualizado com sucesso:  [{}] responseJson[{}]",
                            Professional,
                            new ObjectMapper().writeValueAsString(responseBody));
                    return ok(responseBody);
                }
                else
                {
                    log.info("Professional inexistente:  [{}]",
                            request.getProfessional());
                    return ResponseEntity.notFound().build();
                }
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            ProfessionalResponseBody response = new ProfessionalResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na atualização do Professional: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @RequestMapping(path = "/professionals/{idProfessional}", method = RequestMethod.GET)
    public HttpEntity<ProfessionalResponseBody> findById(
            @PathVariable String idProfessional) {

        try {

            Optional<Professional> professional = service.find(Long.valueOf(idProfessional));

            if (professional.isPresent()) {
                log.info("Busca de Professional com exito: [{}]", professional.get());
                ProfessionalResponseBody response = new ProfessionalResponseBody(professional.get());

                //return ok().body(response);
                return ok(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", idProfessional);
                return notFound().build();
            }

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            ProfessionalResponseBody response = new ProfessionalResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição do Professional: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping(path = "/professionals/{idProfessional}", method = RequestMethod.DELETE)
    public HttpEntity<ProfessionalResponseBody> delete(@PathVariable String idProfessional) {


        String errorCode = String.valueOf(System.nanoTime());

        ProfessionalResponseBody response = new ProfessionalResponseBody();
        response.setDescription("Ação não permitida: Atualize o status do Professional para desativado: " + errorCode);

        log.warn("Ação não permitida para deletar o Professional: {}. Atualize o status do Professional para desativado. - {}", idProfessional, errorCode);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

    }

    /**
     *
     * @param professionalProbe Usado pelo Spring para fazer o bind das queries string nos
     *                          atributos da classe.
     * @return
     */
    @RequestMapping(path = "/professionals", method = RequestMethod.GET)
    public HttpEntity<ProfessionalResponseBody> findAll(@ModelAttribute Professional professionalProbe) {

        try {
            List<Professional> entitylist = service.findAllBy(professionalProbe);

            ProfessionalResponseBody responseBody = new ProfessionalResponseBody();
            responseBody.setProfessionalList(entitylist);
            responseBody.setDescription("TOP 10 successfully retrieved.");

            log.info("{} Professionals successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            ProfessionalResponseBody response = new ProfessionalResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição da Lista de Professional: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private ProfessionalResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        ProfessionalResponseBody responseBody = new ProfessionalResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }

    private ProfessionalResponseBody buildErrorResponse(ConstraintViolationException cve) {
        List<String> errors = cve.getConstraintViolations()
                .stream()
                .map(v -> v.getMessageTemplate())
                .collect(Collectors.toList());

        ProfessionalResponseBody responseBody = new ProfessionalResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }


}
