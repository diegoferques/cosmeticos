package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.model.Order;
import com.cosmeticos.service.OrderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by Vinicius on 15/07/2017.
 */
@ActiveProfiles("production")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceCronProductionTest {

    @Value("${order.unfinished.cron}")
    private String cron;

    @Test
    public void test() {
        Assert.assertEquals("0 0 9 * * ? ", cron);
    }
}
