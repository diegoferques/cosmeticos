package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.model.Order;
import com.cosmeticos.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class FirebasePushNotifierServiceTest {

    @Autowired
    FirebasePushNotifierService firebasePushNotifierService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void pushIntegrationTest() throws Exception {

        Order one = orderRepository.findById(1L);


        // Este token muda com frequencia, portanto, eh interessante por um @IfProfileValue neste metodo de teste.
        one.getIdCustomer().getUser().setFirebaseInstanceId("cXQCO2tSTU4:APA91bGxwobj5U7Z2UK_mD3NJY3l8Ydo5mEagTcI700JG-bQxOUQGuR2-nkH2jVKhxJVZ8KdP3Wd4bviQs1rVK9V_2zJubwYYPjKIUYQ4i2rKLkKF_nuzQUbORGgudOZzrg-q_0hqd36");

        firebasePushNotifierService.push(one);
    }
}
