package com.cosmeticos.subscription.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * Created by Vinicius on 17/07/2017.
 */
@Configuration
@Profile("default")
public class ProductPreLoadConfiguration {

    @Autowired
    private ProductRepository productRepository;

    @PostConstruct

    public void insertInitialH2Data(){

        Product p1 = new Product();
        p1.setNameProduct("Professional 1");
        p1.setDescriptionProduct("Taxa de cadastro e taxa à cada 3 prestações de serviço do profissioanl.");

        productRepository.save(p1);

    }
}
