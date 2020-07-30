package com.cosmeticos.service;

import com.cosmeticos.model.PriceRule;
import com.cosmeticos.model.ProfessionalCategory;
import com.cosmeticos.repository.PriceRuleRepository;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PriceRuleService {
    @Autowired
    private PriceRuleRepository priceRuleRepository;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    public Optional<PriceRule> find(Long id){
        return (priceRuleRepository.findById(id));
    }

    public void delete(Long id) {

        PriceRule i = priceRuleRepository.findById(id).get();

        ProfessionalCategory professionalCategory =
                professionalCategoryRepository.findById(
                        i.getProfessionalCategory().getProfessionalCategoryId()
                ).get();

        if(professionalCategory != null ) {

            professionalCategory.getPriceRuleList().remove(i);
            professionalCategoryRepository.save(professionalCategory);

           // priceRuleRepository.delete(id);
           // priceRuleRepository.flush();
        }
        else
        {
            throw new IllegalArgumentException(id + " nao possui ProfessionalCategory");
        }
    }

    public PriceRule create(PriceRule request) {
        return priceRuleRepository.save(request);
    }
}
