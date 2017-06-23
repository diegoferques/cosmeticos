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
        s1.setIdAddress(new Address());
        s1.setIdLogin(new User("Garry", "123qwe", "Garry@gmail.com"));

        Professional s2 = new Professional();
        s2.setNameProfessional("Diego");
        s2.setIdAddress(new Address());
        s2.setIdLogin(new User("Diego", "123qwe", "Diego@gmail.com"));

        Professional s3 = new Professional();
        s3.setNameProfessional("Deivison");
        s3.setIdAddress(new Address());
        s3.setIdLogin(new User("Deivison", "123qwe", "Deivison@gmail.com"));

        Professional s4 = new Professional();
        s4.setNameProfessional("Vinicius");
        s4.setIdAddress(new Address());
        s4.setIdLogin(new User("Vinicius", "123qwe", "Vinicius@gmail.com"));

        Professional s5 = new Professional();
        s5.setNameProfessional("Habib");
        s5.setIdAddress(new Address());
        s5.setIdLogin(new User("habib", "123qwe", "habibi@gmail.com"));

        repository.save(s1);
        repository.save(s2);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);

    }
}
