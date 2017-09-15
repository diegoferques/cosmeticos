package com.cosmeticos.service;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.commons.UserRequestBody;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
import com.cosmeticos.smtp.MailSenderService;
import com.cosmeticos.validation.UserValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Vinicius on 29/05/2017.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private VoteService voteService;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private RandomCode randomCodeService;

    public User create(UserRequestBody request){

        return repository.save(request.getEntity());
    }

    /**
     *
     * @param request
     * @return
     * @deprecated Devemos usar update(User).
     */
    public Optional<User> update(UserRequestBody request){
        return update(request.getEntity());
    }

    public Optional<User> update(User userFromRequest){

        // TODO ver possibilidade de usar VO pq para update, o ID deve ser obrigatorio.
        Long requestedIdLogin = userFromRequest.getIdLogin();

        Optional<User> optional = Optional.ofNullable(repository.findOne(requestedIdLogin));

        if (optional.isPresent()) {
            User persistentUser = optional.get();

            if (userFromRequest.getUsername() != null) {
                persistentUser.setUsername(userFromRequest.getUsername());
            }
            if (userFromRequest.getPassword() != null) {
                persistentUser.setPassword(userFromRequest.getPassword());
            }
            if (userFromRequest.getEmail() != null) {
                persistentUser.setEmail(userFromRequest.getEmail());
            }
            if (userFromRequest.getSourceApp() != null) {
                persistentUser.setSourceApp(userFromRequest.getSourceApp());
            }
            if (userFromRequest.getRoleCollection() != null) {
                persistentUser.setRoleCollection(userFromRequest.getRoleCollection());
            }
            if (userFromRequest.getCreditCardCollection() != null) {
                persistentUser.setCreditCardCollection(userFromRequest.getCreditCardCollection());
            }
            if (userFromRequest.getStatus() != null) {
                persistentUser.setStatus(userFromRequest.getStatus());
            }
            if (userFromRequest.getGoodByeReason() != null) {
                persistentUser.setGoodByeReason(userFromRequest.getGoodByeReason());
            }
            if (userFromRequest.getPersonType() != null) {
                persistentUser.setPersonType(userFromRequest.getPersonType());
            }
            if (userFromRequest.getVoteCollection() != null) {
                persistentUser.setVoteCollection(userFromRequest.getVoteCollection());
            }
            if (userFromRequest.getLostPasswordToken() != null) {
                persistentUser.setLostPasswordToken(userFromRequest.getLostPasswordToken());
            }

            persistentUser.setEvaluation(voteService.getUserEvaluation(persistentUser));

            repository.save(persistentUser);
        }

        return optional;
    }

    public Optional<User> find(Long id){
        return Optional.ofNullable(repository.findOne(id));
    }

    public void deletar(){
        throw new UnsupportedOperationException("Excluir de acordo com o Status. ");
    }

    public List<User> findAll() {

        Iterable<User> result = repository.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }

    public Boolean verifyEmailExistsforCreate(String email) {

        Optional<User> userOptional = repository.findByEmail(email);

        Boolean emailExists = userOptional.isPresent();

        return emailExists;

    }

    public Boolean verifyEmailExistsforUpdate(User receivedUser) {

        User persistentUser = repository.findOne(receivedUser.getIdLogin());


        if (receivedUser.getEmail() != null && !receivedUser.getEmail().isEmpty()) {
            Boolean emailExists = persistentUser.getEmail().equals(receivedUser.getEmail());

            return !emailExists;
        } else {
            return false;
        }
    }

    public Optional<User> saveToken(UserRequestBody request){

        Optional<User> userOptional = repository.findByEmail(request.getEntity().getEmail());


        if (userOptional.isPresent()) {
            User persistentUser = userOptional.get();

            //RandomCode random =  new RandomCode(4);
            String code = randomCodeService.nextString();
            
            persistentUser.setLostPasswordToken(code);

            repository.save(persistentUser);
        }

        return userOptional;
    }

    public User preparePasswordReset(User entity) {

        Optional<User> userOptional = repository.findByEmail(entity.getEmail());

        if (userOptional.isPresent()) {

            User persistentUser = userOptional.get();

            String code = randomCodeService.nextString();

            Boolean sendEmail = mailSenderService.sendEmail(entity.getEmail(),
                    "Esqueci minha senha",
                    "Seu token pra recriar sua senha é: " + code);

            if(sendEmail == true){
                persistentUser.setLostPasswordToken(code);

                update(persistentUser);

                return persistentUser;
            }else{
                throw new UserValidationException(ResponseCode.USER_PASSWORD_RESET_EMAIL_FAIL, "Falha ao enviar email com token.");
            }
        } else {
            return null;
        }

    }

    public void sendSuccesfullPasswordResetMessage(String email) {
        mailSenderService.sendEmail(email, "Atualização de senha", "Senha atualizada com sucesso");
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
