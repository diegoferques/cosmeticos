package com.cosmeticos.config;

import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
        cc.setToken("4321");
        cc.setCardNumber("45518700000001831");
        cc.setSecurityCode("123");
        cc.setExpirationDate(Timestamp.valueOf(LocalDateTime.of(2017, 12, 6, 0, 0)));
        cc.setVendor("MasterCard");
        cc.setStatus(CreditCard.Status.ACTIVE);

        CreditCard ccNovo = new CreditCard();
        ccNovo.setToken("77777");
        ccNovo.setSecurityCode("321");
        ccNovo.setExpirationDate(Timestamp.valueOf(LocalDateTime.of(2018, 03, 10, 0, 0)));
        ccNovo.setVendor("Visa");
        ccNovo.setStatus(CreditCard.Status.ACTIVE);

        User u1 = new User();
        u1.getCreditCardCollection().add(cc);
        u1.setUsername("KILLER");
        u1.setPassword("109809876");
        u1.setEmail("killer@gmail.com");
        u1.setSourceApp("facebook");

        cc.setOwnerName(u1.getUsername());
        ccNovo.setOwnerName(u1.getUsername());

        cc.setUser(u1);
        ccNovo.setUser(u1);
        //--------------------------//

        CreditCard cc2 = new CreditCard();
        cc2.setToken("7772344377");
        cc2.setCardNumber("67730922222223053");
        cc2.setSecurityCode("123");
        cc2.setExpirationDate(Timestamp.valueOf(LocalDateTime.of(2018, 12, 2, 0, 0)));
        cc2.setVendor("Master");
        cc2.setStatus(CreditCard.Status.INACTIVE);

        User u2 = new User();

        u2.getCreditCardCollection().add(cc2);
        u2.setUsername("NAMEK");
        u2.setPassword("1234098765");
        u2.setEmail("namek@gmail.com");
        u2.setSourceApp("google+");

        cc2.setOwnerName(u2.getUsername());

        cc2.setUser(u2);
        //-------------------------//


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
