package com.cosmeticos.config;

import com.cosmeticos.model.Professional;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.model.Service;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.ProfessionalRepository;
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

        Professional p2 = professionalRepository.findOne(2L);        // Criamos o Usuario que nao existe no banco.
        Service s2 = serviceRepository.findOne(2L);

        Professional p3 = professionalRepository.findOne(3L);        // Criamos o Usuario que nao existe no banco.
        Service s3 = serviceRepository.findOne(3L);

        ProfessionalServices ps1 = new ProfessionalServices(p1.getIdProfessional(), s1.getIdService());
        ps1.setProfessional(p1);
        ps1.setService(s1);

        p1.getProfessionalServicesCollection().add(ps1);

        professionalRepository.save(p1);
    }

}
