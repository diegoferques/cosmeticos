package com.cosmeticos.config;

import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by matto on 24/06/2017.
 */
@Slf4j
@Configuration
@Profile("default")
public class CustomerPreLoadConfiguration {
    @Autowired
    private CustomerRepository customerRepository;

    @PostConstruct
    public void insertInitialH2Data() throws ParseException {
        try {
            Customer c1 = new Customer();
            c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
            c1.setCellPhone("(21) 98877-6655");
            c1.setCpf("098.765.432-10");
            c1.setDateRegister(Calendar.getInstance().getTime());
            c1.setGenre('M');
            c1.setNameCustomer("João da Silva");
            //c1.setOrderCollection(null);
            c1.setStatus(Customer.Status.ACTIVE.ordinal());
            c1.setIdAddress(this.createFakeAddress());
            c1.setUser(this.createFakeLogin("josilva","josilva@bol.com"));

            Date birthDate2 = new SimpleDateFormat("yyyy-MM-dd").parse("1981-01-20");
            Customer c2 = new Customer();
            c2.setBirthDate(birthDate2);
            c2.setCellPhone("(21) 98807-2756");
            c2.setCpf("098.330.987-62");
            c2.setDateRegister(Calendar.getInstance().getTime());
            c2.setGenre('M');
            c2.setNameCustomer("Diego Fernandes");
            //c2.setOrderCollection(null);
            c2.setStatus(Customer.Status.ACTIVE.ordinal());
            c2.setIdAddress(this.createFakeAddress());
            c2.setUser(this.createFakeLogin("loverboy", "diegoferques33@bol.com"));

            Date birthDate3 = new SimpleDateFormat("yyyy-MM-dd").parse("1982-01-20");
            Customer c3 = new Customer();
            c3.setBirthDate(birthDate3);
            c3.setCellPhone("(21) 99988-7766");
            c3.setCpf("831.846.135-15");
            c3.setDateRegister(Calendar.getInstance().getTime());
            c3.setGenre('F');
            c3.setNameCustomer("Maria das Dores");
            //c3.setOrderCollection(null);
            c3.setStatus(Customer.Status.ACTIVE.ordinal());
            c3.setIdAddress(this.createFakeAddress());
            c3.setUser(this.createFakeLogin("madores", "madores@bol.com"));

            Date birthDate4 = new SimpleDateFormat("yyyy-MM-dd").parse("1983-01-20");
            Customer c4 = new Customer();
            c4.setBirthDate(birthDate4);
            c4.setCellPhone("(21) 99887-7665");
            c4.setCpf("816.810.695-68");
            c4.setDateRegister(Calendar.getInstance().getTime());
            c4.setGenre('F');
            c4.setNameCustomer("Fernanda Cavalcante");
            //c4.setOrderCollection(null);
            c4.setStatus(Customer.Status.INACTIVE.ordinal());
            c4.setIdAddress(this.createFakeAddress());
            c4.setUser(this.createFakeLogin("fernandacal", "fecal@bol2.com"));

            Date birthDate5 = new SimpleDateFormat("yyyy-MM-dd").parse("1984-01-20");
            Customer c5 = new Customer();
            c5.setBirthDate(birthDate5);
            c5.setCellPhone("(21) 97766-5544");
            c5.setCpf("541.913.254-81");
            c5.setDateRegister(Calendar.getInstance().getTime());
            c5.setGenre('M');
            c5.setNameCustomer("José das Couves");
            //c5.setOrderCollection(null);
            c5.setStatus(Customer.Status.ACTIVE.ordinal());
            c5.setIdAddress(this.createFakeAddress());
            c5.setUser(this.createFakeLogin("diegoferequest","diegoferques@bol.com"));

            customerRepository.save(c1);
            customerRepository.save(c2);
            customerRepository.save(c3);
            customerRepository.save(c4);
            customerRepository.save(c5);
        } catch (Exception e) {
            log.error("Falha no UserPreLoad", e);
        }
    }

    private User createFakeLogin(String username, String email) {
        User u = new User();
        u.setEmail(email);
        //u.setUser(1234L);
        u.setPassword("123qwe");
        u.setSourceApp("google+");
        u.setUsername(username);
        //u.getCustomerCollection().add(c);
        //userRepository.save(u);
        return u;
    }

    private Address createFakeAddress() {
        Address a = new Address();
        a.setAddress("Rua Perlita");
        a.setCep("0000000");
        a.setCity("RJO");
        a.setCountry("BRA");
        a.setNeighborhood("Austin");
        a.setState("RJ");
        //a.getCustomerCollection().add(customer);
        //addressRepository.save(a);
        return a;
    }
}
