package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.controller.CustomerControllerTests;
import com.cosmeticos.controller.ProfessionalControllerTests;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OrderService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Vinicius on 15/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalServicesRepository professionalServicesRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private OrderService orderService;

    @Before
    public void setUp() throws Exception {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testaddwallet-cliente");
        c1.getUser().setEmail("testaddwallet-cliente@bol");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testaddwallet-professional");
        professional.getUser().setEmail("testaddwallet-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Service serviceProgramador = new Service();
        serviceProgramador.setCategory("PROGRAMADOR");
        serviceRepository.save(serviceProgramador);

        ProfessionalServices ps1 = new ProfessionalServices(professional, serviceProgramador);
        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);


        Order o3 = new Order();
        o3.setStatus(Order.Status.OPEN);
        o3.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 30, 0)));
        o3.setIdCustomer(c1);
        //o3.setIdLocation();
        o3.setProfessionalServices(ps1);
        //o3.setScheduleId(s1);
        orderRepository.save(o3);

        Order o4 = new Order();
        o4.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 40, 0)));
        o4.setIdCustomer(c1);
        //o4.setIdLocation();
        o4.setProfessionalServices(ps1);
        //o4.setScheduleId(s1);
        o4.setStatus(Order.Status.SEMI_CLOSED);

        orderRepository.save(o4);

        Order o5 = new Order();
        o5.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 50, 0)));
        o5.setIdCustomer(c1);
        //o5.setIdLocation();
        o5.setProfessionalServices(ps1);
        //o5.setScheduleId(s2);
        o5.setStatus(Order.Status.SEMI_CLOSED);
        orderRepository.save(o5);
                    }


    @Test
    public void testUpdateStatus(){

        // TODO: insere umas orders com finished by professional e nao confiar no OrderPreLoad

        List<Order> preUpdateallOrders = orderService.findBy(new Order());
        int c = 0;

        for (Order o: preUpdateallOrders) {
            if(o.getStatus() == Order.Status.SEMI_CLOSED){
                c++;
            }
        }

        Assert.assertTrue("PAra prosseguirmos com este teste, " +
                "devemos ter Orders no banco com status FINISHED_BY_PROFESSIONAL", c > 0);

        orderService.updateStatus();

        // assert: nenhuma order do banco pode estar finished_by_professional
        List<Order> allOrders = orderService.findBy(new Order());

        for (Order o: allOrders) {
            Assert.assertNotEquals(Order.Status.SEMI_CLOSED, o.getStatus());
        }
    }
}
