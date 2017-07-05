package com.cosmeticos.repository;

import com.cosmeticos.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Vinicius on 02/07/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerWalletReposioryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Before
    public void setupTets(){

    }

    @Test
    public void insereCustomerEmWalletEWalletEmProfessional(){

        Customer c1 = customerRepository.findOne(1L);
        Customer c2 = customerRepository.findOne(2L);


        Wallet cw1 = new Wallet();

        cw1.getCustomers().add(c1);
        cw1.getCustomers().add(c2);

        //customerWalletRepository.save(cw1);

        Professional s1 = new Professional();
        s1.setNameProfessional("Garry");
        s1.setAddress(new Address());
        s1.setUser(new User("garry", "123qwe", "garry@bol"));

        s1.setWallet(cw1);

        cw1.setProfessional(s1);

        professionalRepository.save(s1);


    }
}
