package com.cosmeticos.repository;

import com.cosmeticos.model.Hability;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * Created by matto on 26/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HabilityRepositoryTests {

    @Autowired
    private HabilityRepository habilityRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    /**
     * Inicializa o H2 com dados iniciais.
     */
    @Before
    public void setupTests() {

        Hability h1 = new Hability();
        h1.setName("Escova Progressiva");
        h1.setService(serviceRepository.findOne(1L)); // Foi criado no PreLoad
        h1.getProfessionalCollection().add(professionalRepository.findOne(1L)); // Foi criado no PreLoad

        habilityRepository.save(h1);
    }

    @Test
    public void testFindByIdCategoryEscovaProgressiva() {
        Hability hability = habilityRepository.findByName("Escova Progressiva");

        Assert.assertNotNull(hability);
    }


}
