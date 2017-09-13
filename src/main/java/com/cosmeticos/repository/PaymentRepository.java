package com.cosmeticos.repository;

import com.cosmeticos.model.Payment;
import com.cosmeticos.payment.Charger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by matto on 26/08/2017.
 */
@Transactional
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    List<Payment> findByStatus(Payment.Status status);

    Payment findByOrder_idOrder(Long idOrder);

    @Query(value = "" +
            "SELECT p " +
            "FROM Payment p " +
            "WHERE p.status " +
            "NOT IN (1, 2, 5)")
    List<Payment> findFailedPayments();


}
