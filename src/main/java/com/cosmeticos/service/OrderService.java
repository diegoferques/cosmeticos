package com.cosmeticos.service;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.model.Sale;
import com.cosmeticos.model.Sale;
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

    public Optional<Sale> find(Long idOrder) {
        return Optional.of(orderRepository.findOne(idOrder));
    }

    public Sale create(OrderRequestBody orderRequest) {
        Sale sale = new Sale();

        sale.setScheduleId(orderRequest.getSale().getScheduleId());
        sale.setProfessionalServices(orderRequest.getSale().getProfessionalServices());
        sale.setIdLocation(orderRequest.getSale().getIdLocation());
        sale.setIdCustomer(orderRequest.getSale().getIdCustomer());
        sale.setDate(Calendar.getInstance().getTime());

        //O ID ORDER SERA DEFINIDO AUTOMATICAMENTE
        //order.setIdOrder(orderRequest.getOrder().getIdOrder());

        //O STATUS INICIAL SERA DEFINIDO COMO CRIADO
        sale.setStatus(Sale.Status.CREATED.ordinal());

        return orderRepository.save(sale);
    }

    public Sale update(OrderRequestBody request) {
        Sale saleRequest = request.getSale();
        Sale sale = orderRepository.findOne(saleRequest.getIdOrder());

        if(!StringUtils.isEmpty(saleRequest.getDate())) {
            sale.setDate(saleRequest.getDate());
        }

        if(!StringUtils.isEmpty(saleRequest.getStatus())) {
            sale.setStatus(saleRequest.getStatus());
        }

        if(!StringUtils.isEmpty(saleRequest.getIdCustomer())) {
            sale.setIdCustomer(saleRequest.getIdCustomer());
        }

        if(!StringUtils.isEmpty(saleRequest.getIdLocation())) {
            sale.setIdLocation(saleRequest.getIdLocation());
        }

        if(!StringUtils.isEmpty(saleRequest.getProfessionalServices())) {
            sale.setProfessionalServices(saleRequest.getProfessionalServices());
        }

        if(!StringUtils.isEmpty(saleRequest.getScheduleId())) {
            sale.setScheduleId(saleRequest.getScheduleId());
        }

        return orderRepository.save(sale);
    }

    public String delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Sale> find10Lastest() {
        return orderRepository.findTop10ByOrderByDateDesc();
        //return orderRepository.findAll();
    }
}
