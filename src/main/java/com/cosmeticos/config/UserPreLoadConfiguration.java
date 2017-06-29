package com.cosmeticos.config;

import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Vinicius on 12/06/2017.
 */
@Configuration
@Profile("default")
public class UserPreLoadConfiguration {

    @Autowired
     private UserRepository repository;



    @PostConstruct
    public void insertInitialH2Data(){





        CreditCard cc = new CreditCard();
        User u1 = new User();
        cc.setToken("1234");
        cc.setVendor("MasterCard");
        cc.setStatus(CreditCard.Status.ACTIVE);
        //User
        u1.getCreditCardCollection().add(cc);
        u1.setUsername("KILLER");
        u1.setPassword("109809876");
        u1.setEmail("Killer@gmail.com");
        u1.setSourceApp("facebook");


        // CreditCard


        User u2 = new User();
        u2.setUsername("NAMEK");
        u2.setPassword("1234098765");
        u2.setEmail("namek@gmail.com");
        u2.setSourceApp("google+");


        User u3 = new User();
        u3.setUsername("FULANO");
        u3.setPassword("12323454");
        u3.setEmail("fulano@gmail.com");
        u3.setSourceApp("gmail");

        User u4 = new User();
        u4.setUsername("KIRA");
        u4.setPassword("09877");
        u4.setEmail("kira@hotmail.com");
        u4.setSourceApp("facebook");

        User u5 = new User();
        u5.setUsername("CICLANO");
        u5.setPassword("1234");
        u5.setEmail("ciclanor@gmail.com");
        u5.setSourceApp("google+");

        repository.save(u1);
        repository.save(u2);
        repository.save(u3);
        repository.save(u4);
        repository.save(u5);


    }
}
