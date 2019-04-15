package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.model.Order;
import com.cosmeticos.model.Payment;
import com.cosmeticos.model.PriceRule;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.PointRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.cosmeticos.service.OrderTest.buildFakeOrder;
import static java.util.Collections.addAll;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PointServiceTest {

    @Rule
    public TestName testName = new TestName();

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Test
    public void testProfessionalNoCentsPriceIncreasingAsExactlySamePoints() {

        Payment p = new Payment(Payment.Type.CASH, new PriceRule(testName.getMethodName(), 4000));

        Order order = buildFakeOrder();
        addAll(order.getPaymentCollection(), p);

        pointService.increase(order);

        Long userId = order.getProfessionalCategory().getProfessional().getUser().getIdLogin();

        Long pointsSum = pointRepository.sumByUserId(userId);

        assertThat(pointsSum).isEqualTo(40);
    }

    @Test
    public void testCustomerNoCentsPriceIncreasingAsExactlySamePoints() {

        Payment p = new Payment(Payment.Type.CASH, new PriceRule(testName.getMethodName(), 4000));

        Order order = buildFakeOrder();
        addAll(order.getPaymentCollection(), p);

        pointService.increase(order);

        Long userId = order.getIdCustomer().getUser().getIdLogin();

        Long pointsSum = pointRepository.sumByUserId(userId);

        assertThat(pointsSum).isEqualTo(40);
    }

    @Test
    public void testProfessionalWithCentsPriceIncreasingAsRoundedUpPoints() {

        Payment p = new Payment(Payment.Type.CC, new PriceRule(testName.getMethodName(), 4015));// R$ 40,15

        Order order = buildFakeOrder();
        addAll(order.getPaymentCollection(), p);

        pointService.increase(order);

        Long userId = order.getProfessionalCategory().getProfessional().getUser().getIdLogin();

        Long pointsSum = pointRepository.sumByUserId(userId);

        assertThat(pointsSum).isEqualTo(41);
    }

    @Test
    public void testCustomerWithCentsPriceIncreasingAsRoundedUpPoints() {

        Payment p = new Payment(Payment.Type.CC, new PriceRule(testName.getMethodName(), 4015));// R$ 40,15

        Order order = buildFakeOrder();
        addAll(order.getPaymentCollection(), p);

        pointService.increase(order);

        Long userId = order.getIdCustomer().getUser().getIdLogin();

        Long pointsSum = pointRepository.sumByUserId(userId);

        assertThat(pointsSum).isEqualTo(41);
    }

    @Test
    public void testCustomerDecreasingPoints() {

        Payment p = new Payment(Payment.Type.CC, new PriceRule(testName.getMethodName(), 4015));// R$ 40,15

        Order order = buildFakeOrder();
        addAll(order.getPaymentCollection(), p);

        // Neste ponto, os usuarios de Professional e Customer possuem 41 pontos (R$ 40,15 eh arredondado pra R$ 41,00).
        pointService.increase(order);

        User user = order.getProfessionalCategory().getProfessional().getUser();

        // Neste ponto, apenas os pontos do Customer sao decrementados.
        pointService.decrease(user, 5L);

        Long userId = user.getIdLogin();

        Long pointsSum = pointRepository.sumByUserId(userId);

        assertThat(pointsSum).isEqualTo(36);
    }

    @Test
    public void testProfessionalDecreasingPoints() {

        Payment p = new Payment(Payment.Type.CC, new PriceRule(testName.getMethodName(), 4015));// R$ 40,15

        Order order = buildFakeOrder();
        addAll(order.getPaymentCollection(), p);

        // Neste ponto, os usuarios de Professional e Customer possuem 41 pontos (R$ 40,15 eh arredondado pra R$ 41,00).
        pointService.increase(order);

        User customerUser = order.getIdCustomer().getUser();

        // Neste ponto, apenas os pontos do Customer sao decrementados.
        pointService.decrease(customerUser, 5L);

        Long customerId = customerUser.getIdLogin();

        Long customerPointsSum = pointRepository.sumByUserId(customerId);

        assertThat(customerPointsSum).isEqualTo(36);
    }
}
