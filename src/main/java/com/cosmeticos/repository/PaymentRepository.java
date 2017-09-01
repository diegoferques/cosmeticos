package com.cosmeticos.repository;

import com.cosmeticos.model.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by matto on 26/08/2017.
 */
@Transactional
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    List<Payment> findByStatus(Payment.Status status);

}
