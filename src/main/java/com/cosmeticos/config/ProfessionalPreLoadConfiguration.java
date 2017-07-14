package com.cosmeticos.config;

import com.cosmeticos.model.*;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

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

        Professional p1 = new Professional();
        p1.setNameProfessional("Garry");
        p1.setAddress(new Address());

        // bidirecional reference
        p1.setUser(user1);
        user1.setProfessional(p1);

        // bidirecional reference
        p1.setWallet(cw1);
        cw1.setProfessional(p1);

        repository.save(p1);



        //
        User user2 = new User("Diego", "123qwe", "Diego@bol");
        Professional s2 = new Professional();
        s2.setNameProfessional("Diego");
        s2.setAddress(new Address());
        s2.setUser(user2);
        user2.setProfessional(s2);

        User user3;
        Professional s3 = new Professional();
        s3.setNameProfessional("Deivison");
        s3.setAddress(new Address());
        s3.setUser(user3 = new User("Deivison", "123qwe", "Deivison@bol"));
        user3.setProfessional(s3);

        User user4;
        Professional s4 = new Professional();
        s4.setNameProfessional("Vinicius");
        s4.setAddress(new Address());
        s4.setUser(user4 = new User("Vinicius", "123qwe", "Vinicius@bol"));
        user4.setProfessional(s4);

        User user5;
        Professional s5 = new Professional();
        s5.setNameProfessional("Habib");
        s5.setAddress(new Address());
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
