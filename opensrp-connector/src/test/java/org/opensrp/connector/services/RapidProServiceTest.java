package org.opensrp.connector.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.opensrp.connector.SpringApplicationContextProvider;
import org.opensrp.connector.rapidpro.RapidProService;
import org.springframework.beans.factory.annotation.Autowired;

public class RapidProServiceTest extends SpringApplicationContextProvider {
	@Autowired
	RapidProService rapidproService;
	List<String> urns;
	List<String> contacts;
	List<String> groups;
	Map<String, Object> contact;
	@Mock
	RapidProService mockRapidProService;
	String addFieldResponse;

	@Before
	public void setup() {
		urns = new ArrayList<String>();
		urns.add("tel:+250788123123");
		groups = new ArrayList<String>();
		contacts = new ArrayList<String>();
		contacts.add("09d23a05-47fe-11e4-bfe9-b8f6b119e9ab");
		contact = new HashMap<String, Object>();
		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("lmp", "2016-09-30");
		fields.put("anc1", "2016-10-10");
		fields.put("anc2", "2016-10-20");
		fields.put("anc3", "2016-10-30");
		fields.put("anc4", "2016-11-10");
		contact.put("name", "Woman Three");
		List<String> urns = new ArrayList<String>();
		List<String> groups = new ArrayList<String>();
		groups.add("Pregnant Women");
		urns.add("telegram:207355745");
		contact.put("urns", urns);
		contact.put("groups", groups);
		contact.put("fields", fields);
		MockitoAnnotations.initMocks(this);
		addFieldResponse = "{\"key\": \"anc3\",\"label\": \"anc3\",\"value_type\": \"T\"}";

		when(mockRapidProService.addField("anc3", "T")).thenReturn(addFieldResponse);

	}

	@After
	public void tearDown() {
		urns.clear();
		groups.clear();
		contacts.clear();
		contact.clear();
	}

	@Test
	public void sendMessageShouldFailIfTextisNull() throws Exception {
		String response = rapidproService.sendMessage(urns, contacts, groups, null, "");
		Assert.assertEquals("Empty text or text longer than 480 characters not allowed", response);
	}

	@Test
	public void sendMessageShouldFailIfTextisEmpty() throws Exception {
		String response = rapidproService.sendMessage(urns, contacts, groups, "", "");
		Assert.assertEquals("Empty text or text longer than 480 characters not allowed", response);
	}

	@Test
	public void sendMessageShouldFailIfTextisTooLong() throws Exception {
		String text = "When Aklima Begum was initially registered in OpenSRP during a census / enumeration round, her mobile phone (or family member’s phone) number was collected as part of her personal profile. Aklima Begum is found to be pregnant in week 20 of her pregnancy. As her pregnancy is recorded in OpenSRP, her health worker, Rokeya, receives a prompt asking whether Aklima would like to receive motivational messages and/or reminders. Aklima agrees, and this is recorded in the OpenSRP system. The server sends her a ‘test’ message to welcome her to the system and ensure the phone number is correct. Every week,";
		String response = rapidproService.sendMessage(urns, contacts, groups, text, "");
		Assert.assertEquals("Empty text or text longer than 480 characters not allowed", response);
	}

	@Test
	@Ignore // FIXME
	public void sendMessageShouldReturnJsonStringOnSuccess() throws Exception {
		Assert.fail("Implement this");
	}

	@Test
	public void sendMessageShouldFailIfEmptyUrnsAndContactsAndGroups() throws Exception {
		tearDown();
		String response = rapidproService.sendMessage(urns, contacts, groups, "Test rapidpro text", "");
		Assert.assertEquals("No recipients specified", response);
	}

	@Test
	public void sendMessageShouldFailIfNullUrnsAndContactsAndGroups() throws Exception {
		String response = rapidproService.sendMessage(null, null, null, "Test rapidpro text", "");
		Assert.assertEquals("No recipients specified", response);
	}

	@Test
	public void createContactShouldFailIfEmptyFieldValues() throws Exception {
		tearDown();
		String response = rapidproService.createContact(contact);
		Assert.assertEquals("Field values cannot be empty and must have urns", response);
	}

	@Test
	public void createContactShouldFailIfNullFieldValues() throws Exception {
		String response = rapidproService.createContact(null);
		Assert.assertEquals("Field values cannot be empty and must have urns", response);
	}

	@Test
	public void createContactShouldFailIfNullUrns() throws Exception {
		contact.remove("urns");
		String response = rapidproService.createContact(contact);
		Assert.assertEquals("Field values cannot be empty and must have urns", response);
	}

	@Test
	@Ignore // FIXME
	public void createContactShouldReturnJsonStringOnSuccess() throws Exception {
		Assert.fail("Implement this");
	}

	@Test
	public void addFieldShouldFailIfNullLabel() throws Exception {
		String response = rapidproService.addField(null, "T");
		Assert.assertEquals("Field label is required", response);
	}

	@Test
	public void addFieldShouldFailIfEmptyLabel() throws Exception {
		String response = rapidproService.addField("", "T");
		Assert.assertEquals("Field label is required", response);
	}

	@Test
	public void addFieldShouldReturnJsonStringOnSuccess() throws Exception {
		String response = mockRapidProService.addField("anc3", "T");
		Assert.assertEquals(addFieldResponse, response);
	}
}
