package com.cosmeticos.service;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.commons.UserRequestBody;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Image;
import com.cosmeticos.model.Payment;
import com.cosmeticos.model.User;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.repository.CreditCardRepository;
import com.cosmeticos.repository.UserRepository;
import com.cosmeticos.smtp.MailSenderService;
import com.cosmeticos.validation.UserValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private CustomerService customerService;

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private RandomCode randomCodeService;

    @Autowired
    private ImageService imageService;

    @Autowired
    @Qualifier("charger")
    private Charger charger;

    @Autowired
    private CreditCardRepository creditCardRepository;

    public User create(UserRequestBody request) {

        return repository.save(request.getEntity());
    }

    public Optional<User> update(User userFromRequest) {

        Long requestedIdLogin = userFromRequest.getIdLogin();

        Optional<User> optional = (repository.findById(requestedIdLogin));

        if (optional.isPresent()) {
            User persistentUser = optional.get();

            // User name nao se altera
            //if (userFromRequest.getUsername() != null) {
            //    persistentUser.setUsername(userFromRequest.getUsername());
            //}
           //if (userFromRequest.getPassword() != null) {
           //    persistentUser.setPassword(userFromRequest.getPassword());
           //}

            // email nao se altera
            //if (userFromRequest.getEmail() != null) {
            //    persistentUser.setEmail(userFromRequest.getEmail());
            //}
            if (userFromRequest.getSourceApp() != null) {
                persistentUser.setSourceApp(userFromRequest.getSourceApp());
            }
            if (userFromRequest.getRoleCollection() != null) {
                persistentUser.setRoleCollection(userFromRequest.getRoleCollection());
            }
            if (userFromRequest.getCreditCardCollection() != null) {
                persistentUser.setCreditCardCollection(userFromRequest.getCreditCardCollection());
            }
            //

            if (userFromRequest.getStatus() != null) {
                persistentUser.setStatus(userFromRequest.getStatus());

                /*
                // Remove a conta do usuario
                // TODO: criar endpoint especifico pra signout
                if (User.Status.INACTIVE.equals(userFromRequest.getStatus())) {
                    this.inactiveUserType(userFromRequest);
                }
                */
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
            if(!StringUtils.isEmpty(userFromRequest.getFirebaseInstanceId())){
                persistentUser.setFirebaseInstanceId(userFromRequest.getFirebaseInstanceId());
            }

            if (userFromRequest.getProfileImageUrl() != null) {
                persistentUser.setProfileImageUrl(
                        "https://s3-sa-east-1.amazonaws.com" +
                        userFromRequest.getProfileImageUrl()
                );
            }

            persistentUser.setEvaluation(voteService.getUserEvaluation(persistentUser));

            repository.save(persistentUser);
        }

        return optional;
    }

    public Optional<User> find(Long id) {
        return (repository.findById(id));
    }

    public void deletar() {
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

        Optional<User> persistentUserOpt = repository.findById(receivedUser.getIdLogin());

        if (persistentUserOpt.isPresent()) {
            User persistentUser = persistentUserOpt.get();
            if (receivedUser.getEmail() != null && !receivedUser.getEmail().isEmpty()) {
                Boolean emailExists = persistentUser.getEmail().equals(receivedUser.getEmail());

                return !emailExists;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException(receivedUser.getIdLogin() + " nao eh um id de usuario valido!");
        }
    }

    public Optional<User> saveToken(UserRequestBody request) {

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

            if (sendEmail == true) {
                persistentUser.setLostPasswordToken(code);
                persistentUser.setLostPassword(true);

                update(persistentUser);

                return persistentUser;
            } else {
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

    public boolean validateToken(User persistentUser, User request) {

        //VERIFICAMOS SE O TOKEN DO USUARIO NO BANCO EH O MESMO QUE O INFORMADO
        if (!persistentUser.getLostPasswordToken().equals(request.getLostPasswordToken())) {
            return false;

        } else if (persistentUser.getLostPassword() != true) {
            return false;
        } else {
            return true;
        }

    }

    public void invalidateToken(User user) {
        user.setLostPassword(false);
        user.setLostPasswordToken(null);
        update(user);
    }

    public User addCreditCard(User user, Payment payment) {

        ChargeResponse<Object> result = charger.addCard(new ChargeRequest<>(payment));

        String token = result.getBody().toString();

        CreditCard cc = payment.getCreditCard();
        cc.setToken(token);

        user.addCreditCard(cc);

        update(user);

        return user;

    }

    public List<User> findAllBy(final User userProbe) {

        // Se nao colocar nulo, esses campos entram na clausula where e nao retorna exatamente o que foi pedido nas queries string
        if (userProbe != null) {
            userProbe.setLostPassword(null);
            userProbe.setEvaluation(null);
            userProbe.setUserType(null);
            userProbe.setStatus(User.Status.ACTIVE);
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase("email");

        return repository.findAll(Example.of(userProbe, matcher));
    }

    public User addImage(Long idUser, Image image) {
        User user = repository.findById(idUser).get();

        // TODO: transformar em property
        String s3host = "https://s3-sa-east-1.amazonaws.com";

        if (!image.getCloudUrlPath().startsWith(s3host)) {
            image.setCloudUrlPath(s3host + image.getCloudUrlPath());
        }
        user.addImage(image);

        return repository.save(user);
    }
}
