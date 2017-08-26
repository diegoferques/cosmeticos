package com.cosmeticos.config;

import com.cosmeticos.model.Category;
import com.cosmeticos.model.PriceRule;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalCategory;
import com.cosmeticos.repository.CategoryRepository;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
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
public class ProfessionalCategoryPreLoadConfiguration {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Transactional
    @PostConstruct
    public void insertInitialH2Data(){

        PriceRule pr1 = new PriceRule();
        pr1.setName("Cabelo curto");
        pr1.setPrice(10000L);

        Professional p1 = professionalRepository.findOne(1L);        // Criamos o Usuario que nao existe no banco.
        Category s1 = serviceRepository.findWithSpecialties(1L);

        ProfessionalCategory ps1 = new ProfessionalCategory(p1, s1);
        pr1.setProfessionalCategory(ps1);
//      ps1.getPriceRule().add(pr1);

        professionalCategoryRepository.save(ps1);

        /////////////////////////////////////////////////////////

        PriceRule pr2 = new PriceRule();
        pr2.setName("Cabelo curto");
        pr2.setPrice(10000L);

        Professional p2 = professionalRepository.findOne(2L);        // Criamos o Usuario que nao existe no banco.
        Category s2 = serviceRepository.findWithSpecialties(2L);

        ProfessionalCategory ps2 = new ProfessionalCategory(p2, s2);

        professionalCategoryRepository.save(ps2);

        /////////////////////////////////////////////////////////
        Professional p3 = professionalRepository.findOne(3L);        // Criamos o Usuario que nao existe no banco.
        Category s3 = serviceRepository.findWithSpecialties(3L);

        ProfessionalCategory ps3 = new ProfessionalCategory(p3, s3);

        professionalCategoryRepository.save(ps3);

        /////////////////////////////////////////////////////////
        Professional p4 = professionalRepository.findOne(4L);        // Criamos o Usuario que nao existe no banco.
        Category s4 = serviceRepository.findWithSpecialties(4L);

        ProfessionalCategory ps4 = new ProfessionalCategory(p4, s4);

        professionalCategoryRepository.save(ps4);

        /////////////////////////////////////////////////////////
        Professional p5 = professionalRepository.findOne(5L);        // Criamos o Usuario que nao existe no banco.
        Category s5 = serviceRepository.findWithSpecialties(5L);

        ProfessionalCategory ps5 = new ProfessionalCategory(p5, s5);

        professionalCategoryRepository.save(ps5);
    }

}
