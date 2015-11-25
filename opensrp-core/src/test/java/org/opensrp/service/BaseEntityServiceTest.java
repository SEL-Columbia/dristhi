package org.opensrp.service;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensrp.domain.Address;
import org.opensrp.domain.BaseEntity;
import org.opensrp.repository.AllBaseEntities;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class BaseEntityServiceTest {

	private BaseEntityService baseEntityService;
	
	@Mock
	private AllBaseEntities allBaseEntities;	
	
	@Before
	public void setUp() throws Exception
	{
		initMocks(this);
		baseEntityService = new BaseEntityService(allBaseEntities);
	}
	
	@Test
	public void shouldAddBaseEntity()
	{
		
		Map<String, String> addressFields = new HashMap<String,String>();
		addressFields.put("addressfields", "Vill: Gaya Bari, Ward No : #3, HouseHoldId: 123456");
		
		List<Address> addresses = new ArrayList<Address>();
		addresses.add(new Address("Permanent",new Date(),new Date(), addressFields, "70.5", "40.5","6300", "","Bangladesh" ));
				
		Map<String, Object> attributes = new HashMap<String, Object>();
		
		BaseEntity domainBaseEntity = new BaseEntity("000000001")
			.withAddresses(addresses)
			.withAttributes(attributes);
		
		allBaseEntities.add(domainBaseEntity);
		
	}
	
}
