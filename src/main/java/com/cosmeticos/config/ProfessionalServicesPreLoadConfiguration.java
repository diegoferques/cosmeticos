package com.cosmeticos.config;

import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.model.Service;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ProfessionalServicesRepository;
import com.cosmeticos.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * Created by Vinicius on 20/06/2017.
 */
@Configuration
@DependsOn({"professionalPreLoadConfiguration", "servicePreLoadConfiguration"})
@Profile("default")
public class ProfessionalServicesPreLoadConfiguration {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProfessionalServicesRepository professionalServicesRepository;

    @PostConstruct
    public void insertInitialH2Data(){

        Professional p1 = professionalRepository.findOne(1L);        // Criamos o Usuario que nao existe no banco.
        Service s1 = serviceRepository.findOne(1L);

        ProfessionalServices ps1 = new ProfessionalServices(p1.getIdProfessional(), s1.getIdService());
        ps1.setProfessional(p1);
        ps1.setService(s1);

        p1.getProfessionalServicesCollection().add(ps1);

        professionalRepository.save(p1);

        /////////////////////////////////////////////////////////
        Professional p2 = professionalRepository.findOne(2L);        // Criamos o Usuario que nao existe no banco.
        Service s2 = serviceRepository.findOne(2L);

        ProfessionalServices ps2 = new ProfessionalServices(p2.getIdProfessional(), s2.getIdService());
        ps2.setProfessional(p2);
        ps2.setService(s2);

        p2.getProfessionalServicesCollection().add(ps2);

        professionalRepository.save(p2);

        /////////////////////////////////////////////////////////
        Professional p3 = professionalRepository.findOne(3L);        // Criamos o Usuario que nao existe no banco.
        Service s3 = serviceRepository.findOne(3L);

        ProfessionalServices ps3 = new ProfessionalServices(p3.getIdProfessional(), s3.getIdService());
        ps3.setProfessional(p3);
        ps3.setService(s3);

        p3.getProfessionalServicesCollection().add(ps3);

        professionalRepository.save(p3);

        /////////////////////////////////////////////////////////
        Professional p4 = professionalRepository.findOne(4L);        // Criamos o Usuario que nao existe no banco.
        Service s4 = serviceRepository.findOne(4L);

        ProfessionalServices ps4 = new ProfessionalServices(p4.getIdProfessional(), s4.getIdService());
        ps4.setProfessional(p4);
        ps4.setService(s4);

        p4.getProfessionalServicesCollection().add(ps4);

        professionalRepository.save(p4);

        /////////////////////////////////////////////////////////
        Professional p5 = professionalRepository.findOne(5L);        // Criamos o Usuario que nao existe no banco.
        Service s5 = serviceRepository.findOne(5L);

        ProfessionalServices ps5 = new ProfessionalServices(p5.getIdProfessional(), s5.getIdService());
        ps5.setProfessional(p5);
        ps5.setService(s5);

        p5.getProfessionalServicesCollection().add(ps5);

        professionalRepository.save(p5);
        

    }

}
