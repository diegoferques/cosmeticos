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
import java.util.ArrayList;
import java.util.List;

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
    private CustomerRepository customerRepository;

    @Autowired
    ProfessionalServicesRepository professionalServicesRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @PostConstruct
    public void insertInitialH2Data() throws URISyntaxException {

        //User u1 = userRepository.findOne(1L);
        //Address a1 = addressRepository.findOne(1L);
        Customer c1 = customerRepository.findOne(1L);
        Professional p1 = professionalRepository.findOne(1L);
        //Schedule s1 = scheduleRepository.findOne(1L);
        //Schedule s2 = scheduleRepository.findOne(2L);

        Service service = new Service();
        service.setCategory("PEDICURE");
        serviceRepository.save(service);

        ProfessionalServices ps1 = new ProfessionalServices(p1, service);

        p1.getProfessionalServicesCollection().add(ps1);

        //Atualizando associando o Profeissional ao Servico
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
        //o1.setIdLocation();
        o1.setProfessionalServices(p1.getProfessionalServicesCollection().iterator().next());

        //o1.setScheduleId(s1);
        orderRepository.save(o1);

        Order o2 = new Order();
        o2.setStatus(Order.Status.OPEN);
        o2.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o2.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o2.setIdCustomer(c1);
        //o2.setIdLocation();
        o2.setProfessionalServices(ps1);
        //o2.setScheduleId(s1);
        orderRepository.save(o2);

        Order o3 = new Order();
        o3.setStatus(Order.Status.OPEN);
        o3.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o3.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o3.setIdCustomer(c1);
        //o3.setIdLocation();
        o3.setProfessionalServices(ps1);
        //o3.setScheduleId(s1);
        orderRepository.save(o3);

        Order o4 = new Order();
        o4.setStatus(Order.Status.OPEN);
        o4.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o4.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o4.setIdCustomer(c1);
        //o4.setIdLocation();
        o4.setProfessionalServices(ps1);
        //o4.setScheduleId(s1);
        o4.setStatus(Order.Status.SEMI_CLOSED);

        orderRepository.save(o4);

        Order o5 = new Order();
        o5.setStatus(Order.Status.OPEN);
        o5.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o5.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o5.setIdCustomer(c1);
        //o5.setIdLocation();
        o5.setProfessionalServices(ps1);
        //o5.setScheduleId(s2);
        o5.setStatus(Order.Status.SEMI_CLOSED);
        orderRepository.save(o5);

        //Scheduled Order
        Order o6 = new Order();
        o6.setStatus(Order.Status.OPEN);
        o6.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o6.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o6.setIdCustomer(c1);
        //o6.setIdLocation();
        o6.setProfessionalServices(ps1);
        o6.setScheduleId(s1);
        orderRepository.save(o6);

        //Scheduled Order
        Order o7 = new Order();
        o7.setStatus(Order.Status.OPEN);
        o7.setDate(Timestamp.valueOf(LocalDateTime.now()));
        o7.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        o7.setIdCustomer(c1);
        //o5.setIdLocation();
        o7.setProfessionalServices(ps1);
        o7.setScheduleId(s2);
        orderRepository.save(o7);

    }
}
