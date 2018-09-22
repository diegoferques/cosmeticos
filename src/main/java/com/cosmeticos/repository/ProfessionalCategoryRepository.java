package com.cosmeticos.repository;

import com.cosmeticos.model.ProfessionalCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * TODO: esta query deve ser indexada ou movida pra elasticsearch
     * @param queryString
     * @return
     */
    @Query("SELECT DISTINCT ps " +
            "FROM ProfessionalCategory ps " +
            "   JOIN ps.professional p " +
            "   JOIN ps.category c " +
            "   JOIN ps.priceRuleList pr " +
            "WHERE " +
            "   p.status = 'ACTIVE' " +
            "   AND (" +
            "       upper(c.name) like upper(?1) " + // Buscamos categorias com nome que bata com a queryString
            "       OR upper(pr.name) like upper(?1)" +  // ou categorias que possuam preco com a descricao que bata com a queryString
            "       OR upper(p.nameProfessional) like upper(?1)" + // ou categorias que possuam nome de profissional que bata com a queryString
            "   )"
    )
    List<ProfessionalCategory> search(String queryString);
}
