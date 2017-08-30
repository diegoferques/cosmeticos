package com.cosmeticos.repository;

import com.cosmeticos.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Vinicius on 31/05/2017.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);

    @Query(value = "" +
            "SELECT s " +
            "FROM Category s " +
            "left join fetch s.professionalCategoryCollection ps " +
            "WHERE s.idCategory = ?1")
    Category findWithSpecialties(Long idCategory);
}
