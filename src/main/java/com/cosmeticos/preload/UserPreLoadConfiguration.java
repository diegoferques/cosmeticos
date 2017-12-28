package com.cosmeticos.preload;

import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Order;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Created by Vinicius on 12/06/2017.
 */

@Configuration
@Profile("default")
public class UserPreLoadConfiguration {

    @Autowired
     private UserRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    @PostConstruct
    public void insertInitialH2Data(){

        Order o1 = orderRepository.findOne(1L);

        Order o2 = orderRepository.findOne(2L);

        CreditCard cc = CreditCard.builder().build();
        cc.setToken("4321");
        cc.setSuffix("1831");
        cc.setExpirationDate(LocalDate.of(2017, 12, 1).format(ofPattern("MM/yy")));
        cc.setVendor("MasterCard");
        cc.setStatus(CreditCard.Status.ACTIVE);

        CreditCard ccNovo = CreditCard.builder().build();
        ccNovo.setToken("7777");
        ccNovo.setExpirationDate(LocalDate.of(2018, 3, 1).format(ofPattern("MM/yy")));
        //ccNovo.setExpirationDate("03/2018");
        ccNovo.setVendor("Visa");
        ccNovo.setStatus(CreditCard.Status.ACTIVE);

        User u1 = new User();
        u1.getCreditCardCollection().add(cc);
        u1.setUsername("KILLER");
        u1.setPassword("109809876");
        u1.setEmail("killer@gmail.com");
        u1.setSourceApp("facebook");
        u1.setPersonType(User.PersonType.FISICA);

        cc.setOwnerName(u1.getUsername());
        ccNovo.setOwnerName(u1.getUsername());

        cc.setUser(u1);
        ccNovo.setUser(u1);
        //--------------------------//

        CreditCard cc2 = CreditCard.builder().build();
        cc2.setToken("7772344377");
        cc2.setSuffix("3053");
        cc2.setExpirationDate(LocalDate.of(2019, 4, 1).format(ofPattern("MM/yy")));
        //cc2.setExpirationDate("12/2018");
        cc2.setVendor("Master");
        cc2.setStatus(CreditCard.Status.INACTIVE);

        CreditCard cc3 =  CreditCard.builder().build();
        cc3.setToken("7772344377");
        cc3.setSuffix("3053");
        cc3.setExpirationDate(LocalDate.of(2019, 12, 1).format(ofPattern("MM/yy")));
        cc3.setVendor("Master");
        cc3.setStatus(CreditCard.Status.ACTIVE);

        User u2 = new User();

        u2.getCreditCardCollection().add(cc2);
        u2.setUsername("NAMEK");
        u2.setPassword("1234098765");
        u2.setEmail("namek@gmail.com");
        u2.setSourceApp("google+");
        u2.setPersonType(User.PersonType.JURIDICA);


        cc2.setOwnerName(u2.getUsername());

        cc2.setUser(u2);
        //-------------------------//


        User u3 = new User();
        u3.setUsername("FULANO");
        u3.setPassword("12323454");
        u3.setEmail("fulano@gmail.com");
        u3.setSourceApp("gmail");
        u3.setPersonType(User.PersonType.FISICA);


        User u4 = new User();
        u4.setUsername("KIRA");
        u4.setPassword("09877");
        u4.setEmail("kira@hotmail.com");
        u4.setSourceApp("facebook");
        u4.setPersonType(User.PersonType.JURIDICA);


        User u5 = new User();
        u5.setUsername("CICLANO");
        u5.setPassword("1234");
        u5.setEmail("ciclanor@gmail.com");
        u5.setSourceApp("google+");
        u5.setPersonType(User.PersonType.FISICA);

        User u6 = new User();
        u6.setUsername("diegoferques");
        u6.setPassword("abc123");
        u6.setEmail("diegoferques@gmail.com");
        u6.setSourceApp("google+");
        u6.setPersonType(User.PersonType.FISICA);


        repository.save(u1);
        repository.save(u2);
        repository.save(u3);
        repository.save(u4);
        repository.save(u5);
        repository.save(u6);


    }
}
