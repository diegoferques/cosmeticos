package com.cosmeticos.repository;

import com.cosmeticos.model.OrderStatus;
import com.cosmeticos.model.Payment;
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

    /**
     *
     * https://stackoverflow.com/a/36540371/3810036 Diz que payment nao pode ser opcional. Mas payment eh opciional, pq
     * a pricerule eh criada pelo profissional no momento do cadastro,e no cadastro nao ha payment.
     * @return
     */
    /*@Query(value = "" +
            "SELECT p " +
            "FROM Payment p " +
            "JOIN p.priceRule " +
            "WHERE p.id = ?1")
    Payment findWithPriceRule(Long paymentId);*/

    @Query(value = "" +
            "SELECT p " +
            "FROM Payment p " +
            "WHERE p.status " +
            "NOT IN (1, 2, 5)")
    List<Payment> findFailedPayments();

    List<Payment> findByOrderStatus(OrderStatus orderStatus);
}
