package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.controller.CustomerControllerTests;
import com.cosmeticos.controller.ProfessionalControllerTests;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.Exception;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private OrderService orderService;

    Order o3;
    Order o4;
    Order o5;

    @Before
    public void setUp() throws Exception {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testUpdateStatus-cliente");
        c1.getUser().setEmail("testUpdateStatus-cliente@bol");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testUpdateStatus-professional");
        professional.getUser().setEmail("testUpdateStatus-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Category serviceProgramador = new Category();
        serviceProgramador.setName("PROGRAMADOR");
        serviceRepository.save(serviceProgramador);

        PriceRule priceRule3 = new PriceRule("payment3", 2000);
        PriceRule priceRule4 = new PriceRule("payment4", 4000);
        PriceRule priceRule5 = new PriceRule("payment5", 5000);

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, serviceProgramador);
        ps1.addPriceRule(priceRule3);
        ps1.addPriceRule(priceRule4);
        ps1.addPriceRule(priceRule5);

        professionalCategoryRepository.save(ps1);




        Payment payment3 = new Payment(Payment.Type.CASH);

        priceRule3.addPayment(payment3);

        o3 = new Order();
        o3.setStatus(Order.Status.OPEN);
        o3.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 30, 0)));
        o3.setLastStatusUpdate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 30, 0)));
        o3.setIdCustomer(c1);
        o3.setAttendanceType(Order.AttendanceType.HOME_CARE);
        //o3.setIdLocation();
        o3.setProfessionalCategory(ps1);
        o3.addPayment(payment3);
        //o3.setScheduleId(s1);
        orderRepository.save(o3);



        Payment payment4 = new Payment(Payment.Type.CASH);

        priceRule4.addPayment(payment4);

        LocalDateTime ldt1 = LocalDateTime.now();
        ldt1.minusDays(3);
        o4 = new Order();
        o4.setDate(Timestamp.valueOf(ldt1));
        o4.setLastStatusUpdate(Timestamp.valueOf(ldt1.minusDays(3)));
        o4.setIdCustomer(c1);
        o4.setAttendanceType(Order.AttendanceType.HOME_CARE);
        //o4.setIdLocation();
        o4.setProfessionalCategory(ps1);
        //o4.setScheduleId(s1);
        o4.setStatus(Order.Status.SEMI_CLOSED);
        o4.addPayment(payment4);

        orderRepository.save(o4);



        Payment payment5 = new Payment(Payment.Type.CASH);

        priceRule5.addPayment(payment5);

        LocalDateTime ldt2 = LocalDateTime.now();
        ldt2.minusDays(8);
        o5 = new Order();
        o5.setDate(Timestamp.valueOf(ldt2));
        o5.setIdCustomer(c1);
        o5.setLastStatusUpdate(Timestamp.valueOf(ldt2.minusDays(8)));
        //o5.setIdLocation();
        o5.setProfessionalCategory(ps1);
        o5.setAttendanceType(Order.AttendanceType.HOME_CARE);
        //o5.setScheduleId(s2);
        o5.setStatus(Order.Status.SEMI_CLOSED);
        o5.addPayment(payment5);
        orderRepository.save(o5);
    }


    @Test
    public void testUpdateStatus(){

        orderService.scheduledUpdateStatus();
        o3 = orderRepository.findById(o3.getIdOrder());
        o4 = orderRepository.findById(o4.getIdOrder());
        o5 = orderRepository.findById(o5.getIdOrder());

        Assert.assertEquals(Order.Status.SEMI_CLOSED, o4.getStatus());

        Assert.assertEquals(Order.Status.AUTO_CLOSED, o5.getStatus());
        Assert.assertEquals(Order.Status.OPEN, o3.getStatus());
    }
}
