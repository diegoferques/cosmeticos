package com.cosmeticos.repository;

import com.cosmeticos.model.ProfessionalCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Transactional
public interface ProfessionalCategoryRepository extends JpaRepository<ProfessionalCategory, Long> {

    @Query("SELECT ps FROM" +
            " ProfessionalCategory ps" +
            " WHERE " +
            "ps.professional.status = 'ACTIVE' " +
            "AND ps.category.name = ?1 " +
            "AND ps.priceRuleList IS NOT EMPTY"
    )
    List<ProfessionalCategory> findByPriceRuleNotNullAndService(String categoryName);

    @Query("SELECT ps FROM" +
            " ProfessionalCategory ps" +
            " WHERE " +
            "ps.professional.status = 'ACTIVE' " +
            "AND ps.category.name = ?1 " +
            "AND ps.priceRuleList IS NOT EMPTY " +
            "AND ps.professional.attendance = 0"
    )
    List<ProfessionalCategory> findByPriceRuleNotNullAndServiceAndHomecare(String categoryName);
}
