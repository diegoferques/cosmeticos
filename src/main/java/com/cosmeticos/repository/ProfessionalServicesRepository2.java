package com.cosmeticos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.cosmeticos.model.ProfessionalServices2;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Transactional
public interface ProfessionalServicesRepository2 extends JpaRepository<ProfessionalServices2, Long> {

	//Set<ProfessionalServices> findByServiceIdService(Long idCategory);
}
