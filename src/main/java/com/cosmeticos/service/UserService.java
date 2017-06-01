package com.cosmeticos.service;

import com.cosmeticos.commons.UserRequestBody;
import com.cosmeticos.model.Schedule;
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
        User user = new User();
        user.setUsername(request.getOwnerName());
        user.setPassword(request.getPassWordUser());
        user.setSourceApp(request.getSourceApp());

        return repository.save(user);
    }

    public User update(UserRequestBody request){
        User user = repository.findOne(request.getIdUser());
        user.setUsername(request.getOwnerName());
        user.setPassword(request.getPassWordUser());
        user.setSourceApp(request.getSourceApp());

        return repository.save(user);
    }

    public Optional<User> find(Long id){
        return Optional.of(repository.findOne(id));
    }

    public void deletar(){
        throw new UnsupportedOperationException("Excluir de acordo com o Status. ");
    }

    public List<User> findAll() {

        Iterable<User> result = repository.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }

}
