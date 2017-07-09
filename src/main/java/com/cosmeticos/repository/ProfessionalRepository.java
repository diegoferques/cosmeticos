package com.cosmeticos.repository;

import com.cosmeticos.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by matto on 22/05/2017.
 */
@Transactional
public interface ProfessionalRepository extends
        JpaRepository<Professional, Long>
{
    List<Professional> findTop10ByOrderByDateRegisterDesc();
}
