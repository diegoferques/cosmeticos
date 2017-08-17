package com.cosmeticos.repository;

import com.cosmeticos.model.Service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Vinicius on 31/05/2017.
 */
public interface ServiceRepository extends CrudRepository<Service, Long> {

    Service findByCategory(String category);
    

    @Query(value = "" +
            "SELECT s " +
            "FROM Service s " +
            "left join fetch s.professionalServicesCollection ps " +
            "WHERE s.idService = ?1")
    Service findWithSpecialties(Long idService);
}
