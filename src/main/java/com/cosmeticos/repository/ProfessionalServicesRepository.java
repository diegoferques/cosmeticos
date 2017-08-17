package com.cosmeticos.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.cosmeticos.model.ProfessionalServices;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Transactional
public interface ProfessionalServicesRepository extends JpaRepository<ProfessionalServices, Long> {

	//Set<ProfessionalServices> findByServiceIdService(Long idCategory);
}
