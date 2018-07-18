package com.cosmeticos.controller;

import com.cosmeticos.commons.ProfessionalCategoryRequestBody;
import com.cosmeticos.commons.ProfessionalCategoryResponseBody;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.model.ProfessionalCategory;
import com.cosmeticos.service.ProfessionalCategoryService;
import com.fasterxml.jackson.annotation.JsonView;
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
 * Created by Vinicius on 22/06/2017.
 */
@Slf4j
@RestController
public class ProfessionalCategoryController {
    @Autowired
    private ProfessionalCategoryService professionalCategoryService;

    @RequestMapping(path = "/professionalcategories", method = RequestMethod.POST)
    public HttpEntity<ProfessionalCategoryResponseBody> create(@Valid @RequestBody ProfessionalCategoryRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                ProfessionalCategory s = professionalCategoryService.create(request);
                log.info("Service adicionado com sucesso:  [{}]", s);

                ProfessionalCategoryResponseBody responseBody = new ProfessionalCategoryResponseBody();

                responseBody.setDescription("Success");
                responseBody.getProfessionalCategoryList().add(s);

                return ok().body(responseBody);


            }
        } catch (Exception e) {

            log.error("Falha no cadastro: {}", e.getMessage(), e);
            ProfessionalCategoryResponseBody responseBody = new ProfessionalCategoryResponseBody();
            responseBody.setDescription(e.getMessage());

            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @JsonView(ResponseJsonView.ProfessionalCategoryFindAll.class)
    @RequestMapping(path = "/professionalcategories", method = RequestMethod.GET)
    public HttpEntity<ProfessionalCategoryResponseBody> findAll(@ModelAttribute ProfessionalCategory professionalCategory) {

        try {
            List<ProfessionalCategory> entitylist = professionalCategoryService.findAllBy(professionalCategory);

            ProfessionalCategoryResponseBody responseBody = new ProfessionalCategoryResponseBody();
            responseBody.setProfessionalCategoryList(entitylist);
            responseBody.setDescription("All Services retrieved.");

            log.info("{} ProfessionalServices successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            ProfessionalCategoryResponseBody responseBody = new ProfessionalCategoryResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/professionalcategories/{id}", method = RequestMethod.GET)
    public HttpEntity<ProfessionalCategoryResponseBody> findById(@PathVariable Long id) {

        try {
            Optional<ProfessionalCategory> optional = professionalCategoryService.find(id);

            if (optional.isPresent()) {

                ProfessionalCategory foundService = optional.get();
                ProfessionalCategoryResponseBody response = new ProfessionalCategoryResponseBody();
                response.setDescription("Service succesfully retrieved");
                response.getProfessionalCategoryList().add(foundService);

                log.info("Busca de Service com exito: [{}]", foundService);

                return ok().body(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", id);
                return notFound().build();
            }
        } catch (Exception e) {
            log.error("Falha na busca por ID: {}", e.getMessage(), e);
            ProfessionalCategoryResponseBody responseBody = new ProfessionalCategoryResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @JsonView(ResponseJsonView.ProfessionalCategoryFindAll.class)
    @RequestMapping(path = "/professionalcategories/nearby/", method = RequestMethod.GET)
    public HttpEntity<ProfessionalCategoryResponseBody> nearby(
            @ModelAttribute ProfessionalCategory bindableQueryObject,
            @RequestParam("latitude") String latitude,
            @RequestParam("longitude") String longitude,
            @RequestParam("radius") String searchRadius
    ) {

        try {
            double receivedLatitude = Double.parseDouble(latitude);
            double receivedLongitude = Double.parseDouble(longitude);

            List<ProfessionalCategory> entitylist = professionalCategoryService.getNearby(bindableQueryObject, receivedLatitude, receivedLongitude, searchRadius);

            ProfessionalCategoryResponseBody responseBody = new ProfessionalCategoryResponseBody();
            responseBody.setProfessionalCategoryList(entitylist);
            responseBody.setDescription("All Services retrieved.");

            log.info("{} ProfessionalServices successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            log.error("Failed to retrieve ProfessionalServices: {}", e.getMessage(), e);

            ProfessionalCategoryResponseBody responseBody = new ProfessionalCategoryResponseBody();
            responseBody.setDescription(e.getMessage());

            return ResponseEntity.status(500).body(responseBody);
        }

    }

    /**
     * Problemas com delete: https://stackoverflow.com/a/32745045/3810036
     * @param idProfessionalCategory
     * @return
     */
    @RequestMapping(path = "/professionalcategories/{id}", method = RequestMethod.DELETE)
    public HttpEntity<Void> delete(@PathVariable("id") Long idProfessionalCategory) {

        try {

            if (idProfessionalCategory == null) {
                log.error("id esta nulo");
                return badRequest().build();
            } else {
                professionalCategoryService.delete(idProfessionalCategory);

                log.info("{} ProfessionalServices successfully deleted.", idProfessionalCategory);

                return ok().build();
            }
        } catch (Exception e) {
            log.error("Failed to delete idProfessionalCategory: {}", idProfessionalCategory, e);
            return ResponseEntity.status(500).build();
        }
    }

    private ProfessionalCategoryResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        ProfessionalCategoryResponseBody responseBody = new ProfessionalCategoryResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }
}
