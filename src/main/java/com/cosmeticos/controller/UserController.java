package com.cosmeticos.controller;

import com.cosmeticos.commons.*;
import com.cosmeticos.model.Service;
import com.cosmeticos.model.User;
import com.cosmeticos.service.UserService;
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
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;


/**
 * Created by Vinicius on 29/05/2017.
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService service;

    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public HttpEntity<UserResponseBody> create(@Valid @RequestBody UserRequestBody request, BindingResult bindingResult){

        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                User u = service.create(request);
                log.info("User adicionado com sucesso:  [{}]", u);

                UserResponseBody responseBody = new UserResponseBody();

                responseBody.setDescription("Success");
                responseBody.getUserList().add(u);

                return ok().body(responseBody);


            }
        }catch(Exception e){

            log.error("Falha no cadastro: {}", e.getMessage(), e);
            UserResponseBody responseBody = new UserResponseBody();
            responseBody.setDescription(e.getMessage());

            return ResponseEntity.status(500).body(responseBody);
        }

    }

    @RequestMapping(path = "/users", method = RequestMethod.PUT)
    public HttpEntity<UserResponseBody> update(@Valid @RequestBody UserRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {

                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else if (request.getEntity().getIdLogin() == null) {

                UserResponseBody responseBody = new UserResponseBody();
                responseBody.setDescription("Entity ID must to be set!");
                return badRequest().body(responseBody);

            } else {

                Optional<User> userOptional = service.update(request);

                if (userOptional.isPresent()) {
                    User updatedUser = userOptional.get();

                    UserResponseBody responseBody = new UserResponseBody();
                    responseBody.getUserList().add(updatedUser);

                    log.info("User atualizado com sucesso:  [{}]", updatedUser);
                    return ok().body(responseBody);

                } else {

                    log.error("User nao encontrada: idLogin[{}]", request.getEntity().getIdLogin());
                    return notFound().build();

                }
            }
        } catch (Exception e) {
            log.error("Falha na atualizacao: {}", e.getMessage(), e);
            UserResponseBody responseBody = new UserResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }

    }

    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
    public HttpEntity<UserResponseBody> findById(@PathVariable String id) {

        try {
            Optional<User> optional = service.find(Long.valueOf(id));

            if (optional.isPresent()) {

                User foundService = optional.get();
                UserResponseBody response = new UserResponseBody();
                response.setDescription("User succesfully retrieved");
                response.getUserList().add(foundService);

                log.info("Busca de User com exito: [{}]", foundService);

                return ok().body(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", id);
                return notFound().build();
            }
        } catch (Exception e) {
            log.error("Falha na busca por ID: {}", e.getMessage(), e);
            UserResponseBody responseBody = new UserResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }

    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public HttpEntity<UserResponseBody> findAll() {

        try {
            List<User> entitylist = service.findAll();

            UserResponseBody responseBody = new UserResponseBody();
            responseBody.setUserList(entitylist);
            responseBody.setDescription("All Users retrieved.");

            log.info("{} Users successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            UserResponseBody responseBody = new UserResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
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
