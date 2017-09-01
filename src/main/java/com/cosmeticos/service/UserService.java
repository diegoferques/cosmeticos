package com.cosmeticos.service;

import com.cosmeticos.commons.UserRequestBody;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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

            persistentUser.setUsername(userFromRequest.getUsername());
            persistentUser.setPassword(userFromRequest.getPassword());
            persistentUser.setEmail(userFromRequest.getEmail());
            persistentUser.setSourceApp(userFromRequest.getSourceApp());
            persistentUser.setRoleCollection(userFromRequest.getRoleCollection());
            persistentUser.setCreditCardCollection(userFromRequest.getCreditCardCollection());
            persistentUser.setStatus(userFromRequest.getStatus());
            persistentUser.setGoodByeReason(userFromRequest.getGoodByeReason());
            persistentUser.setPersonType(userFromRequest.getPersonType());
            persistentUser.setEvaluation(voteService.getUserEvaluation(persistentUser));
            persistentUser.setVoteCollection(userFromRequest.getVoteCollection());

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

    public Boolean verifyEmailExists(String email) {

        Optional<User> userOptional = repository.findByEmail(email);

        Boolean emailExists = userOptional.isPresent();

        return emailExists;

    }

    public Optional<User> passwordReset(UserRequestBody request){

        Optional<User> userOptional = repository.findByEmail(request.getEntity().getEmail());

        if (userOptional.isPresent()) {
            User persistentUser = userOptional.get();

            persistentUser.setPassword(generateRandomPassword(8));

            repository.save(persistentUser);
        }

        return userOptional;
    }

    private String generateRandomPassword(int length) {

        Random RANDOM = new SecureRandom();

        //http://www.java2s.com/Code/Java/Security/GeneratearandomStringsuitableforuseasatemporarypassword.htm
        // Pick from some letters that won't be easily mistaken for each
        // other. So, for example, omit o O and 0, 1 l and L.
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

        String pw = "";
        for (int i=0; i<length; i++)
        {
            int index = (int)(RANDOM.nextDouble()*letters.length());
            pw += letters.substring(index, index+1);
        }

        return pw;
    }
}
