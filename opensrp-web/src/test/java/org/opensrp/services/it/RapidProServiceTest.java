package org.opensrp.services.it;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opensrp.connector.rapidpro.RapidProService;
import org.opensrp.integration.SpringApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class RapidProServiceTest extends SpringApplicationContextProvider {
	
	@Autowired
	RapidProService rapidproService;
	
	@Test
	@Ignore//FIXME
	public void shouldCreateContactAndReceiveContactObjectResponse() throws Exception {
		//		Map<String,Object> contact= new HashMap<String,Object>();
		//		Map<String,Object> fields= new HashMap<String,Object>();
		//		fields.put("lmp", "2016-09-30");
		//		fields.put("anc1", "2016-10-10");
		//		fields.put("anc2", "2016-10-20");
		//		fields.put("anc3", "2016-10-30");
		//		fields.put("anc4", "2016-11-10");
		//		contact.put("name", "Woman Three");
		//		List<String> urns= new ArrayList<String>();
		//		List<String> groups= new ArrayList<String>();
		//		groups.add("Pregnant Women");
		//		urns.add("telegram:207355745");
		//		contact.put("urns", urns);
		//		contact.put("groups", groups);
		//		contact.put("fields", fields);
		//		rapidproService.sendMessage(urns, null, null, "rapidpro test text is here from opensrp", null);
		//		rapidproService.createContact(contact);
		Assert.fail("Implement this");
	}
	
	@Test
	@Ignore//FIXME
	public void shouldCreateContactAndAddThemToGroup() throws Exception {
		Assert.fail("Implement this");
	}
	
	@Test
	@Ignore//FIXME
	public void shouldUpdateContactAndReceiveContactObjectResponse() throws Exception {
		Assert.fail("Implement this");
	}
	
	@Test
	@Ignore//FIXME
	public void shouldSendMessageAndReceiveMessageBroadcastResponse() throws Exception {
		Assert.fail("Implement this");
	}
	
	@Test
	@Ignore//FIXME
	public void shouldCreateFieldAndReceiveFieldKeyResponse() throws Exception {
		Assert.fail("Implement this");
	}
}
