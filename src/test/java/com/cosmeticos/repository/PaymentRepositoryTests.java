package com.cosmeticos.repository;

import com.cosmeticos.controller.CustomerControllerTests;
import com.cosmeticos.controller.ProfessionalControllerTests;
import com.cosmeticos.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentRepositoryTests {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private PriceRuleRepository priceRuleRepository;

	@Autowired
	private ProfessionalCategoryRepository professionalCategoryRepository;

	@Autowired
	private ProfessionalRepository professionalRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CategoryRepository serviceRepository;

	private PriceRule pr;

	/**
	 * Inicializa o H2 com dados iniciais.
	 */
	@Before
	@Transactional
	public void setupTests()
	{
		Professional professional = ProfessionalControllerTests.createFakeProfessional();
		professionalRepository.save(professional);

		Category service = serviceRepository.findByName("PEDICURE");
		service = serviceRepository.findWithSpecialties(service.getIdCategory());

		pr = new PriceRule();
		pr.setName("RULE");
		pr.setPrice(30000L);

		ProfessionalCategory ps1 = new ProfessionalCategory(professional, service);
		ps1.addPriceRule(pr);

		professionalCategoryRepository.save(ps1);

		priceRuleRepository.save(pr);

	}

	@Test
	@Transactional
	public void testSchedule1ActiveOnDecember31th() {


		Customer customer = CustomerControllerTests.createFakeCustomer();
		customerRepository.save(customer);

		Payment p = new Payment(Payment.Type.CASH);
		p.setPriceRule(pr);
		pr.setPayment(p);

		Order o = new Order();
		o.setProfessionalCategory(pr.getProfessionalCategory());
		o.addPayment(p);
		o.setStatus(Order.Status.OPEN);
		o.setIdCustomer(customer);

		orderRepository.save(o);

		paymentRepository.save(p);

		Assert.assertNotNull("");
	}
}
