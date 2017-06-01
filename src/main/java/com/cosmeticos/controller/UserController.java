package com.cosmeticos.controller;

import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.commons.UserRequestBody;
import com.cosmeticos.commons.UserResponseBody;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.model.User;
import com.cosmeticos.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;


/**
 * Created by Vinicius on 29/05/2017.
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService uService;

    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public HttpEntity<UserResponseBody> create(@Valid @RequestBody UserRequestBody request, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            log.error("Erro na requisição do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        }else{
            User us = uService.create(request);
            log.info("User adicionado com sucesso:  [{}]", us);
            return ok().build();
        }
    }

    @RequestMapping(path = "/users", method = RequestMethod.PUT)
    public HttpEntity<UserResponseBody> update(@Valid @RequestBody UserRequestBody request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        }
        else
        {
            User us = uService.update(request);
            log.info(" atualizado com sucesso:  [{}]", us);
            return ok(new UserResponseBody(us));
        }
    }

    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
    public HttpEntity<UserResponseBody> findById(@PathVariable String id) {

        Optional<User> us = uService.find(Long.valueOf(id));

        if(us.isPresent()) {
            log.info("Busca de User com exito: [{}]", us.get());
            UserResponseBody response = new UserResponseBody(us.get());

            return ok().body(response);
        }
        else
        {
            log.error("Nenhum registro encontrado para o id: {}", id);
            return notFound().build();
        }
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public HttpEntity<UserResponseBody> findLastest10() {

        List<User> entitylist = uService.findAll();

        UserResponseBody response = new UserResponseBody();
        response.setUserList(entitylist);
        response.setDescription("TOP 10 successfully retrieved.");

        log.info("{} User successfully retrieved.", entitylist.size());

        return ok().body(response);
    }

    private UserResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        UserResponseBody responseBody = new UserResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }
}
