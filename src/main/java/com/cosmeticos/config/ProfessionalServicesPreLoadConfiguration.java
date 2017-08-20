package com.cosmeticos.config;

import com.cosmeticos.model.Category;
import com.cosmeticos.model.PriceRule;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.repository.CategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ProfessionalServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by Vinicius on 20/06/2017.
 */
@Configuration
@DependsOn({"professionalPreLoadConfiguration", "categoryPreLoadConfiguration"})
@Profile("default")
public class ProfessionalServicesPreLoadConfiguration {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private ProfessionalServicesRepository professionalServicesRepository;

    @Transactional
    @PostConstruct
    public void insertInitialH2Data(){

        PriceRule pr1 = new PriceRule();
        pr1.setName("Cabelo curto");
        pr1.setPrice(10000L);

        PriceRule pr2 = new PriceRule();
        pr2.setName("Cabelo Medio");
        pr2.setPrice(15000L);

        PriceRule pr3 = new PriceRule();
        pr3.setName("Cabelo Longo");
        pr3.setPrice(20000L);

        Professional p1 = professionalRepository.findOne(1L);        // Criamos o Usuario que nao existe no banco.
        Category s1 = serviceRepository.findWithSpecialties(1L);

        ProfessionalServices ps1 = new ProfessionalServices(p1, s1);
        ps1.addPriceRule(pr1);
        ps1.addPriceRule(pr2);
        ps1.addPriceRule(pr3);


        professionalRepository.save(p1);

        /////////////////////////////////////////////////////////
        Professional p2 = professionalRepository.findOne(2L);        // Criamos o Usuario que nao existe no banco.
        Category s2 = serviceRepository.findWithSpecialties(2L);

        ProfessionalServices ps2 = new ProfessionalServices(p2, s2);

        professionalServicesRepository.save(ps2);

        /////////////////////////////////////////////////////////
        Professional p3 = professionalRepository.findOne(3L);        // Criamos o Usuario que nao existe no banco.
        Category s3 = serviceRepository.findWithSpecialties(3L);

        ProfessionalServices ps3 = new ProfessionalServices(p3, s3);

        professionalServicesRepository.save(ps3);

        /////////////////////////////////////////////////////////
        Professional p4 = professionalRepository.findOne(4L);        // Criamos o Usuario que nao existe no banco.
        Category s4 = serviceRepository.findWithSpecialties(4L);

        ProfessionalServices ps4 = new ProfessionalServices(p4, s4);
        ps4.addPriceRule(pr1);
        ps4.addPriceRule(pr2);
        ps4.addPriceRule(pr3);
        
        professionalServicesRepository.save(ps4);

        /////////////////////////////////////////////////////////
        Professional p5 = professionalRepository.findOne(5L);        // Criamos o Usuario que nao existe no banco.
        Category s5 = serviceRepository.findWithSpecialties(5L);

        ProfessionalServices ps5 = new ProfessionalServices(p5, s5);

        professionalServicesRepository.save(ps5);
    }

}
