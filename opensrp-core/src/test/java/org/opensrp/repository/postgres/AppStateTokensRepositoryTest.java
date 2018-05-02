package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.opensrp.domain.AppStateToken;
import org.opensrp.repository.AppStateTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AppStateTokensRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("appStateTokensRepositoryPostgres")
	private AppStateTokensRepository appStateTokensRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("app_state_token.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		AppStateToken token = appStateTokensRepository.get("1");
		assertEquals("sync_schedule_tracker_by_last_update_enrollment", token.getName());
		assertEquals("34343", token.getValue().toString());
		assertEquals("ScheduleTracker token to keep track of enrollment", token.getDescription());
		assertEquals(1521017416, token.getLastEditDate());
		//Try to find non-exist token
		assertNull(appStateTokensRepository.get("10"));
	}
	
	@Test
	public void testGetAll() {
		List<AppStateToken> tokens = appStateTokensRepository.getAll();
		assertEquals(5, tokens.size());
		
		appStateTokensRepository.safeRemove(appStateTokensRepository.findByName("sync_event_by_date_updated").get(0));
		
		tokens = appStateTokensRepository.getAll();
		assertEquals(4, tokens.size());
		for (AppStateToken token : tokens)
			assertNotEquals("sync_event_by_date_updated", token.getName());
	}
	
	@Test
	public void testFindByName() {
		List<AppStateToken> tokens = appStateTokensRepository.findByName("sync_event_by_date_updated");
		assertEquals(1, tokens.size());
		assertEquals("343232", tokens.get(0).getValue());
		assertEquals(1521017416, tokens.get(0).getLastEditDate());
		//Try to find non-exist token
		assertTrue(appStateTokensRepository.findByName("sync_client_non_existent").isEmpty());
	}
	
	@Test
	public void testSafeRemove() {
		appStateTokensRepository.safeRemove(appStateTokensRepository.findByName("sync_event_by_date_voided").get(0));
		assertTrue(appStateTokensRepository.findByName("sync_event_by_date_voided").isEmpty());
		List<AppStateToken> tokens = appStateTokensRepository.getAll();
		assertEquals(4, tokens.size());
		for (AppStateToken token : tokens)
			assertNotEquals("sync_event_by_date_voided", token.getName());
	}
	
	@Test
	public void testUpdate() {
		AppStateToken token = new AppStateToken("sync_event_by_date_updated", "4564353453434", 1521019916);
		appStateTokensRepository.update(token);
		
		AppStateToken updatedToken = appStateTokensRepository.findByName("sync_event_by_date_updated").get(0);
		
		assertEquals("4564353453434", updatedToken.getValue());
		assertEquals(1521019916, updatedToken.getLastEditDate());
		
	}
	
	@Test
	public void testAdd() {
		AppStateToken token = new AppStateToken("sync_apptoken_custom", "45643534MKHT", 15210234516l,
		        "Custom Test App Token");
		appStateTokensRepository.add(token);
		
		AppStateToken savedToken = appStateTokensRepository.findByName("sync_apptoken_custom").get(0);
		
		assertEquals("45643534MKHT", savedToken.getValue());
		assertEquals(15210234516l, savedToken.getLastEditDate());
		assertEquals("Custom Test App Token", savedToken.getDescription());
		
	}
	
}
