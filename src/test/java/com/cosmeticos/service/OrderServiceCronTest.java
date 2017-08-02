package com.cosmeticos.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cosmeticos.Application;

/**
 * Created by Vinicius on 15/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceCronTest {

    @Value("${order.unfinished.cron}")
    private String cron;

    @Test
    public void test() {
        Assert.assertEquals("0 0/10 * * * ? ", cron);
    }
}
