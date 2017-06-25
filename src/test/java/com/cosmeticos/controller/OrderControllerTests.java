package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created by matto on 24/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    //@Autowired
    //private ProfessionalServices professionalServices;
/*
    @Before
    public void setupTests() throws ParseException {
        User u1 = this.createFakeLogin();
        Address a1 = createFakeAddress();
        Customer c1 = this.createFakeCustomer(a1, u1);
        Professional p1 = this.createFakeProfessional(c1);
        Schedule s1 = this.createFakeSchedule();

        //ProfessionalServices ps1 = new ProfessionalServices();
        //ps1.setProfessional(p1);

        u1.setProfessional(p1);
        userRepository.save(u1);


        Order o1 = new Order();
        o1.setStatus(Order.Status.CREATED.ordinal());
        o1.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 10, 0)));
        o1.setIdCustomer(c1);
        //o1.setIdLocation();
        //o1.setProfessionalServices();
        o1.setScheduleId(s1);
        orderRepository.save(o1);

        Order o2 = new Order();
        o2.setStatus(Order.Status.CREATED.ordinal());
        o2.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 20, 0)));
        o2.setIdCustomer(c1);
        //o2.setIdLocation();
        //o2.setProfessionalServices();
        o2.setScheduleId(s1);
        orderRepository.save(o2);

        Order o3 = new Order();
        o3.setStatus(Order.Status.CREATED.ordinal());
        o3.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 30, 0)));
        o3.setIdCustomer(c1);
        //o3.setIdLocation();
        //o3.setProfessionalServices();
        o3.setScheduleId(s1);
        orderRepository.save(o3);

        Order o4 = new Order();
        o4.setStatus(Order.Status.CREATED.ordinal());
        o4.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 40, 0)));
        o4.setIdCustomer(c1);
        //o4.setIdLocation();
        //o4.setProfessionalServices();
        o4.setScheduleId(s1);
        orderRepository.save(o4);

        Order o5 = new Order();
        o5.setStatus(Order.Status.CREATED.ordinal());
        o5.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 50, 0)));
        o5.setIdCustomer(c1);
        //o5.setIdLocation();
        //o5.setProfessionalServices();
        o5.setScheduleId(s1);
        orderRepository.save(o5);
    }
*/
    @Test
    public void testStartOk() {
        Assert.assertEquals(1L,1L);
    }

    public User createFakeLogin() {
        User u = new User();
        u.setEmail("diego@bol.com");
        //u.setIdLogin(1234L);
        u.setPassword("123qwe");
        u.setSourceApp("google+");
        u.setUsername("diegoferques");
        //u.getCustomerCollection().add(c);
        userRepository.save(u);
        return u;
    }

    public Address createFakeAddress() {
        Address a = new Address();
        a.setAddress("Rua Perlita");
        a.setCep("0000000");
        a.setCity("RJO");
        a.setCountry("BRA");
        a.setNeighborhood("Austin");
        a.setState("RJ");
        //a.getCustomerCollection().add(customer);
        addressRepository.save(a);
        return a;
    }

    public Customer createFakeCustomer(Address address, User user) {
        Customer c1 = new Customer();
        c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
        c1.setCellPhone("(21) 98877-6655");
        c1.setCpf("098.765.432-10");
        c1.setDateRegister(Calendar.getInstance().getTime());
        c1.setGenre('M');
        c1.setNameCustomer("João da Silva");
        //c1.setOrderCollection(null);
        c1.setStatus(Customer.Status.ACTIVE.ordinal());
        c1.setIdAddress(address);
        c1.setIdLogin(user);

        customerRepository.save(c1);

        return c1;
    }

    private Schedule createFakeSchedule() {
        Schedule s1 = new Schedule();
        s1.setScheduleId(1L);
        s1.setScheduleDate(Timestamp.valueOf(LocalDateTime.of(2017, 12, 31, 20, 0)));
        s1.setStatus(Schedule.Status.ACTIVE);

        return s1;
    }

    private Professional createFakeProfessional(Customer customer) {
        Professional p1 = new Professional();
        p1.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1980, 01, 20, 0, 0, 0)));
        p1.setCellPhone("(21) 98877-6655");
        p1.setCnpj("098.765.432-10");
        p1.setDateRegister(Calendar.getInstance().getTime());
        p1.setGenre('M');
        p1.setNameProfessional("João da Silva");
        p1.setStatus(Professional.Status.ACTIVE);

        p1.setIdAddress(customer.getIdAddress());
        p1.setIdLogin(customer.getIdLogin());

        professionalRepository.save(p1);

        return p1;
    }
}
