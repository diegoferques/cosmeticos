package com.cosmeticos.controller;

import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalCategory;
import com.cosmeticos.model.User;
import com.cosmeticos.service.ProfessionalService;
import com.cosmeticos.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
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
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RestController
public class ProfessionalController {

    @Autowired
    private ProfessionalService service;

    @Autowired
    private UserService userService;

    @JsonView(ResponseJsonView.ProfessionalCreate.class)
    @RequestMapping(path = "/professionals", method = RequestMethod.POST)
    public HttpEntity<ProfessionalResponseBody> create(@Valid @RequestBody ProfessionalRequestBody request,
                                                       BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            }
            // Tem que haver um servico associado ao profissional.
            else if (!hasService(request)) {
                log.error("BAD REQUEST: Nao foi identificado um Servico associado ao profissional ou o Service.ID " +
                        "nao esta configurado");
                return badRequest().body(buildErrorResponse(bindingResult));

            } else {

                String userEmail = "";
                if(request.getProfessional().getUser() != null) {
                    userEmail = request.getProfessional().getUser().getEmail();
                }

                if(userService.verifyEmailExistsforCreate(userEmail)) {

                    ProfessionalResponseBody responseBody = new ProfessionalResponseBody();
                    responseBody.setDescription("E-mail já existente.");
                    log.error("Nao e permitido criar profissional com um email já existente.");

                    return badRequest().body(responseBody);

                } else {
                    Professional professional = service.create(request);

                    ProfessionalResponseBody responseBody = new ProfessionalResponseBody(professional);
                    responseBody.setDescription("sucess");

                    log.info("Professional adicionado com sucesso:  [{}]", professional);

                    return ok(responseBody);
                }
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

    @JsonView(ResponseJsonView.ProfessionalUpdate.class)
    @RequestMapping(path = "/professionals", method = RequestMethod.PUT)
    public HttpEntity<ProfessionalResponseBody> update(@RequestBody ProfessionalRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else if (request.getProfessional() == null || request.getProfessional().getIdProfessional() == null) {
                log.error("Entidade a ser alterada esta nula.");
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
            	
            	Optional<Professional> optional = service.find(request.getProfessional().getIdProfessional());

				if (optional.isPresent()) {

					Professional persistentProfessional = optional.get();
					User user =  request.getProfessional().getUser();

					String emailInDatabase = persistentProfessional.getUser().getEmail();
					String emailFromRequest = user == null ? emailInDatabase : user.getEmail();

					if (emailFromRequest != null || emailInDatabase.equals(emailFromRequest)) {

						optional = service.update(request.getProfessional());

						Professional professional = optional.get();

						ProfessionalResponseBody responseBody = new ProfessionalResponseBody(professional);
						log.info("Professional atualizado com sucesso: id[{}]", professional.getIdProfessional());
						return ok(responseBody);
					} else {
						ProfessionalResponseBody responseBody = new ProfessionalResponseBody();
						responseBody.setDescription("E-mail já existente.");
						log.error("Nao e permitido atualizar profissional para um email já existente.");

						return badRequest().body(responseBody);
					}

                } else {
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

    @JsonView(ResponseJsonView.ProfessionalUpdate.class)
    @RequestMapping(path = "/professionals/{bossId}/employees/{employeeId}", method = RequestMethod.DELETE)
    public HttpEntity<ProfessionalResponseBody> delete(
            @PathVariable("bossId") Long bossId,
            @PathVariable("employeeId") Long employeeId) {

        try {

            	service.deleteEmployee(bossId, employeeId);

            	return ok().build();

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            ProfessionalResponseBody response = new ProfessionalResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na atualização do Professional: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @JsonView(ResponseJsonView.ProfessionalFindAll.class)
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

        log.warn("Ação não permitida para delete o Professional: {}. Atualize o status do Professional para desativado. - {}", idProfessional, errorCode);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

    }

    /**
     *
     * @param professionalProbe Usado pelo Spring para fazer o bind das queries string nos
     *                          atributos da classe.
     * @return
     */
    @JsonView(ResponseJsonView.ProfessionalFindAll.class)
    @RequestMapping(path = "/professionals", method = RequestMethod.GET)
    public HttpEntity<ProfessionalResponseBody> findAll(@ModelAttribute Professional professionalProbe) {

        try {
            List<Professional> entitylist = service.findAllBy(professionalProbe);

            ProfessionalResponseBody responseBody = new ProfessionalResponseBody();
            responseBody.setProfessionalList(entitylist);
            responseBody.setDescription("TOP 10 successfully retrieved.");

            if (!entitylist.isEmpty()) {
                log.info("{} Professionals successfully retrieved.", entitylist.size());

                return ok().body(responseBody);
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(responseBody);
            }

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            ProfessionalResponseBody response = new ProfessionalResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição da Lista de Professional: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Verifica se o Service recebido no request possui ID. Isso indica que o cliente primeiro listou os services
     * e depois usou o Service desejado para o cadastro do Professional, que eh o fluxo correto.
     * @param request
     * @return
     */
    private boolean hasService(ProfessionalRequestBody request) {

        Set<ProfessionalCategory> psCollection = request.getProfessional().getProfessionalCategoryCollection();

        for (ProfessionalCategory ps : psCollection) {
            if (ps.getCategory().getIdCategory() == null) {
                return false;
            }
        }
        return true;
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
