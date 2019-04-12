package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.controller.CustomerControllerTests;
import com.cosmeticos.controller.ProfessionalControllerTests;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.PointRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.Collections.addAll;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PointServiceTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Test
    public void testNoCentsPriceIncreasingAsExactlySamePoints() {

        PriceRule pr = new PriceRule("testNoCentsPriceIncreasingAsExactlySamePoints", 4000);

        Payment p = new Payment(Payment.Type.CASH);
        p.setPriceRule(pr);


        Professional fakeProfessional = ProfessionalControllerTests.createFakeProfessional();
        Category fakeCategory = new Category();

        Order order = new Order();

        addAll(order.getPaymentCollection(), p);
        order.setProfessionalCategory(new ProfessionalCategory(fakeProfessional, fakeCategory));

        Point increased = pointService.increase(order);

        assertThat(increased.getValue()).isEqualTo(40);
    }

    @Test
    public void testWithCentsPriceIncreasingAsRoundedUpPoints() {

        PriceRule pr = new PriceRule("testWithCentsPriceIncreasingAsRoundedUpPoints", 4015);// R$ 40,15

        Payment p = new Payment(Payment.Type.CC);
        p.setPriceRule(pr);


        Professional fakeProfessional = ProfessionalControllerTests.createFakeProfessional();
        Category fakeCategory = new Category();

        Order order = new Order();

        addAll(order.getPaymentCollection(), p);
        order.setProfessionalCategory(new ProfessionalCategory(fakeProfessional, fakeCategory));

        Point increased = pointService.increase(order);

        assertThat(increased.getValue()).isEqualTo(41);
    }

    @Test
    public void testDecreasingPoints() {

        PriceRule pr = new PriceRule("testWithCentsPriceIncreasingAsRoundedUpPoints", 4015);// R$ 40,15

        Payment p = new Payment(Payment.Type.CC);
        p.setPriceRule(pr);
        Professional fakeProfessional = ProfessionalControllerTests.createFakeProfessional();
        fakeProfessional.getUser().setIdLogin(12345670000000L);
        Category fakeCategory = new Category();

        Order order = new Order();

        addAll(order.getPaymentCollection(), p);
        order.setProfessionalCategory(new ProfessionalCategory(fakeProfessional, fakeCategory));

        // Neste ponto, o usuario possui 41 pontos (R$ 40,15 eh arredondado pra R$ 41,00).
        pointService.increase(order);

        Long remainingBalance = pointService.decrease(fakeProfessional.getUser(), 5L);

        assertThat(remainingBalance).isEqualTo(36);
    }
}
