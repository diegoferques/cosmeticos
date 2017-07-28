package com.cosmeticos.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cosmeticos.Application;
import com.cosmeticos.model.Order;
import com.cosmeticos.model.Order.Status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OrderTest {

	@Autowired
	private OrderStatusHandler handler;
	
	private Order order;

	@Before
	public void setup() {
		order = new Order();
	}

	@Test
	public void testNullToOpen() {
		Assert.assertEquals(Status.OPEN, handler.handle(order, Status.OPEN));
	}
	
	
	@Test
	public void testOpenToAccepted() {
		order.setStatus(Status.OPEN);
		Assert.assertEquals(Status.ACCEPTED, handler.handle(order, Status.ACCEPTED));
	}
	
	@Test
	public void testOpenToScheduled() {
		order.setStatus(Status.OPEN);
		Assert.assertEquals(Status.SCHEDULED, handler.handle(order, Status.SCHEDULED));
	}
	
	@Test
	public void testOpenToCancelled() {
		order.setStatus(Status.OPEN);
		Assert.assertEquals(Status.CANCELLED, handler.handle(order, Status.CANCELLED));
	}
	
	@Test
	public void testAcceptedToInprogress() {
		order.setStatus(Status.ACCEPTED);
		Assert.assertEquals(Status.INPROGRESS, handler.handle(order, Status.INPROGRESS));
	}
	
	@Test
	public void testAcceptedToCancelled() {
		order.setStatus(Status.ACCEPTED);
		Assert.assertEquals(Status.CANCELLED, handler.handle(order, Status.CANCELLED));
	}
	
	@Test
	public void testScheduledToInprogress() {
		order.setStatus(Status.SCHEDULED);
		Assert.assertEquals(Status.INPROGRESS, handler.handle(order, Status.INPROGRESS));
	}
	
	
	@Test
	public void testScheduledToCanceled() {
		order.setStatus(Status.SCHEDULED);
		Assert.assertEquals(Status.CANCELLED, handler.handle(order, Status.CANCELLED));
	}
	
	@Test
	public void testInprogressToSemiclosed() {
		order.setStatus(Status.INPROGRESS);
		Assert.assertEquals(Status.SEMI_CLOSED, handler.handle(order, Status.SEMI_CLOSED));
	}
	
	
	@Test
	public void testInprogressToCancelled() {
		order.setStatus(Status.INPROGRESS);
		Assert.assertEquals(Status.CANCELLED, handler.handle(order, Status.CANCELLED));
	}
	
	@Test
	public void testSemiclosedToClosed() {
		order.setStatus(Status.SEMI_CLOSED);
		Assert.assertEquals(Status.CLOSED, handler.handle(order, Status.CLOSED));
	}
	
	@Test
	public void testFailOnOpenToClosed() {
		try {
			order.setStatus(Status.OPEN);
			Assert.assertEquals(Status.CLOSED, handler.handle(order, Status.CLOSED));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}

	@Test
	public void testFailOnCancelledToOpen() {
		try {
			order.setStatus(Status.CANCELLED);
			Assert.assertEquals(Status.OPEN, handler.handle(order, Status.OPEN));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}

	@Test
	public void testFailOnClosedToOpen() {
		try {
			order.setStatus(Status.CLOSED);
			Assert.assertEquals(Status.OPEN, handler.handle(order, Status.OPEN));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}
	
	@Test
	public void testFailOnScheduledToClosed() {
		try {
			order.setStatus(Status.SCHEDULED);
			Assert.assertEquals(Status.CLOSED, handler.handle(order, Status.CLOSED));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}
	
	@Test
	public void testFailOnInprogressToClosed() {
		try {
			order.setStatus(Status.INPROGRESS);
			Assert.assertEquals(Status.CLOSED, handler.handle(order, Status.CLOSED));
			Assert.fail("setStatus deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}

}
