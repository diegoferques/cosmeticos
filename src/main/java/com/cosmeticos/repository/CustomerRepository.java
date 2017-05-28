package com.cosmeticos.repository;

import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by matto on 22/05/2017.
 */
@Transactional
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
