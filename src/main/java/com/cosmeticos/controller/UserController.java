package com.cosmeticos.controller;

import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.commons.UserRequestBody;
import com.cosmeticos.commons.UserResponseBody;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
import com.cosmeticos.service.UserService;
import com.cosmeticos.smtp.MailSenderService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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
    private UserRepository userRepository;



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

            } else if(service.verifyEmailExistsforCreate(request.getEntity().getEmail())) {
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

            } else if(service.verifyEmailExistsforUpdate(request.getEntity())) {
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

    @JsonView(ResponseJsonView.ProfessionalCategoryFindAll.class)
    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
    public HttpEntity<UserResponseBody> findById(@PathVariable Long id) {

        try {
            Optional<User> optional = service.find(id);

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

    //TODO - AO CHAMAR ESTE ENDPOINT COM UM EMAIL QUE NAO EXISTE NO BANCO, RETORNA ERRO 500
    @RequestMapping(path = "/users/prepare_send_token", method = RequestMethod.POST)
    public HttpEntity<UserResponseBody> preparePasswordReset(@RequestBody UserRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {

                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else if (request.getEntity().getEmail() == null) {

                UserResponseBody responseBody = new UserResponseBody();
                responseBody.setDescription("E-mail não informado!");
                log.error("BAD REQUEST: E-mail não informado!");
                return badRequest().body(responseBody);

            } else {

                User user = service.preparePasswordReset(request.getEntity());

                if (user != null) {

                    UserResponseBody responseBody = new UserResponseBody(user);
                    responseBody.setDescription("Uma nova senha foi enviada para o email cadastrado");

                    log.info("Uma nova senha foi enviada para o email cadastrado: [{}]", user.getEmail());
                    return ok().body(responseBody);
                } else {

                    UserResponseBody responseBody = new UserResponseBody(user);
                    responseBody.setDescription("Usuario com o email " + request.getEntity().getEmail() + " nao existe");

                    log.error(responseBody.getDescription());

                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
                }
            }

        } catch (Exception e) {
            log.error("Falha na atualizacao: {}", e.getMessage(), e);
            UserResponseBody responseBody = new UserResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }

    }

    //TODO - CHAMEI ESTE ENDPOINT SEM PASSAR A SENHA E DEU ERRO 500
    //TODO - CHAMEI ESTE ENDPOINT SEM PASSANDO A SENHA E DEU ERRO 500, MAS ALTEROU A SENHA E NAO FINALIZOU COM SUCESSO
    //TODO - FALTA TERMINAR A CONFIGURACAO DO SMTP E TESTAR COM USUARIO E SENHA
    //TODO - FALTA CONFIGURAR EMAIL, SENHA SMTP E ETC DO SERVIDOR
    //TODO - FALTA CRIAR TESTES E VALIDAR
    //REFERENCIA: https://www.quickprogrammingtips.com/spring-boot/how-to-send-email-from-spring-boot-applications.html
    @RequestMapping(path = "/users/password_reset", method = RequestMethod.PUT)
    public HttpEntity<UserResponseBody> passwordReset(@RequestBody UserRequestBody request, BindingResult bindingResult) {

        // Se o generateToken do request estiver  nulo, retornar BAD REQUEST
        UserResponseBody responseBody = new UserResponseBody();

        String receivedToken = request.getEntity().getLostPasswordToken();
        if(receivedToken == null || receivedToken.isEmpty() ){
            responseBody.setDescription("Token vazio");
            return badRequest().body(responseBody);
        }

        // Buscar o User no banco atraves de service.find(user.getId()) e ver se o
        // token recebido no request eh igual ao token do user que foi retornado pelo service. Se nao for igual, retornar BAD REQUEST.
        Optional<User> userOptional = service.findByEmail(request.getEntity().getEmail());

        if (userOptional.isPresent()) {
            if(!receivedToken.equals(userOptional.get().getLostPasswordToken())){
                responseBody.setDescription("Token Invalido");
                return badRequest().body(responseBody);
            }
        }else{
            return notFound().build();
        }

        request.getEntity().setIdLogin(userOptional.get().getIdLogin());

        ResponseEntity<UserResponseBody> response = (ResponseEntity<UserResponseBody>) update(request, bindingResult);

        if(HttpStatus.OK.equals(response.getStatusCode()))
        {
            service.invalidateToken(request.getEntity());
            service.sendSuccesfullPasswordResetMessage(request.getEntity().getEmail());

            return response;

        }
        else
        {
            return response;
        }
    }

    @RequestMapping(path = "/users/validate_token", method = RequestMethod.POST)
    public HttpEntity<UserResponseBody> validateToken(@RequestBody UserRequestBody request) {

        try {

            if(request.getEntity().getEmail().isEmpty() || request.getEntity().getLostPasswordToken().isEmpty()) {
                UserResponseBody responseBody = new UserResponseBody();
                responseBody.setDescription("Requisição inválida!");
                log.error("Requisição inválida!.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);

            } else {

                Optional<User> userOptional = service.findByEmail(request.getEntity().getEmail());

                if (!userOptional.isPresent()) {
                    UserResponseBody responseBody = new UserResponseBody();
                    responseBody.setDescription("E-mail informado não encontrado!");
                    log.error("E-mail informado não encontrado.");

                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);

                } else if(!service.validateToken(userOptional.get(), request.getEntity())) {
                    UserResponseBody responseBody = new UserResponseBody();
                    responseBody.setDescription("Token ou email inválido!");
                    log.error("Token ou email inválido!.");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);

                } else {
                    UserResponseBody responseBody = new UserResponseBody();
                    responseBody.setDescription("Token validado com sucesso!");
                    log.info("Token validado com sucesso:  [{}]", request.getEntity().getLostPasswordToken());

                    return ok().body(responseBody);
                }

            }



        } catch (Exception e) {
            UserResponseBody responseBody = new UserResponseBody();
            responseBody.setDescription("Houve um erro ao processar a sua solicitação, tente novamente mais tarde!");

            log.error("Falha na validação do token: {}", e.getMessage(), e);

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
