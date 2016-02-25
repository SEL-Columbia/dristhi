package org.opensrp.web.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensrp.domain.Client;
import org.opensrp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-opensrp-web.xml")
public class ClientResourceTest {
	
	MockHttpServletRequest req;
	
	
	@Mock
	private MockHttpServletResponse resp = new MockHttpServletResponse();
	
	private ClientResource cr = new ClientResource();

	@Autowired
	private ClientService cs;
	
	@Test
	public void testClientSearch() {
		Client res = cr.getByUniqueId("ei0");
	}
}
