package com.cosmeticos.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cosmeticos.model.Order;
import com.cosmeticos.model.Order.Status;

/**
 * Created by matto on 17/06/2017.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findTop10ByOrderByDateDesc();

    List<Order> findByIdCustomer_idCustomer(Long idCustomer);


    List<Order> findByStatus(Order.Status status);

    List<Order> findByProfessionalServices_Professional_idProfessional(Long idProfessional);

    List<Order> findByStatusOrStatusAndProfessionalServices_Professional_idProfessional(
            Order.Status s1, Order.Status s2, Long idProfessional);

    @Query(value = "" +
            "SELECT o " +
            "FROM Order o " +
            "join fetch o.professionalServices ps " +
            "join fetch ps.professional p " +
            "WHERE p.idProfessional = ?1 " +
            "AND  o.status in( 7, 8 )")
    List<Order> findByProfessionalServices_Professional_idProfessionalAndStatusOrStatus(
            Long idProfessional);
    
    /*
    @Query(value = "SELECT * FROM " +
            "Order o " +
            "WHERE o.Status NOT LIKE 'CANCELLED' OR " +
            "o.Status NOT LIKE 'CLOSED'",
            nativeQuery = true)
    */
    //@Query("SELECT o FROM Order o WHERE o.Status NOT LIKE 'CANCELLED'")
    //List<Order> findAllCustom();

    //@Query("status:*?0* OR description:*?0*")
    //@Query("Status NOT LIKE 'CANCELLED'")
    //List<Order> findByQueryAnnotation();

    //List<Order> findByStatusNotLike(Order.Status s1);
    //List<Order> findByStatusNotLike(String s1);
    //List<Order> findByStatusNotLike(int s1);
    List<Order> findByStatusNotIn(Collection<Status> status);

	List<Order> findByStatusNotInAndIdCustomer_IdCustomer(Collection<Status> status,
			Long idCustomer);
}
