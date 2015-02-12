package org.ei.drishti.repository.it;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.ei.drishti.util.Matcher.hasSameFieldsAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.List;

import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.Person;
import org.ei.drishti.repository.AllPersons;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllPersonsIntegrationTest {
	@Autowired
	private AllPersons allPersons;

	@Before
	public void setUp() throws Exception {
		allPersons.removeAll();
	}

	@Test
	public void shouldRegisterAPerson() {
		HashMap<String, String> details = new HashMap<>();
		details.put("some_field", "some_value");
		Person person = new Person("CASE-1").withName("Person name")
				.withDob("2004-01-07").withSex("M")
				.withAddress("Some place in Dhaka")
				.withContactNumber("1234567").withHeight(160)
				.withAnm("ANM ID 1")
				.withLocation("bherya", "Sub Center", "PHC X");

		allPersons.add(person);

		List<Person> allThePersons = allPersons.getAll();
		assertThat(allThePersons.size(), is(1));

		Person personFromDB = allThePersons.get(0);
		assertThat(personFromDB, hasSameFieldsAs(person));
	}

}
