package com.cosmeticos.config;

import com.cosmeticos.model.Category;
import com.cosmeticos.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * Created by Vinicius on 31/05/2017.
 */
@Configuration
@Profile("default")
public class CategoryPreLoadConfiguration {

    @Autowired
    private CategoryRepository repository;

    @PostConstruct
    public void insertInitialH2Data(){

        Category ownerCategory = new Category();
        ownerCategory.setName("CABELEIREIRA");
//////////////////
        Category s1 = new Category();
        s1.setName("ESCOVA");

        ownerCategory.addChild(s1);
//////////////////
        Category s2 = new Category();
        s2.setName("HIDRATAÇÃO");

        ownerCategory.addChild(s2);
/////////////////
        Category s3 = new Category();
        s3.setName("PENTEADO");

        Category s4 = new Category();
        s4.setName("DEPILAÇÃO");

         Category s5 = new Category();
        s5.setName("MASSAGEM");

        repository.saveAndFlush(ownerCategory);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);
    }
}
