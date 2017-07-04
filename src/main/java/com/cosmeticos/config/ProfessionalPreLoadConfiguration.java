package com.cosmeticos.config;

import com.cosmeticos.model.*;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.CustomerWalletRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe que so vai executar em dev, pois o profile de producao sera PRODUCTION.
 * Created by Lulu on 26/05/2017.
 */
@Configuration
@Profile("default")
public class ProfessionalPreLoadConfiguration {

    @Autowired
    private ProfessionalRepository repository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerWalletRepository customerWalletRepository;

    @PostConstruct
    public void insertInitialH2Data()
    {


        Customer c1 = customerRepository.findOne(1L);
        Customer c2 = customerRepository.findOne(2L);


        Wallet cw1 = new Wallet();

        cw1.getCustomerCollection().add(c1);
        cw1.getCustomerCollection().add(c2);

        //customerWalletRepository.save(cw1);

        Professional s1 = new Professional();
        s1.setNameProfessional("Garry");
        s1.setAddress(new Address());
        s1.setUser(new User("garry", "123qwe", "garry@bol"));

        s1.setWallet(cw1);

        cw1.setProfessional(s1);

        repository.save(s1);



        //
        Professional s2 = new Professional();
        s2.setNameProfessional("Diego");
        s2.setAddress(new Address());
        s2.setUser(new User("Diego", "123qwe", "Diego@bol"));

        Professional s3 = new Professional();
        s3.setNameProfessional("Deivison");
        s3.setAddress(new Address());
        s3.setUser(new User("Deivison", "123qwe", "Deivison@bol"));


        Professional s4 = new Professional();
        s4.setNameProfessional("Vinicius");
        s4.setAddress(new Address());
        s4.setUser(new User("Vinicius", "123qwe", "Vinicius@bol"));

        Professional s5 = new Professional();
        s5.setNameProfessional("Habib");
        s5.setAddress(new Address());
        s5.setUser(new User("Habib", "123qwe", "Habib@bol"));


        repository.save(s2);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);

    }
}
