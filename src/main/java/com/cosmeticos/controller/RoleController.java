package com.cosmeticos.controller;


import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.commons.RoleResponseBody;
import com.cosmeticos.model.Role;
import com.cosmeticos.service.RoleService;
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

@Slf4j
@RestController
public class RoleController {

    @Autowired
    private RoleService service;

    @RequestMapping(path = "/roles", method = RequestMethod.POST)
    public HttpEntity<RoleResponseBody> create(@Valid @RequestBody RoleRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Role s = service.create(request);
                log.info("Role adicionada com sucesso:  [{}]", s);

                RoleResponseBody responseBody = new RoleResponseBody();
                responseBody.setDescription("success");
                responseBody.getRoleList().add(s);

                return ok().body(responseBody);
            }
        } catch (Exception e) {
            log.error("Falha no cadastro: {}", e.getMessage(), e);
            RoleResponseBody responseBody = new RoleResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }


    @RequestMapping(path = "/roles", method = RequestMethod.PUT)
    public HttpEntity<RoleResponseBody> update(@Valid @RequestBody RoleRequestBody request, BindingResult
            bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else if (request.getEntity().getIdRole() == null) {
                RoleResponseBody responseBody = new RoleResponseBody();
                responseBody.setDescription("Entity ID must to be set!");
                return badRequest().body(responseBody);
            } else {
                Optional<Role> optional = service.update(request);

                if (optional.isPresent()) {
                    Role updatedRole = optional.get();

                    RoleResponseBody responseBody = new RoleResponseBody();
                    responseBody.getRoleList().add(updatedRole);

                    log.info("Role atualizado com sucesso:  [{}]", updatedRole);

                    return ok().body(responseBody);
                } else {
                    log.error("Role nao encontrada: idRole[{}]", request.getEntity().getIdRole());
                    return notFound().build();
                }
            }
        } catch (Exception e) {
            log.error("Falha na atualizacao: {}", e.getMessage(), e);
            RoleResponseBody responseBody = new RoleResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/roles", method = RequestMethod.GET)
    public HttpEntity<RoleResponseBody> findAll() {

        try {
            List<Role> entitylist = service.findAll();

            RoleResponseBody responseBody = new RoleResponseBody();
            responseBody.setRoleList(entitylist);
            responseBody.setDescription("All roles retrieved.");

            log.info("{} Roles successfully retrieved.", entitylist.size());

            return ok().body(responseBody);
        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            RoleResponseBody responseBody = new RoleResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/roles/{id}", method = RequestMethod.GET)
    public HttpEntity<RoleResponseBody> findById(@PathVariable String id) {

        try {
            Optional<Role> optional = service.find(Long.valueOf(id));

            if (optional.isPresent()) {

                Role foundRole = optional.get();
                RoleResponseBody response = new RoleResponseBody();
                response.setDescription("Role succesfully retrieved");
                response.getRoleList().add(foundRole);

                log.info("Busca de Schedule com exito: [{}]", foundRole);

                return ok().body(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", id);
                return notFound().build();
            }
        } catch (Exception e) {
            log.error("Falha na busca por ID: {}", e.getMessage(), e);
            RoleResponseBody responseBody = new RoleResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }


    private RoleResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        RoleResponseBody responseBody = new RoleResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }


}
