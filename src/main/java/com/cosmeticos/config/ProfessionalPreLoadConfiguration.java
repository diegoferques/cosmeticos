package com.cosmeticos.config;

import com.cosmeticos.model.Address;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.ProfessionalRepository;
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

    @PostConstruct
    public void insertInitialH2Data()
    {
        Professional s1 = new Professional();
        s1.setNameProfessional("Garry");
        s1.setAddress(new Address());
        s1.setUser(new User("garry", "123qwe", "garry@bol"));

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

        repository.save(s1);
        repository.save(s2);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);

    }
}
