package com.cosmeticos.repository;

import com.cosmeticos.model.ProfessionalCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Transactional
public interface ProfessionalCategoryRepository extends JpaRepository<ProfessionalCategory, Long> {

	//Set<ProfessionalServices> findByServiceIdService(Long idCategory);
}
