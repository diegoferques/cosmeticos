package com.cosmeticos.service;

import com.cosmeticos.commons.UserRequestBody;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
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

    public User create(UserRequestBody request){

        return repository.save(request.getEntity());
    }

    public Optional<User> update(UserRequestBody request){
        User userFromRequest = request.getEntity();

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

    public boolean isValid(String email) {
        User u = new User();
        u.getEmail();
        return true;
    }
}
