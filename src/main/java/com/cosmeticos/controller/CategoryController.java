package com.cosmeticos.controller;

import com.cosmeticos.commons.CategoryRequestBody;
import com.cosmeticos.commons.CategoryResponseBody;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.model.Category;
import com.cosmeticos.service.CategoryService;
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
 * Created by Vinicius on 31/05/2017.
 */
@Slf4j
@RestController
public class CategoryController {

    @Autowired
    private CategoryService category;

    @RequestMapping(path = "/categories", method = RequestMethod.POST)
    public HttpEntity<CategoryResponseBody> create(@Valid @RequestBody CategoryRequestBody request, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Category s = category.create(request);
                log.info("Service adicionado com sucesso:  [{}]", s);

                CategoryResponseBody responseBody = new CategoryResponseBody();

                responseBody.setDescription("Success");
                responseBody.getCategoryList().add(s);

                return ok().body(responseBody);


            }
        }catch(Exception e){

            log.error("Falha no cadastro: {}", e.getMessage(), e);
            CategoryResponseBody responseBody = new CategoryResponseBody();
            responseBody.setDescription(e.getMessage());

            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/categories", method = RequestMethod.PUT)
    public HttpEntity<CategoryResponseBody> update(@Valid @RequestBody CategoryRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {

                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else if (request.getEntity().getIdCategory() == null) {

                CategoryResponseBody responseBody = new CategoryResponseBody();
                responseBody.setDescription("Entity ID must to be set!");
                return badRequest().body(responseBody);

            } else {
                Optional<Category> optional = category.update(request);

                if (optional.isPresent()) {
                    Category updatedService = optional.get();

                    CategoryResponseBody responseBody = new CategoryResponseBody();
                    responseBody.getCategoryList().add(updatedService);

                    log.info("Service atualizado com sucesso:  [{}]", updatedService);
                    return ok().body(responseBody);

                } else {

                    log.error("Service nao encontrada: idService[{}]", request.getEntity().getIdCategory());
                    return notFound().build();

                }
            }
        } catch (Exception e) {
            log.error("Falha na atualizacao: {}", e.getMessage(), e);
            CategoryResponseBody responseBody = new CategoryResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @JsonView(ResponseJsonView.CategoryGetAll.class)
    @RequestMapping(path = "/categories", method = RequestMethod.GET)
    public HttpEntity<CategoryResponseBody> findAll(@ModelAttribute Category ma) {

        try {
            List<Category> entitylist = (ma == null) ?
                    category.findAll() :
                    category.findAll(ma); // Busca com filtro

            CategoryResponseBody responseBody = new CategoryResponseBody();
            responseBody.setCategoryList(entitylist);
            responseBody.setDescription("All Services retrieved.");

            log.info("{} Services successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            CategoryResponseBody responseBody = new CategoryResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/categories/{id}", method = RequestMethod.GET)
    public HttpEntity<CategoryResponseBody> findById(@PathVariable String id) {

        try {
            Optional<Category> optional = category.find(Long.valueOf(id));

            if (optional.isPresent()) {

                Category foundService = optional.get();
                CategoryResponseBody response = new CategoryResponseBody();
                response.setDescription("Service succesfully retrieved");
                response.getCategoryList().add(foundService);

                log.info("Busca de Service com exito: [{}]", foundService);

                return ok().body(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", id);
                return notFound().build();
            }
        } catch (Exception e) {
            log.error("Falha na busca por ID: {}", e.getMessage(), e);
            CategoryResponseBody responseBody = new CategoryResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    private CategoryResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        CategoryResponseBody responseBody = new CategoryResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }
}
