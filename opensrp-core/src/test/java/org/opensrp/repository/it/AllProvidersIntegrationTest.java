package org.opensrp.repository.it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.repository.AllProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllProvidersIntegrationTest {
	
	@Autowired
	private AllProviders allProviders;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	public void shouldAddProvider()
	{
		/*Map<String, String> identifiers = new HashMap<String, String>();
		identifiers.put("identifier-1",
				"FWA-1");

		
		org.opensrp.domain.Provider domainProvider = new org.opensrp.domain.Provider()
														.withBaseEntityId("0001")
														.withIdentifiers(identifiers);
		
		
		allProviders.add(domainProvider);*/
		
		
		
		org.opensrp.domain.Provider baseEntityIdInProvider = allProviders
				.findByBaseEntityId("0001");

		//assertThat(allBaseEntitiesInDB, is(asList(domainBaseEntity)));
		//assertThat(baseEntityIdInProvider.getBaseEntityId(), is("0001"));
		
		
	}
}
