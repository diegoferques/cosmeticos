package com.cosmeticos.service;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by matto on 10/06/2017.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createFromCustomer(CustomerRequestBody request) {
        User u = new User();
        u.setEmail(request.getUser().getEmail());
        u.setPassword(request.getUser().getPassword());
        u.setSourceApp(request.getUser().getSourceApp());
        u.setUsername(request.getUser().getUsername());

        return userRepository.save(u);
    }

    public User createFakeUser() {
        User u = new User();
        u.setEmail("diego@bol.com");
        //u.setIdLogin(1234L);
        u.setPassword("123qwe");
        u.setSourceApp("google+");
        u.setUsername("diegoferques");
        //u.getCustomerCollection().add(c);
        userRepository.save(u);
        return u;
    }
}
