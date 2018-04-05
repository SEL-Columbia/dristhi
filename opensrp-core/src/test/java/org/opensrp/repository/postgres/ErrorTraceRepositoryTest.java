package org.opensrp.repository.postgres;

import static org.junit.Assert.*;

import static org.opensrp.repository.postgres.ErrorTraceRepositoryImpl.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.repository.ErrorTraceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ErrorTraceRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("errorRepositoryPostgres")
	private ErrorTraceRepository errorTraceRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("error.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		ErrorTrace error = errorTraceRepository.get("85ed95b2-8436-4a93-9ae7-ab104d65edd7");
		assertEquals(new DateTime("2018-03-14T07:53:11.152"), error.getDateOccurred());
		assertEquals("OPENMRS FAILED CLIENT PUSH", error.getErrorType());
		assertEquals("org.opensrp.domain.Client", error.getDocumentType());
		assertNull(errorTraceRepository.get("07271855-4018-497a-b180-6af"));
	}
	
	@Test
	public void testAdd() {
		DateTime occured = new DateTime();
		ErrorTrace error = new ErrorTrace(occured, "GENERIC ERROR 123!!", occured.toString(),
		        "error at stocktrace.java(677)", "unsolved", "org.test.bean");
		errorTraceRepository.add(error);
		List<ErrorTrace> errors = errorTraceRepository.getAll();
		assertEquals(9, errors.size());
		boolean found = false;
		for (ErrorTrace er : errors) {
			if (er.getDateOccurred().equals(occured)) {
				found = true;
				assertEquals("GENERIC ERROR 123!!", error.getErrorType());
				assertEquals("error at stocktrace.java(677)", error.getStackTrace());
				break;
			}
		}
		assertTrue(found);
		
	}
	
	@Test
	public void testUpdate() {
		ErrorTrace error = errorTraceRepository.get("85ed95b2-8436-4a93-9ae7-ab104d65edd7");
		error.setStatus("closed");
		Date now = new Date();
		error.setDateClosed(now);
		error.setRecordId("jjhi-iiui-234423-ll");
		errorTraceRepository.update(error);
		
		ErrorTrace updateError = errorTraceRepository.get("85ed95b2-8436-4a93-9ae7-ab104d65edd7");
		assertEquals("closed", updateError.getStatus());
		assertEquals(now, updateError.getDateClosed());
		assertEquals("jjhi-iiui-234423-ll", updateError.getRecordId());
		errorTraceRepository.update(error);
	}
	
	@Test
	public void testGetAll() {
		assertEquals(8, errorTraceRepository.getAll().size());
		errorTraceRepository.safeRemove(errorTraceRepository.get("ea96c23d-bbf1-4365-bdf5-bcf56f08ce1f"));
		assertEquals(7, errorTraceRepository.getAll().size());
	}
	
	@Test
	public void testSafeRemove() {
		errorTraceRepository.safeRemove(errorTraceRepository.get("911c7520-f701-419e-b225-5e21286b585b"));
		assertEquals(7, errorTraceRepository.getAll().size());
	}
	
	@Test
	public void testFindById() {
		ErrorTrace error = errorTraceRepository.get("07c32814-d7d1-48a5-8320-ed80435a4606");
		assertEquals(new DateTime("2018-01-03T17:06:11.166"), error.getDateOccurred());
		assertEquals("FormSubmissionProcessor", error.getErrorType());
		assertEquals("org.opensrp.service.formSubmission.FormSubmissionListener", error.getOccurredAt());
		assertEquals("[Ljava.lang.StackTraceElement;@50ef4583", error.getStackTrace());
		
		assertNull(errorTraceRepository.get("07271855-4-b180-6af"));
	}
	
	@Test
	public void testExists() {
		assertTrue(errorTraceRepository.exists("07c32814-d7d1-48a5-8320-ed80435a4606"));
		
		assertTrue(errorTraceRepository.exists("ea96c23d-bbf1-4365-bdf5-bcf56f08ce1f"));
		
		assertFalse(errorTraceRepository.exists("07c32814-d7d1-48a5-8320-ed804356"));
	}
	
	@Test
	public void testFindAllErrors() {
		assertEquals(8, errorTraceRepository.findAllErrors().size());
		errorTraceRepository.safeRemove(errorTraceRepository.get("ea96c23d-bbf1-4365-bdf5-bcf56f08ce1f"));
		assertEquals(7, errorTraceRepository.findAllErrors().size());
	}
	
	@Test
	public void testFindAllUnSolvedErrors() {
		assertEquals(6, errorTraceRepository.findAllUnSolvedErrors().size());
		
		ErrorTrace error = errorTraceRepository.get("911c7520-f701-419e-b225-5e21286b585b");
		error.setStatus(SOLVED);
		error.setDateClosed(new Date());
		errorTraceRepository.update(error);
		
		List<ErrorTrace> errors = errorTraceRepository.findAllUnSolvedErrors();
		assertEquals(5, errors.size());
		
		for (ErrorTrace err : errors)
			assertTrue(err.getStatus() == null || err.getStatus().equals(UNSOLVED));
	}
	
	@Test
	public void testFindAllSolvedErrors() {
		assertEquals(2, errorTraceRepository.findAllSolvedErrors().size());
		
		ErrorTrace error = errorTraceRepository.get("48106c03-8900-4156-a7bf-93226e1d3e58");
		error.setStatus(UNSOLVED);
		error.setDateClosed(null);
		errorTraceRepository.update(error);
		
		List<ErrorTrace> errors = errorTraceRepository.findAllSolvedErrors();
		assertEquals(1, errors.size());
		assertEquals(new DateTime("2018-01-03T17:06:11.166"), errors.get(0).getDateOccurred());
		assertEquals("FormSubmissionProcessor", errors.get(0).getErrorType());
		assertEquals("org.opensrp.service.formSubmission.FormSubmissionListener", errors.get(0).getOccurredAt());
		assertEquals("[Ljava.lang.StackTraceElement;@50ef4583", errors.get(0).getStackTrace());
		
	}
}
