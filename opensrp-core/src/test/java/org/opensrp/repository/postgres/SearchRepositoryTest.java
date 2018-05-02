package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.opensrp.domain.Client;
import org.opensrp.repository.SearchRepository;
import org.opensrp.search.ClientSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SearchRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("searchRepositoryPostgres")
	private SearchRepository searchRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("client.sql");
		return scripts;
	}
	
	@Test
	public void testFindByCriteria() {
		ClientSearchBean clientSearchBean = new ClientSearchBean();
		List<Client> clients = searchRepository.findByCriteria(clientSearchBean, "Jan", null, null, 20);
		
		assertEquals(5, clients.size());
		
		clients = searchRepository.findByCriteria(clientSearchBean, "Jan", null, "Bab", 20);
		
		assertEquals(5, clients.size());
		
		clients = searchRepository.findByCriteria(clientSearchBean, "Jan", null, "Baby", 20);
		
		assertEquals(4, clients.size());
		
		clients = searchRepository.findByCriteria(clientSearchBean, "Janu", null, "Baby", 3);
		
		assertEquals(3, clients.size());
		
		for (Client client : clients) {
			assertTrue(client.getFirstName().contains("Janu"));
			assertTrue(client.getLastName().contains("Baby"));
		}
		
		clients = searchRepository.findByCriteria(clientSearchBean, "JAnu", null, "BABY", 3);
		
		assertEquals(3, clients.size());
		
		for (Client client : clients) {
			assertTrue(client.getFirstName().contains("Janu"));
			assertTrue(client.getLastName().contains("Baby"));
		}
		
		clients = searchRepository.findByCriteria(clientSearchBean, "Janu", null, "Babyfive", 3);
		
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b241693a2f", clients.get(0).getId());
		assertEquals("aabcd2cc-c111-41c6-85e6-cb5d9e350d08", clients.get(0).getBaseEntityId());
		
		ClientSearchBean searchBean = new ClientSearchBean();
		assertEquals(15, searchRepository.findByCriteria(searchBean, null, null, null, 20).size());
		
		searchBean.setNameLike("Jan");
		assertEquals(4, searchRepository.findByCriteria(searchBean, "Janu", null, null, 20).size());
		
		searchBean.setNameLike("Baby");
		searchBean.setGender("Male");
		assertEquals(2, searchRepository.findByCriteria(searchBean, "Jan", null, "Baby", 20).size());
		
		searchBean.setBirthdateFrom(new DateTime("2016-04-13"));
		searchBean.setBirthdateTo(new DateTime());
		assertEquals(2, searchRepository.findByCriteria(searchBean, "Jan", null, "Baby", 20).size());
		
		searchBean.setDeathdateFrom(new DateTime("2018-01-01"));
		searchBean.setDeathdateTo(new DateTime());
		assertEquals(1, searchRepository.findByCriteria(searchBean, "Jan", null, "Baby", 20).size());
		
		searchBean = new ClientSearchBean();
		searchBean.setAttributeType("Home_Facility");
		searchBean.setAttributeValue("Happy Kids Clinic");
		assertEquals(9, searchRepository.findByCriteria(searchBean, null, null, null, 20).size());
		
		searchBean.setAttributeType("CHW_Name");
		searchBean.setAttributeValue("Hellen");
		clients = searchRepository.findByCriteria(searchBean, null, null, null, 20);
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		
		searchBean = new ClientSearchBean();
		searchBean.setLastEditFrom(new DateTime("2018-03-13T12:57:05.652"));
		searchBean.setLastEditTo(new DateTime());
		assertEquals(6, searchRepository.findByCriteria(searchBean, null, null, null, 20).size());
		
	}
	
}
