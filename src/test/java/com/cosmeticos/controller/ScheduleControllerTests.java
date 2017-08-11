package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.CategoryRepository;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScheduleControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProfessionalRepository professionalRepository;

	@Autowired
	private CategoryRepository serviceRepository;

}
