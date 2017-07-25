package com.cosmeticos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cosmeticos.model.Order;

/**
 * Created by matto on 17/06/2017.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findTop10ByOrderByDateDesc();

    List<Order> findByIdCustomer_idCustomer(Long idCustomer);


    List<Order> findByStatus(Order.Status status);
}
