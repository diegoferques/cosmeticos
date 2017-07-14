package com.cosmeticos.repository;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.model.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.ParseException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by matto on 25/06/2017.
 */
@Ignore // Nao tem nenhum teste implementado aqui
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderRepositoryTests {

    //@Autowired
    //private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Collection<Customer> customerCollection;

   @Before
    public void setupTests() throws ParseException{


        customerCollection = new ArrayList<Customer>();

        User u = new User();

        Customer client1 = new Customer();
        client1.setNameCustomer("Client");


        customerRepository.save(client1);
       System.out.println(client1);

    }

}
