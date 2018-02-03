package com.cosmeticos.repository;

import com.cosmeticos.model.Order;
import com.cosmeticos.model.Order.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by matto on 17/06/2017.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findTop10ByOrderByDateDesc();

    List<Order> findByIdCustomer_idCustomer(Long idCustomer);


    List<Order> findByStatus(Order.Status status);


    List<Order> findByProfessionalCategory_Professional_idProfessional(Long idProfessional);

    List<Order> findByStatusOrStatusAndProfessionalCategory_Professional_idProfessional(
            Order.Status s1, Order.Status s2, Long idProfessional);

    /**
     * PEga Orders abertas por um cliente a um mesmo profissional em um mesmo servico.
     * @param idProfessional
     * @param iCustomer
     * @param idOrder
     * @param idCategory
     * @return
     */
    @Query(value = "" +
            "SELECT o " +
            "FROM Order o " +
            "join fetch o.professionalCategory ps " +
            "join fetch o.idCustomer cm " +
            "join fetch ps.professional p " +
            "join fetch ps.category cg " +
            "WHERE p.idProfessional = ?1 " +
            "AND cm.idCustomer = ?2 " +
            "AND o.idOrder != ?3 " +
            "AND cg.idCategory = ?4 " +
            "AND  o.status in('OPEN', 'INPROGRESS', 'ACCEPTED' )")
    List<Order> findOpenedDuplicatedOrders(
            Long idProfessional, Long iCustomer, Long idOrder, Long idCategory
    );

    @Query(value = "" +
            "SELECT o " +
            "FROM Order o " +
            "join fetch o.professionalCategory ps " +
            "join fetch ps.professional p " +
            "WHERE p.idProfessional = ?1 " +
            "AND o.idOrder != ?2 " +
            "AND  o.status in( 'INPROGRESS', 'ACCEPTED' )")
    List<Order> findRunningOrdersByProfessional(
            Long idProfessional, Long idOrder);

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

	List<Order> findByStatusNotInAndIdCustomer_user_email(Collection<Status> status,
			String email);

    @Query(value = "" +
            "SELECT o " +
            "FROM Order o " +
            "join fetch o.professionalCategory ps " +
            "join fetch ps.professional p " +
            "WHERE p.idProfessional = ?1 " +
            "AND  o.status in( 'INPROGRESS', 'ACCEPTED' )")
    List<Order> findByProfessionalCategory_Professional_idProfessionalAndDateOrDate(
            Long idProfessional);

    @Query(value = "" +
            "SELECT o " +
            "FROM Order o " +
            "join fetch o.professionalCategory ps " +
            "WHERE ps.professionalCategoryId = ?1 " +
            "AND  o.status in( 'SCHEDULED', 'INPROGRESS' )")
    List<Order> findScheduledOrdersByProfessionalCategory(Long professionalCategoryId);


    @Query(value = "" +
            "SELECT o " +
            "FROM Order o " +
            "join fetch o.professionalCategory ps " +
            "join fetch ps.professional p " +
            "WHERE p.idProfessional = ?1 " +
            "AND  o.status in( 'SCHEDULED', 'INPROGRESS' ) " +
            "AND ?2 between o.scheduleId.scheduleStart and o.scheduleId.scheduleEnd")
    List<Order> findScheduledOrdersByProfessionalWithScheduleConflict(Long idProfessional, Date pretendedStart);

    @Query(value = "" +
            "SELECT o " +
            "FROM Order o " +
            "join fetch o.professionalCategory ps " +
            "WHERE ps.professional.user.email = ?1 "

            // Vamos retornar tudo pro app e deixar o app decidir como exibira os pedidos.
            //+ "AND  o.status in( 'SCHEDULED', 'INPROGRESS', 'ACCEPTED', 'OPEN')"
    )
    List<Order> findByProfessionalEmail(String email);



}
