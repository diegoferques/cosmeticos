package com.cosmeticos.preload;

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

        Professional p1 = professionalRepository.findOne(1L);        // Criamos o Usuario que nao existe no banco.
        Category s1 = serviceRepository.findWithSpecialties(4L);

        ProfessionalCategory ps1 = new ProfessionalCategory(p1, s1);
        ps1.addPriceRule( new PriceRule("Cabelo curto", 10000L));
        ps1.addPriceRule( new PriceRule("Cabelo Medio", 15000L));
        ps1.addPriceRule( new PriceRule("Cabelo Longo", 20000L));

        professionalCategoryRepository.save(ps1);

        /////////////////////////////////////////////////////////

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
        //////////////// VINICIUS ///////////////////////////////
        addProfessionalCategoryAndPriceRule(
                4L,
                4L,
                new PriceRule("Cabelo curto", 10000L),
                new PriceRule("Cabelo Medio", 15000L),
                new PriceRule("Cabelo Longo", 20000L)
        );

        /////////////////////////////////////////////////////////
        ///////////////// HABIBI ////////////////////////////////
        addProfessionalCategoryAndPriceRule(
                5L,
                6L,
                new PriceRule("Shiatsu", 10000L),
                new PriceRule("Cervical", 15000L),
                new PriceRule("Relxante", 20000L)
        );

        /////////////////////////////////////////////////////////
        ///////////////// KELLY /////////////////////////////////
        addProfessionalCategoryAndPriceRule(
                6L,
                6L,
                new PriceRule("Relxante", 20000L)
        );

        /////////////////////////////////////////////////////////
        ///////////////// KDOBA /////////////////////////////////
        addProfessionalCategoryAndPriceRule(
                7L,
                2L,
                new PriceRule("Xampu A", 20000L),
                new PriceRule("Xampu B", 50000L)
        );

        /////////////////////////////////////////////////////////
        ///////////////// JOSE PAULINO //////////////////////////
        addProfessionalCategoryAndPriceRule(
                8L,
                3L,
                new PriceRule("Casamentos", 100000L),
                new PriceRule("15 Anos", 150000L),
                new PriceRule("Casual", 40000L)
        );
    }

    private void addProfessionalCategoryAndPriceRule(
            Long professionalId,
            Long categoryId,
            PriceRule... priceRules
    )
    {
        Professional p = professionalRepository.findOne(professionalId);
        Category s = serviceRepository.findWithSpecialties(categoryId);

        ProfessionalCategory ps = new ProfessionalCategory(p, s);

        for (PriceRule pr : priceRules) {
            ps.addPriceRule(pr);
        }

        professionalCategoryRepository.save(ps);
    }

}
