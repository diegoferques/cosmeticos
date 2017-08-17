package com.cosmeticos.repository;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.cosmeticos.controller.ProfessionalControllerTests;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.model.Service;

/**
 * Created by matto on 26/05/2017.
 */
@ActiveProfiles("treta")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfessionalServiceRepositoryTests {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private ProfessionalServicesRepository professionalServicesRepository;

    @Before
    public void setupTests() {

    }
    
	@org.springframework.transaction.annotation.Transactional
    @Test
    public void autoIncrementTest()
    {

        Professional p = ProfessionalControllerTests.createFakeProfessional();
        professionalRepository.save(p);
        
        Service s = new Service();
        s.setCategory("cat");
        s.setProfessionalServicesCollection(new HashSet<>());
        serviceRepository.save(s);
        
        
        ProfessionalServices ps1 = new ProfessionalServices(p, s);

        professionalServicesRepository.save(ps1);
        
        
        /////////// Inserindo outro

        Service s2 = new Service();
        s2.setCategory("cat2");
        s.setProfessionalServicesCollection(new HashSet<>());
        
        serviceRepository.save(s2);

        ProfessionalServices ps2= new ProfessionalServices(p, s2);

        professionalServicesRepository.save(ps2);

        org.junit.Assert.assertEquals(Long.valueOf(2), ps2.getProfessionalServicesId());
        org.junit.Assert.assertEquals(2, p.getProfessionalServicesCollection().size());
    }   
}
