package com.cosmeticos.repository;

import com.cosmeticos.model.Role;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleRepositoryTests {

	@Autowired
	private RoleRepository repository;

	/**
	 * Inicializa o H2 com dados iniciais.
	 */
	@Before
	public void setupTests()
	{
		Role s1 = new Role();
		s1.setName("ADMIN");

		Role s2 = new Role();
		s2.setName("CUSTOMER");

		Role s3 = new Role();
		s3.setName("PROFESSIONAL");

		Role s4 = new Role();
		s4.setName("CALLCENTER");

		repository.save(s1);
		repository.save(s2);
		repository.save(s3);
		repository.save(s4);
	}

	@Test
	public void testFindRoleADMIN() {
		Role schedule = repository.findById(1L);
		Assert.assertNotNull(schedule);

		// Confere se o Role que retornou confere com a primeira Role inserida.
		Assert.assertEquals("ADMIN", schedule.getName());
	}

	@Test
	public void testFindRoleCUSTOMER() {
		Role schedule = repository.findById(2L);
		Assert.assertNotNull(schedule);

		// Confere se o Role que retornou confere com a primeira Role inserida.
		Assert.assertEquals("CUSTOMER", schedule.getName());
	}

	@Test
	public void testFindRolePROFESSIONAL() {
		Role schedule = repository.findById(3L);
		Assert.assertNotNull(schedule);

		// Confere se o Role que retornou confere com a primeira Role inserida.
		Assert.assertEquals("PROFESSIONAL", schedule.getName());
	}

	@Test
	public void testFindRoleCALLCENTER() {
		Role schedule = repository.findById(4L);
		Assert.assertNotNull(schedule);

		// Confere se o Role que retornou confere com a primeira Role inserida.
		Assert.assertEquals("CALLCENTER", schedule.getName());
	}

}
