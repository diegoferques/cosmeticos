package com.cosmeticos.service;

import com.cosmeticos.model.Order;
import com.cosmeticos.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 17/06/2017.
 */
@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public Optional<Order> find(Long idOrder) {
        return Optional.of(orderRepository.findOne(idOrder));
    }

    public String create() {
        return "Create não implementado";
    }

    public String update() {
        return "Update não implementado";
    }

    public String delete() {
        return "Update não implementado";
    }

    public List<Order> find10Lastest() {
        return orderRepository.findTop10ByidOrderDesc();
    }
}
