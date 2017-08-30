package com.cosmeticos.repository;

import com.cosmeticos.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by matto on 22/05/2017.
 */
@Transactional
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
