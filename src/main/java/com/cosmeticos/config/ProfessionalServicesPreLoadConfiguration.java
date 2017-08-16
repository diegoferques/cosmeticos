package com.cosmeticos.config;

import com.cosmeticos.model.Category;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ProfessionalServicesRepository;
import com.cosmeticos.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

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

    @PostConstruct
    public void insertInitialH2Data(){

        Professional p1 = professionalRepository.findOne(1L);        // Criamos o Usuario que nao existe no banco.
        Category s1 = serviceRepository.findOne(1L);

        ProfessionalServices ps1 = new ProfessionalServices(p1.getIdProfessional(), s1.getIdCategory());
        ps1.setProfessional(p1);
        ps1.setCategory(s1);

        p1.getProfessionalServicesCollection().add(ps1);

        professionalRepository.save(p1);

        /////////////////////////////////////////////////////////
        Professional p2 = professionalRepository.findOne(2L);        // Criamos o Usuario que nao existe no banco.
        Category s2 = serviceRepository.findOne(2L);

        ProfessionalServices ps2 = new ProfessionalServices(p2.getIdProfessional(), s2.getIdCategory());
        ps2.setProfessional(p2);
        ps2.setCategory(s2);

        p2.getProfessionalServicesCollection().add(ps2);

        professionalRepository.save(p2);

        /////////////////////////////////////////////////////////
        Professional p3 = professionalRepository.findOne(3L);        // Criamos o Usuario que nao existe no banco.
        Category s3 = serviceRepository.findOne(3L);

        ProfessionalServices ps3 = new ProfessionalServices(p3.getIdProfessional(), s3.getIdCategory());
        ps3.setProfessional(p3);
        ps3.setCategory(s3);

        p3.getProfessionalServicesCollection().add(ps3);

        professionalRepository.save(p3);

        /////////////////////////////////////////////////////////
        Professional p4 = professionalRepository.findOne(4L);        // Criamos o Usuario que nao existe no banco.
        Category s4 = serviceRepository.findOne(4L);

        ProfessionalServices ps4 = new ProfessionalServices(p4.getIdProfessional(), s4.getIdCategory());
        ps4.setProfessional(p4);
        ps4.setCategory(s4);

        p4.getProfessionalServicesCollection().add(ps4);

        professionalRepository.save(p4);

        /////////////////////////////////////////////////////////
        Professional p5 = professionalRepository.findOne(5L);        // Criamos o Usuario que nao existe no banco.
        Category s5 = serviceRepository.findOne(5L);

        ProfessionalServices ps5 = new ProfessionalServices(p5.getIdProfessional(), s5.getIdCategory());
        ps5.setProfessional(p5);
        ps5.setCategory(s5);

        p5.getProfessionalServicesCollection().add(ps5);

        professionalRepository.save(p5);
        

    }

}
