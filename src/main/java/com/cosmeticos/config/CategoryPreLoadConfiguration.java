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
        repository.saveAndFlush(ownerCategory);
//////////////////
        Category s1 = new Category();
        s1.setName("ESCOVA");

        s1.setOwnerCategory(ownerCategory);
//////////////////
        Category s2 = new Category();
        s2.setName("HIDRATAÇÃO");

        s2.setOwnerCategory(ownerCategory);
/////////////////
        Category s3 = new Category();
        s3.setName("PENTEADO");

        Category s4 = new Category();
        s4.setName("DEPILAÇÃO");

         Category s5 = new Category();
        s5.setName("MASSAGEM");

        repository.save(s1);
        repository.save(s2);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);
    }
}
