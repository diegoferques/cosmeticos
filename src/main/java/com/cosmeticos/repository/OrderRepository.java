package com.cosmeticos.repository;

import com.cosmeticos.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by matto on 17/06/2017.
 */
public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findTop10ByOrderByDateDesc();

    List<Order> findAll();

    List<Order> findByIdCustomer_idCustomer(Long idCustomer);
}
