package org.opensrp.repository.it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.repository.AllClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllClientsIntegrationTest {

	
	@Autowired
	private AllClients allClients;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	public void shouldAddClient()
	{
		/*Map<String, String> identifiers = new HashMap<String, String>();
		identifiers.put("identifier-1",
				"FWA-1");

		
		org.opensrp.domain.Client domainClient = new org.opensrp.domain.Client()
														.withBaseEntityId("0001")
														.withIdentifiers(identifiers);
		
		allClients.add(domainClient);*/
		
		org.opensrp.domain.Client getClientByBaseEntityId = allClients
				.findByBaseEntityId("0001");
		
		//assertThat(getClientByBaseEntityId.getBaseEntityId(), is("0001"));
	}
	
	/*@Test
	public void shouldGetClientbyIdentifiers()
	{
		org.opensrp.domain.Client getClientByKeyId = allClients
				.findClientByIds("id2");
		
		assertThat(getClientByKeyId.getBaseEntityId(), is("0001"));
		
	}*/
	
}
