package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.model.Order;
import com.cosmeticos.model.OrderStatus;
import com.cosmeticos.service.order.OrderStatusValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OrderTest {

	@Autowired
	private OrderStatusValidator validator;
	
	private Order order;

	@Before
	public void setup() {
		order = new Order();
	}

	@Test
	public void testNullToOpen() {
		Assert.assertEquals(OrderStatus.OPEN, validator.validate(order, OrderStatus.OPEN));
	}
	
	
	@Test
	public void testOpenToAccepted() {
		order.setStatus(OrderStatus.OPEN);
		Assert.assertEquals(OrderStatus.ACCEPTED, validator.validate(order, OrderStatus.ACCEPTED));
	}
	
	@Test
	public void testOpenToScheduled() {
		order.setStatus(OrderStatus.OPEN);
		Assert.assertEquals(OrderStatus.SCHEDULED, validator.validate(order, OrderStatus.SCHEDULED));
	}
	
	@Test
	public void testOpenToCancelled() {
		order.setStatus(OrderStatus.OPEN);
		Assert.assertEquals(OrderStatus.CANCELLED, validator.validate(order, OrderStatus.CANCELLED));
	}

	@Test
	public void testOpenToExpired() {
		order.setStatus(OrderStatus.OPEN);
		Assert.assertEquals(OrderStatus.EXPIRED, validator.validate(order, OrderStatus.EXPIRED));
	}
	
	@Test
	public void testAcceptedToInprogress() {
		order.setStatus(OrderStatus.ACCEPTED);
		Assert.assertEquals(OrderStatus.INPROGRESS, validator.validate(order, OrderStatus.INPROGRESS));
	}
	
	@Test
	public void testAcceptedToCancelled() {
		order.setStatus(OrderStatus.ACCEPTED);
		Assert.assertEquals(OrderStatus.CANCELLED, validator.validate(order, OrderStatus.CANCELLED));
	}
	
	@Test
	public void testScheduledToInprogress() {
		order.setStatus(OrderStatus.SCHEDULED);
		Assert.assertEquals(OrderStatus.INPROGRESS, validator.validate(order, OrderStatus.INPROGRESS));
	}
	
	
	@Test
	public void testScheduledToCanceled() {
		order.setStatus(OrderStatus.SCHEDULED);
		Assert.assertEquals(OrderStatus.CANCELLED, validator.validate(order, OrderStatus.CANCELLED));
	}
	
	@Test
	public void testInprogressToSemiclosed() {
		order.setStatus(OrderStatus.INPROGRESS);
		Assert.assertEquals(OrderStatus.SEMI_CLOSED, validator.validate(order, OrderStatus.SEMI_CLOSED));
	}
	
	
	@Test
	public void testInprogressToCancelled() {
		order.setStatus(OrderStatus.INPROGRESS);
		Assert.assertEquals(OrderStatus.CANCELLED, validator.validate(order, OrderStatus.CANCELLED));
	}
	
	@Test
	public void testSemiclosedToClosed() {
		order.setStatus(OrderStatus.SEMI_CLOSED);
		Assert.assertEquals(OrderStatus.CLOSED, validator.validate(order, OrderStatus.CLOSED));
	}
	
	@Test
	public void testFailOnOpenToClosed() {
		try {
			order.setStatus(OrderStatus.OPEN);
			Assert.assertEquals(OrderStatus.CLOSED, validator.validate(order, OrderStatus.CLOSED));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}

	@Test
	public void testFailOnCancelledToOpen() {
		try {
			order.setStatus(OrderStatus.CANCELLED);
			Assert.assertEquals(OrderStatus.OPEN, validator.validate(order, OrderStatus.OPEN));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}

	@Test
	public void testFailOnExpiredToOpen() {
		try {
			order.setStatus(OrderStatus.EXPIRED);
			Assert.assertEquals(OrderStatus.OPEN, validator.validate(order, OrderStatus.OPEN));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}

	@Test
	public void testFailOnClosedToOpen() {
		try {
			order.setStatus(OrderStatus.CLOSED);
			Assert.assertEquals(OrderStatus.OPEN, validator.validate(order, OrderStatus.OPEN));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}
	
	@Test
	public void testFailOnScheduledToClosed() {
		try {
			order.setStatus(OrderStatus.SCHEDULED);
			Assert.assertEquals(OrderStatus.CLOSED, validator.validate(order, OrderStatus.CLOSED));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}
	
	@Test
	public void testFailOnInprogressToClosed() {
		try {
			order.setStatus(OrderStatus.INPROGRESS);
			Assert.assertEquals(OrderStatus.CLOSED, validator.validate(order, OrderStatus.CLOSED));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}

}
