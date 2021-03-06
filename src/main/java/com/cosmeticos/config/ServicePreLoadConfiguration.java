package com.cosmeticos.config;

import com.cosmeticos.model.Service;
import com.cosmeticos.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * Created by Vinicius on 31/05/2017.
 */
@Configuration
@Profile("default")
public class ServicePreLoadConfiguration {

    @Autowired
    private ServiceRepository repository;

    @PostConstruct
    public void insertInitialH2Data(){

        Service s1 = new Service();
        s1.setCategory("BRUSH");

        Service s2 = new Service();
        s2.setCategory("HYDRATION");

        Service s3 = new Service();
        s3.setCategory("HAIRSTYLE");

        Service s4 = new Service();
        s4.setCategory("HAIR REMOVAL");

        Service s5 = new Service();
        s5.setCategory("MASSAGE");

        repository.save(s1);
        repository.save(s2);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);
    }
}
