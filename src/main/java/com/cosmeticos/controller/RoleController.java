package com.cosmeticos.controller;


import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.commons.RoleResponseBody;
import com.cosmeticos.model.Role;
import com.cosmeticos.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
public class RoleController {

    private @Autowired
    RoleService service;

    @RequestMapping(path = "/roles", method = RequestMethod.POST)
    public HttpEntity<RoleResponseBody> create(@Valid @RequestBody RoleRequestBody request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        }
        else
        {
            Role s = service.create(request);
            log.info("Role adicionada com sucesso:  [{}]", s);

            return ok().build();
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
