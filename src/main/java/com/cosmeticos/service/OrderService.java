package com.cosmeticos.service;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.model.Order;
import com.cosmeticos.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Calendar;
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

    public Order create(OrderRequestBody orderRequest) {
        Order order = new Order();

        order.setScheduleId(orderRequest.getOrder().getScheduleId());
        order.setProfessionalServices(orderRequest.getOrder().getProfessionalServices());
        order.setIdLocation(orderRequest.getOrder().getIdLocation());
        order.setIdCustomer(orderRequest.getOrder().getIdCustomer());
        order.setDate(Calendar.getInstance().getTime());

        //O ID ORDER SERA DEFINIDO AUTOMATICAMENTE
        //order.setIdOrder(orderRequest.getOrder().getIdOrder());

        //O STATUS INICIAL SERA DEFINIDO COMO CRIADO
        order.setStatus(Order.Status.CREATED.ordinal());

        return orderRepository.save(order);
    }

    public Order update(OrderRequestBody request) {
        Order orderRequest = request.getOrder();
        Order order = orderRepository.findOne(orderRequest.getIdOrder());

        if(!StringUtils.isEmpty(orderRequest.getDate())) {
            order.setDate(orderRequest.getDate());
        }

        if(!StringUtils.isEmpty(orderRequest.getStatus())) {
            order.setStatus(orderRequest.getStatus());
        }

        if(!StringUtils.isEmpty(orderRequest.getIdCustomer())) {
            order.setIdCustomer(orderRequest.getIdCustomer());
        }

        if(!StringUtils.isEmpty(orderRequest.getIdLocation())) {
            order.setIdLocation(orderRequest.getIdLocation());
        }

        if(!StringUtils.isEmpty(orderRequest.getProfessionalServices())) {
            order.setProfessionalServices(orderRequest.getProfessionalServices());
        }

        if(!StringUtils.isEmpty(orderRequest.getScheduleId())) {
            order.setScheduleId(orderRequest.getScheduleId());
        }

        return orderRepository.save(order);
    }

    public String delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Order> find10Lastest() {
        return orderRepository.findTop10ByOrderByDateDesc();
        //return orderRepository.findAll();
    }
}
