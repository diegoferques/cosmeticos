package com.cosmeticos.config;

import com.cosmeticos.model.*;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by matto on 24/06/2017.
 */

@DependsOn({"professionalServicesPreLoadConfiguration"})
@Configuration
@Profile("default")
public class OrderPreLoadConfiguration {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    //@Autowired
    // ProfessionalServicesRepository professionalServicesRepository;

    @PostConstruct
    public void insertInitialH2Data(){

        //User u1 = userRepository.findOne(1L);
        //Address a1 = addressRepository.findOne(1L);
        Customer c1 = customerRepository.findOne(1L);
        Professional p1 = professionalRepository.findOne(1L);
        Schedule s1 = scheduleRepository.findOne(1L);
        Schedule s2 = scheduleRepository.findOne(2L);
        ProfessionalServices ps1 = new ProfessionalServices(1L, 1L);
        //professionalServicesRepository.save(ps1);

        //ps1.setProfessional(p1);

        //u1.setProfessional(p1);
        //userRepository.save(u1);

        Order o1 = new Order();
        o1.setStatus(Order.Status.CREATED.ordinal());
        o1.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 10, 0)));
        o1.setIdCustomer(c1);
        //o1.setIdLocation();
        o1.setProfessionalServices(p1.getProfessionalServicesCollection().iterator().next());
        //o1.setScheduleId(s1);
        orderRepository.save(o1);

        Order o2 = new Order();
        o2.setStatus(Order.Status.CREATED.ordinal());
        o2.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 20, 0)));
        o2.setIdCustomer(c1);
        //o2.setIdLocation();
        o2.setProfessionalServices(ps1);
        //o2.setScheduleId(s1);
        orderRepository.save(o2);

        Order o3 = new Order();
        o3.setStatus(Order.Status.CREATED.ordinal());
        o3.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 30, 0)));
        o3.setIdCustomer(c1);
        //o3.setIdLocation();
        o3.setProfessionalServices(ps1);
        //o3.setScheduleId(s1);
        orderRepository.save(o3);

        Order o4 = new Order();
        o4.setStatus(Order.Status.CREATED.ordinal());
        o4.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 40, 0)));
        o4.setIdCustomer(c1);
        //o4.setIdLocation();
        o4.setProfessionalServices(ps1);
        o4.setScheduleId(s1);
        o4.setStatus(Order.Status.FINISHED_BY_PROFESSIONAL.ordinal());

        orderRepository.save(o4);

        Order o5 = new Order();
        o5.setStatus(Order.Status.CREATED.ordinal());
        o5.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 50, 0)));
        o5.setIdCustomer(c1);
        //o5.setIdLocation();
        o5.setProfessionalServices(ps1);
        o5.setScheduleId(s2);
        o5.setStatus(Order.Status.FINISHED_BY_PROFESSIONAL.ordinal());
        orderRepository.save(o5);

        //Scheduled Order
        Order o6 = new Order();
        o6.setStatus(Order.Status.CREATED.ordinal());
        o6.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 02, 22, 40, 0)));
        o6.setIdCustomer(c1);
        //o6.setIdLocation();
        o6.setProfessionalServices(ps1);
        o6.setScheduleId(s1);
        orderRepository.save(o6);

        //Scheduled Order
        Order o7 = new Order();
        o7.setStatus(Order.Status.CREATED.ordinal());
        o7.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 02, 22, 50, 0)));
        o7.setIdCustomer(c1);
        //o5.setIdLocation();
        o7.setProfessionalServices(ps1);
        o7.setScheduleId(s2);
        orderRepository.save(o7);

    }
}
