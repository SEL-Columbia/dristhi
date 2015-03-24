package org.opensrp.repository.it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.common.AllConstants;
import org.opensrp.repository.AllBaseEntities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllBaseEntitiesIntegrationTest {

	@Autowired
	@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR)
	CouchDbConnector db;

	@Autowired
	private AllBaseEntities allBaseEntities;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	public void shouldAddBaseEntity() throws Exception {

	/*	Map<String, String> addressFields = new HashMap<String, String>();
		addressFields.put("addressfields",
				"Vill: Gaya Bari, Ward No : #3, HouseHoldId: 123456");

		List<Address> addresses = new ArrayList<Address>();
		addresses.add(new Address("Permanent", new Date(), new Date(),
				addressFields, "70.5", "40.5", "6300", "", "Bangladesh"));

		Map<String, Object> attributes = new HashMap<String, Object>();
		
		attributes.put("attr1", "value1");
		attributes.put("attr2", "value2");

		
				
	//	DateTime birthDate = new DateTime().withYear(1982).withMonthOfYear(03).withDayOfMonth(25);
		 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		 Date birthDate = formatter.parse("25/03/1982");
		 Date deathDate = formatter.parse("25/03/2050");
		 
		org.opensrp.domain.BaseEntity domainBaseEntity = new org.opensrp.domain.BaseEntity()
				.withBaseEntityId("0002")
				.withFirstName("Md").withMiddleName("Julkar")
				.withLastName("Nain").withGender("Male")
				.withBirthdate(birthDate, false)
				.withDeathdate(deathDate, false)
				.withAddresses(addresses)
				.withAttributes(attributes);

		allBaseEntities.add(domainBaseEntity);*/

		org.opensrp.domain.BaseEntity getBaseEntityById = allBaseEntities
				.findByBaseEntityId("0002");

		//assertThat(allBaseEntitiesInDB, is(asList(domainBaseEntity)));
		assertThat(getBaseEntityById.getFirstName(), is("Md"));

	}

}
