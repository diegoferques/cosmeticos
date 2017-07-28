package com.cosmeticos.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.model.Wallet;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.*;

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
    private WalletRepository customerWalletRepository;

    @PostConstruct
    public void insertInitialH2Data()
    {
        Customer c1 = customerRepository.findOne(1L);
        Customer c2 = customerRepository.findOne(2L);

        Wallet cw1 = new Wallet();
        cw1.getCustomers().add(c1);
        cw1.getCustomers().add(c2);

        User user1 = new User("garry", "123qwe", "garry@bol");

        Address address1 = new Address();
        address1.setLatitude("-22.7245761");
        address1.setLongitude("-43.51020159999999");


        Professional p1 = new Professional();
        p1.setNameProfessional("Garry");

        p1.setAddress(address1);
        address1.setProfessional(p1);

        // bidirecional reference
        p1.setUser(user1);
        user1.setProfessional(p1);

        // bidirecional reference
        p1.setWallet(cw1);
        cw1.setProfessional(p1);

        repository.save(p1);

        ////////////////////////////////////////
        User user2 = new User("Diego", "123qwe", "Diego@bol");

        Address address2 = new Address();
        address2.setLatitude("-22.750996");
        address2.setLongitude("-43.45973010000001");

        Professional s2 = new Professional();
        s2.setNameProfessional("Diego");
        s2.setAddress(address2);
        s2.setUser(user2);

        user2.setProfessional(s2);
        address2.setProfessional(s2);



        ////////////////////////////////////////
        User user3;

        Address address3 = new Address();
        address3.setLatitude("-22.7248375");
        address3.setLongitude("-43.5251476");

        Professional s3 = new Professional();
        s3.setNameProfessional("Deivison");

        s3.setAddress(address3);
        address3.setProfessional(s3);

        s3.setUser(user3 = new User("Deivison", "123qwe", "Deivison@bol"));
        user3.setProfessional(s3);

        ////////////////////////////////////////
        User user4;
        Address address4 = new Address();
        address4.setLatitude("-22,9111");
        address4.setLongitude("-43,1826");

        Professional s4 = new Professional();
        s4.setNameProfessional("Vinicius");

        s4.setAddress(address4);
        address4.setProfessional(s4);

        s4.setUser(user4 = new User("Vinicius", "123qwe", "Vinicius@bol"));
        user4.setProfessional(s4);

        ////////////////////////////////////////
        User user5;
        Address address5 = new Address();
        address5.setLatitude("-22.7269425");
        address5.setLongitude("-43.528198");

        Professional s5 = new Professional();
        s5.setNameProfessional("Habib");

        s5.setAddress(address5);
        address5.setProfessional(s5);

        s5.setUser(user5 = new User("Habib", "123qwe", "Habib@bol"));
        user5.setProfessional(s5);

        repository.save(s2);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);

        //SEGUE ABAIXO O ITEM 1 DO CARD 39
        //Alterar ProfessionalPreLoadConfiguration, de modo a incluir + 2 profissionais
        //com endereços e Locations fictícios.

        User user6 = new User("kelly", "123abc", "joana@bol");
        Address address6 = new Address();
        address6.setAddress("Travessa Tuviassuiara, 32");
        address6.setNeighborhood("Rodilândia");
        address6.setCity("Nova Iguaçu");
        address6.setState("Rio de Janeiro");
        address6.setCountry("Brazil");
        address6.setCep("26083-285");

        Professional s6 = new Professional();
        s6.setNameProfessional("Kelly");
        s6.setAddress(address6);
        s6.setUser(user6);

        address6.setProfessional(s6);
        user6.setProfessional(s6);

        repository.save(s6);

        User user7 = new User("kdoba", "123abc", "joao@bol");
        Address address7 = new Address();
        address7.setAddress("Avenida Marechal Floriano, 46");
        address7.setNeighborhood("Centro ");
        address7.setCity("Rio de Janeiro/");
        address7.setState("Rio de Janeiro");
        address7.setCountry("Brazil");
        address7.setCep("20080-001");

        Professional s7 = new Professional();
        s7.setNameProfessional("Kdoba");
        s7.setAddress(address7);
        s7.setUser(user7);

        address7.setProfessional(s7);
        user7.setProfessional(s7);

        repository.save(s7);


    }
}
