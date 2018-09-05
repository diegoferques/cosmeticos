package com.cosmeticos.preload;

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

        /////////////////////////////////////////////////////
        // CATEGORIA CABELEIREIRO ///////////////////////////
        /////////////////////////////////////////////////////
        Category ownerCategory = new Category();
        ownerCategory.setName("CABELEIREIRO");
//////////////////
        Category sub1 = new Category();
        sub1.setName("ESCOVA");
        sub1.setImageUrl("cabeleireiro");

        ownerCategory.addChild(sub1);
//////////////////
        Category s2 = new Category();
        s2.setName("HIDRATAÇÃO");
        s2.setImageUrl("cabeleireiro");

        ownerCategory.addChild(s2);
/////////////////
        Category s3 = new Category();
        s3.setName("PENTEADO");
        s3.setImageUrl("cabeleireiro");

        ownerCategory.addChild(s3);
/////////////////


        /////////////////////////////////////////////////////
        // CATEGORIA ESTETICA ///////////////////////////////
        /////////////////////////////////////////////////////
        Category s4 = new Category();
        s4.setName("DEPILAÇÃO");
        s4.setImageUrl("barba");

        Category s5 = new Category();
        s5.setName("MASSAGEM");

        Category categEstetica = new Category();
        categEstetica.setName("ESTETICA");
        categEstetica.setImageUrl("spa");
        categEstetica.addChild(s4);
        categEstetica.addChild(s5);

        repository.saveAndFlush(ownerCategory);
        repository.saveAndFlush(categEstetica);
    }
}
