package com.cosmeticos.repository;

import com.cosmeticos.model.Schedule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleRepositoryTests {

	@Autowired
	private ScheduleRepository repository;

	/**
	 * Inicializa o H2 com dados iniciais.
	 */
	@Before
	public void setupTests()
	{
		Schedule s1 = new Schedule();
		s1.setScheduleId(1L);
		s1.setScheduleStart(Timestamp.valueOf(LocalDateTime.of(2017, 12, 31, 20, 0)));

		Schedule s2 = new Schedule();
		s2.setScheduleId(2L);
		s2.setScheduleStart(Timestamp.valueOf(LocalDateTime.of(2017, 12, 30, 20, 0)));

		Schedule s3 = new Schedule();
		s3.setScheduleId(3L);
		s3.setScheduleStart(Timestamp.valueOf(LocalDateTime.of(2017, 12, 29, 20, 0)));

		Schedule s4 = new Schedule();
		s4.setScheduleId(4L);
		s4.setScheduleStart(Timestamp.valueOf(LocalDateTime.of(2017, 12, 28, 20, 0)));

		Schedule s5 = new Schedule();
		s5.setScheduleId(5L);
		s5.setScheduleStart(Timestamp.valueOf(LocalDateTime.of(2017, 12, 27, 20, 0)));

		repository.save(s1);
		repository.save(s2);
		repository.save(s3);
		repository.save(s4);
		repository.save(s5);
	}

	@Test
	public void testSchedule1ActiveOnDecember31th() {
		Schedule schedule = repository.findOne(1L);

		Assert.assertNotNull(schedule);

		LocalDateTime ldt = LocalDateTime.ofInstant(schedule.getScheduleStart().toInstant(), ZoneId.systemDefault());

		// Confere se o Schedule que retornou foi o mesmo que foi inserido com id 1.
		Assert.assertEquals(31, ldt.getDayOfMonth());
		Assert.assertEquals(12, ldt.getMonth().getValue());
		Assert.assertEquals(20, ldt.getHour());
	}

	@Test
	public void testSchedule2ActiveOnDecember30th() {
		Schedule schedule = repository.findOne(2L);
		Assert.assertNotNull(schedule);

		LocalDateTime ldt = LocalDateTime.ofInstant(schedule.getScheduleStart().toInstant(), ZoneId.systemDefault());

		// Confere se o Schedule que retornou foi o mesmo que foi inserido com id 2.
		Assert.assertEquals(30, ldt.getDayOfMonth());
		Assert.assertEquals(12, ldt.getMonth().getValue());
		Assert.assertEquals(20, ldt.getHour());
	}

	@Test
	public void testSchedule3ActiveOnDecember29th() {
		Schedule schedule = repository.findOne(3L);
		Assert.assertNotNull(schedule);

		LocalDateTime ldt = LocalDateTime.ofInstant(schedule.getScheduleStart().toInstant(), ZoneId.systemDefault());

		// Confere se o Schedule que retornou foi o mesmo que foi inserido com id 3.
		Assert.assertEquals(29, ldt.getDayOfMonth());
		Assert.assertEquals(12, ldt.getMonth().getValue());
		Assert.assertEquals(20, ldt.getHour());
	}

	@Test
	public void testSchedule4ActiveOnDecember28th() {
		Schedule schedule = repository.findOne(4L);
		Assert.assertNotNull(schedule);

		LocalDateTime ldt = LocalDateTime.ofInstant(schedule.getScheduleStart().toInstant(), ZoneId.systemDefault());

		// Confere se o Schedule que retornou foi o mesmo que foi inserido com id 4.
		Assert.assertEquals(28, ldt.getDayOfMonth());
		Assert.assertEquals(12, ldt.getMonth().getValue());
		Assert.assertEquals(20, ldt.getHour());
	}

	@Test
	public void testSchedule5InactiveOnDecember27th() {
		Schedule schedule = repository.findOne(5L);
		Assert.assertNotNull(schedule);

		LocalDateTime ldt = LocalDateTime.ofInstant(schedule.getScheduleStart().toInstant(), ZoneId.systemDefault());

		// Confere se o Schedule que retornou foi o mesmo que foi inserido com id 5.
		Assert.assertEquals(27, ldt.getDayOfMonth());
		Assert.assertEquals(12, ldt.getMonth().getValue());
		Assert.assertEquals(20, ldt.getHour());
	}

}
