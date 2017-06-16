package com.cosmeticos.repository;

import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Professional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by matto on 22/05/2017.
 */
@Transactional
public interface ProfessionalRepository extends CrudRepository<Professional, Long> {
    List<Professional> findTop10ByOrderByDateRegisterDesc();
}
