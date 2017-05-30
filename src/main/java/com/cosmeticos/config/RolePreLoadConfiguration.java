package com.cosmeticos.config;

import com.cosmeticos.model.Role;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.repository.RoleRepository;
import com.cosmeticos.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
        Role s1 = new Role();
        s1.setName("ADMIN");

        Role s2 = new Role();
        s2.setName("CUSTOMER");

        Role s3 = new Role();
        s3.setName("PROFESSIONAL");

        Role s4 = new Role();
        s4.setName("CALLCENTER");

        Role s5 = new Role();
        s5.setName("CONTENTMANAGER");

        repository.save(s1);
        repository.save(s2);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);

    }
}
