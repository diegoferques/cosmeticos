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
		Role s1 = Role.builder().name("ADMIN").build();
		Role s2 = Role.builder().name("CUSTOMER").build();
		Role s3 = Role.builder().name("PROFESSIONAL").build();
		Role s4 = Role.builder().name("CALLCENTER").build();
		repository.save(s1);
		repository.save(s2);
		repository.save(s3);
		repository.save(s4);
	}

	@Test
	public void testFindRoleADMIN() {
		Role schedule = repository.findById(1L).get();
		Assert.assertNotNull(schedule);

		// Confere se o Role que retornou confere com a primeira Role inserida.
		Assert.assertEquals("ADMIN", schedule.getName());
	}

	@Test
	public void testFindRoleCUSTOMER() {
		Role schedule = repository.findById(2L).get();
		Assert.assertNotNull(schedule);

		// Confere se o Role que retornou confere com a primeira Role inserida.
		Assert.assertEquals("CUSTOMER", schedule.getName());
	}

	@Test
	public void testFindRolePROFESSIONAL() {
		Role schedule = repository.findById(3L).get();
		Assert.assertNotNull(schedule);

		// Confere se o Role que retornou confere com a primeira Role inserida.
		Assert.assertEquals("PROFESSIONAL", schedule.getName());
	}

	@Test
	public void testFindRoleCALLCENTER() {
		Role schedule = repository.findById(4L).get();
		Assert.assertNotNull(schedule);

		// Confere se o Role que retornou confere com a primeira Role inserida.
		Assert.assertEquals("CALLCENTER", schedule.getName());
	}

}
