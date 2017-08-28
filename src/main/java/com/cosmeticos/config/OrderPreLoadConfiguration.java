package com.cosmeticos.config;

import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by matto on 24/06/2017.
 */

@DependsOn({"professionalCategoryPreLoadConfiguration"})
@Configuration
@Profile("default")
public class OrderPreLoadConfiguration {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @PostConstruct
    public void insertInitialH2Data() throws URISyntaxException {

        //User u1 = userRepository.findOne(1L);
        //Address a1 = addressRepository.findOne(1L);
        Customer c1 = customerRepository.findOne(1L);
        Professional p1 = professionalRepository.findOne(1L);
        //Schedule s1 = scheduleRepository.findOne(1L);
        //Schedule s2 = scheduleRepository.findOne(2L);

        PriceRule pr1 = new PriceRule();
        pr1.setName("Cabelo curto");
        pr1.setPrice(10000L);

        Category service = new Category();
        service.setName("PEDICURE");
        serviceRepository.save(service);

        ProfessionalCategory ps1 = new ProfessionalCategory(p1, service);
        pr1.setProfessionalCategory(ps1);

        //Atualizando associando o Profeissional ao Servico
        professionalCategoryRepository.save(ps1);
        professionalRepository.save(p1);

        Schedule s1 = new Schedule();
        s1.setScheduleStart(Timestamp.valueOf(LocalDateTime.now()));

        Schedule s2 = new Schedule();
        s2.setScheduleStart(Timestamp.valueOf(LocalDateTime.now()));

        CreditCard creditCard = new CreditCard();
        creditCard.setLastUsage(Timestamp.valueOf(LocalDateTime.now()));

        Order o1 = new Order();
        o1.setStatus(Order.Status.OPEN);
        o1.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o1.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o1.setIdCustomer(c1);
        o1.addPayment(new Payment(Payment.Type.CASH));
        //o1.setIdLocation();
        o1.setProfessionalCategory(p1.getProfessionalCategoryCollection().iterator().next());

        //o1.setScheduleId(s1);
        orderRepository.save(o1);

        Order o2 = new Order();
        o2.setStatus(Order.Status.OPEN);
        o2.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o2.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o2.setIdCustomer(c1);
        o2.addPayment(new Payment(Payment.Type.CASH));

        //o2.setIdLocation();
        o2.setProfessionalCategory(ps1);
        //o2.setScheduleId(s1);
        orderRepository.save(o2);

        Order o3 = new Order();
        o3.setStatus(Order.Status.OPEN);
        o3.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o3.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o3.setIdCustomer(c1);
        o3.addPayment(new Payment(Payment.Type.CASH));

        //o3.setIdLocation();
        o3.setProfessionalCategory(ps1);
        //o3.setScheduleId(s1);
        orderRepository.save(o3);

        Order o4 = new Order();
        o4.setStatus(Order.Status.OPEN);
        o4.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o4.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o4.setIdCustomer(c1);
        o4.addPayment(new Payment(Payment.Type.CASH));

        //o4.setIdLocation();
        o4.setProfessionalCategory(ps1);
        //o4.setScheduleId(s1);
        o4.setStatus(Order.Status.SEMI_CLOSED);

        orderRepository.save(o4);

        Order o5 = new Order();
        o5.setStatus(Order.Status.OPEN);
        o5.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o5.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o5.setIdCustomer(c1);
        //o5.setIdLocation();
        o5.addPayment(new Payment(Payment.Type.CASH));

        o5.setProfessionalCategory(ps1);
        //o5.setScheduleId(s2);
        o5.setStatus(Order.Status.SEMI_CLOSED);
        orderRepository.save(o5);

        //Scheduled Order
        Order o6 = new Order();
        o6.setStatus(Order.Status.OPEN);
        o6.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o6.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o6.setIdCustomer(c1);
        o6.addPayment(new Payment(Payment.Type.CASH));

        //o6.setIdLocation();
        o6.setProfessionalCategory(ps1);
        o6.setScheduleId(s1);
        orderRepository.save(o6);

        //Scheduled Order
        Order o7 = new Order();
        o7.setStatus(Order.Status.OPEN);
        o7.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o7.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o7.setIdCustomer(c1);
        o7.addPayment(new Payment(Payment.Type.CASH));

        //o5.setIdLocation();
        o7.setProfessionalCategory(ps1);
        o7.setScheduleId(s2);
        orderRepository.save(o7);

        //Scheduled Order
        Order o8 = new Order();
        o8.setStatus(Order.Status.INPROGRESS);
        o8.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o8.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o8.setIdCustomer(c1);
        o8.addPayment(new Payment(Payment.Type.CASH));
        o8.setProfessionalCategory(ps1);

        Schedule s8 = new Schedule();
        s8.setScheduleStart(Timestamp.valueOf(LocalDateTime.now().plusHours(5)));
        s8.setScheduleEnd(Timestamp.valueOf(LocalDateTime.now().plusHours(7)));// 2 horas de trabalho.
        o8.setScheduleId(s8);

        orderRepository.save(o8);

        //Scheduled Order
        Order o9 = new Order();
        o9.setStatus(Order.Status.SCHEDULED);
        o9.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o9.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o9.setIdCustomer(c1);
        o9.addPayment(new Payment(Payment.Type.CASH));
        o9.setProfessionalCategory(ps1);

        Schedule s9 = new Schedule();
        s9.setScheduleStart(Timestamp.valueOf(LocalDateTime.now().plusHours(8)));
        s9.setScheduleStart(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30))); // meia hora de trabalho.
        o9.setScheduleId(s9);
        orderRepository.save(o9);

        //Scheduled Order
        Order o10 = new Order();
        o10.setStatus(Order.Status.SCHEDULED);
        o10.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o10.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o10.setIdCustomer(c1);
        o10.addPayment(new Payment(Payment.Type.CASH));
        o10.setProfessionalCategory(ps1);

        Schedule s10 = new Schedule();
        s10.setScheduleStart(Timestamp.valueOf(LocalDateTime.now().plusDays(2)));
        s10.setScheduleStart(Timestamp.valueOf(LocalDateTime.now().plusHours(4)));//4h de servico
        o10.setScheduleId(s10);
        orderRepository.save(o10);

    }
}
