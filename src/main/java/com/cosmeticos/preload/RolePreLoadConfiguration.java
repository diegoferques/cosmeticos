package com.cosmeticos.preload;

import com.cosmeticos.model.Role;
import com.cosmeticos.repository.RoleRepository;
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
public class RolePreLoadConfiguration {

    @Autowired
    private RoleRepository repository;

    @PostConstruct
    public void insertInitialH2Data()
    {
        Role s1 = Role.builder().name("ADMIN").build();
        Role s2 = Role.builder().name("CUSTOMER").build();
        Role s3 = Role.builder().name("PROFESSIONAL").build();
        Role s4 = Role.builder().name("CALLCENTER").build();
        Role s5 = Role.builder().name("CONTENTMANAGER").build();

        repository.save(s1);
        repository.save(s2);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);
    }
}
