package com.cosmeticos.controller;

import com.cosmeticos.commons.UserRequestBody;
import com.cosmeticos.commons.UserResponseBody;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.User;
import com.cosmeticos.service.UserService;
import com.cosmeticos.smtp.MailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;


/**
 * Created by Vinicius on 29/05/2017.
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    MailSenderService mailSenderService;

    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public HttpEntity<UserResponseBody> create(@Valid @RequestBody UserRequestBody request, BindingResult bindingResult) {

        try {

            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else if (!validateCreditCards(request.getEntity().getCreditCardCollection())) {

                UserResponseBody responseBody = new UserResponseBody();
                responseBody.setDescription("Nao e permitido cadastro de usuario associados a cartoes pre-existentes.");
                log.error("Nao e permitido cadastro de usuario associados a cartoes pre-existentes.");
                return badRequest().body(responseBody);

            } else if(service.verifyEmailExists(request.getEntity().getEmail())) {
                UserResponseBody responseBody = new UserResponseBody();
                responseBody.setDescription("E-mail já existente.");
                log.error("Nao e permitido cadastro de usuario associados a emails pre-existentes.");
                return badRequest().body(responseBody);

            } else {
                User u = service.create(request);
                log.info("User adicionado com sucesso:  [{}]", u);

                UserResponseBody responseBody = new UserResponseBody(u);
                responseBody.setDescription("Success");

                return ok().body(responseBody);


            }
        } catch (Exception e) {

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
                log.error("BAD REQUEST: Entity ID must to be set!");
                return badRequest().body(responseBody);

            } else if(service.verifyEmailExists(request.getEntity().getEmail())) {
                UserResponseBody responseBody = new UserResponseBody();
                responseBody.setDescription("E-mail já existente.");
                log.error("Nao e permitido atualizar usuario para um novo emails pre-existentes.");
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
                UserResponseBody response = new UserResponseBody(foundService);
                response.setDescription("User succesfully retrieved");

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

    //TODO - FALTA TERMINAR A CONFIGURACAO DO SMTP E TESTAR COM USUARIO E SENHA
    //TODO - FALTA CONFIGURAR EMAIL, SENHA SMTP E ETC DO SERVIDOR
    //TODO - FALTA CRIAR TESTES E VALIDAR
    //REFERENCIA: https://www.quickprogrammingtips.com/spring-boot/how-to-send-email-from-spring-boot-applications.html
        @RequestMapping(path = "/password_reset", method = RequestMethod.PUT)
    public HttpEntity<UserResponseBody> passwordReset(@RequestBody UserRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {

                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else if (request.getEntity().getEmail() == null) {

                UserResponseBody responseBody = new UserResponseBody();
                responseBody.setDescription("E-mail não informado!");
                log.error("BAD REQUEST: E-mail não informado!");
                return badRequest().body(responseBody);

            } else if(!service.verifyEmailExists(request.getEntity().getEmail())) {
                UserResponseBody responseBody = new UserResponseBody();
                responseBody.setDescription("E-mail inexistente.");
                log.error("NOT FOUND: E-mail inexistente.");
                return notFound().build();

            } else {

                Optional<User> userOptional = service.passwordReset(request);
                User updatedUser = userOptional.get();

                Boolean sendEmail = mailSenderService.sendPasswordReset(updatedUser);

                UserResponseBody responseBody = new UserResponseBody();

                if (sendEmail) {
                    responseBody.setDescription("Uma nova senha foi enviada para o email cadastrado");

                    log.info("Uma nova senha foi enviada para o email cadastrado:  [{}]", updatedUser);
                    return ok().body(responseBody);
                } else {
                    responseBody.setDescription("Houve um erro ao enviar o email com sua nova senha, tente novamente.");

                    log.info("Houve um erro ao enviar o email com sua nova senha, tente novamente.:  [{}]", updatedUser);
                    return ResponseEntity.status(500).body(responseBody);
                }
            }

        } catch (Exception e) {
            log.error("Falha na atualizacao: {}", e.getMessage(), e);
            UserResponseBody responseBody = new UserResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }

    }

    private boolean validateCreditCards(Set<CreditCard> creditCardCollection) {
        if(CollectionUtils.isEmpty(creditCardCollection))
        {
            return true;
        }
        else{
            for (CreditCard cc : creditCardCollection ) {
                if (cc.getIdCreditCard() != null)
                {
                    return false;
                }
            }
        }
        return true;
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
