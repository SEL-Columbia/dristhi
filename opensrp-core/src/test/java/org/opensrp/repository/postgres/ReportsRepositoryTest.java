package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.opensrp.domain.Report;
import org.opensrp.repository.ReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ReportsRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("reportsRepositoryPostgres")
	private ReportsRepository reportsRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("report.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		Report report = reportsRepository.get("07271855-4018-497a-b180-6af01f0fa62b");
		assertEquals("7073cef4-b974-4910-afa7-dd2ccadba089", report.getFormSubmissionId());
		assertEquals("b0781dd2-e1b1-4878-8e6c-fba488eb9fa3", report.getLocationId());
		assertNull(reportsRepository.get("07271855-4018-497a-b180-6af"));
	}
	
	@Test
	public void testAdd() {
		Report report = new Report("ghsjf-s723-sj97s", "3453535sdfs", new DateTime(), "MONTHLY", "435-njhjh-jjh", "tester11",
		        "3", 1522829702064l, 0, null);
		reportsRepository.add(report);
		assertEquals(6, reportsRepository.getAll().size());
		List<Report> reports = reportsRepository.findByBaseEntityAndFormSubmissionId("ghsjf-s723-sj97s", "435-njhjh-jjh");
		assertEquals("3453535sdfs", reports.get(0).getLocationId());
		assertEquals("MONTHLY", report.getReportType());
		assertNull(report.getHia2Indicators());
		
	}
	
	@Test
	public void testUpdate() {
		Report report = reportsRepository.get("cd09a3d4-01d9-485c-a1c5-a2eb078a61be");
		report.setBaseEntityId("asads-asdas7676-ggas");
		report.setServerVersion(1522827820l);
		report.setDuration(6);
		
		reportsRepository.update(report);
		
		Report updateReport = reportsRepository.get("cd09a3d4-01d9-485c-a1c5-a2eb078a61be");
		assertEquals("asads-asdas7676-ggas", updateReport.getBaseEntityId());
		assertEquals(1522827820l, updateReport.getServerVersion().longValue());
		assertEquals(6, updateReport.getDuration());
		
	}
	
	@Test
	public void testGetAll() {
		assertEquals(5, reportsRepository.getAll().size());
		reportsRepository.safeRemove(reportsRepository.get("cd09a3d4-01d9-485c-a1c5-a2eb078a61be"));
		assertEquals(4, reportsRepository.getAll().size());
	}
	
	@Test
	public void testSafeRemove() {
		reportsRepository.safeRemove(reportsRepository.get("c57ba49f-34b9-4986-9b87-69f48b1830c5"));
		assertEquals(4, reportsRepository.getAll().size());
		assertNull(reportsRepository.get("c57ba49f-34b9-4986-9b87-69f48b1830c5"));
	}
	
	@Test
	public void testFindById() {
		Report report = reportsRepository.findById("c57ba49f-34b9-4986-9b87-69f48b1830c5");
		assertEquals("88f9fe90-1e45-46b3-8056-e932574dcbd9", report.getFormSubmissionId());
		assertEquals(64, report.getHia2Indicators().size());
	}
	
	@Test
	public void testFindByBaseEntityId() {
		
		assertEquals(2, reportsRepository.findByBaseEntityId("6654kk-mnj45-mmnfgd-l45645").size());
		
		List<Report> reports = reportsRepository.findByBaseEntityId("678343544-nhj7-jghdfgfd-mkjdkfg");
		assertEquals(1, reports.size());
		assertEquals("ecafd20f-c95b-4046-9355-9512e1908da4", reports.get(0).getId());
		assertEquals("1aea74ac-b737-477a-99d3-728011fbae3f", reports.get(0).getFormSubmissionId());
		
		assertTrue(reportsRepository.findByBaseEntityId("sfsdfd").isEmpty());
		
	}
	
	@Test
	public void testFindAllByIdentifier() {
		assertEquals(2, reportsRepository.findAllByIdentifier("56757L").size());
		
		List<Report> reports = reportsRepository.findAllByIdentifier("1121K");
		assertEquals(1, reports.size());
		assertEquals("07271855-4018-497a-b180-6af01f0fa62b", reports.get(0).getId());
		
		assertTrue(reportsRepository.findAllByIdentifier("7234M").isEmpty());
	}
	
	@Test
	public void testFindByBaseEntityAndType() {
		assertEquals(2, reportsRepository.findByBaseEntityAndType("6654kk-mnj45-mmnfgd-l45645", "HIA2").size());
		
		List<Report> reports = reportsRepository.findByBaseEntityAndType("678343544-nhj7-jghdfgfd-mkjdkfg", "HIA2");
		assertEquals(1, reports.size());
		assertEquals("ecafd20f-c95b-4046-9355-9512e1908da4", reports.get(0).getId());
		assertEquals("1aea74ac-b737-477a-99d3-728011fbae3f", reports.get(0).getFormSubmissionId());
		
		assertTrue(reportsRepository.findByBaseEntityAndType("678343544-nhj7-jghdfgfd-mkjdkfg", "HIA4").isEmpty());
	}
	
	@Test
	public void testFindByEmptyServerVersion() {
		List<Report> reports = reportsRepository.findByEmptyServerVersion();
		assertEquals(1, reports.size());
		assertEquals("60ab7d5c-a051-4633-b0b3-f52b701cb261", reports.get(0).getId());
		assertEquals("03b5d0b8-4f72-4415-9909-ce03b5802c75", reports.get(0).getFormSubmissionId());
		
		reports.get(0).setServerVersion(System.currentTimeMillis());
		reportsRepository.update(reports.get(0));
		
		assertTrue(reportsRepository.findByEmptyServerVersion().isEmpty());
	}
	
	@Test
	public void testFindByServerVersion() {
		assertEquals(1, reportsRepository.findByServerVersion(1503312366264l).size());
		
		assertEquals(3, reportsRepository.findByServerVersion(1500307579515l).size());
		
		assertTrue(reportsRepository.findByServerVersion(System.currentTimeMillis()).isEmpty());
	}
	
	@Test
	public void testFindByBaseEntityAndFormSubmissionId() {
		List<Report> reports = reportsRepository.findByBaseEntityAndFormSubmissionId("6654kk-mnj45-mmnfgd-l45645",
		    "03b5d0b8-4f72-4415-9909-ce03b5802c75");
		assertEquals(1, reports.size());
		assertEquals("60ab7d5c-a051-4633-b0b3-f52b701cb261", reports.get(0).getId());
		
		assertTrue(reportsRepository
		        .findByBaseEntityAndFormSubmissionId("6654kk-mnj45-mmnfgd-l45645", "d78a8105-f808-488a-b67a-0c4c46845194")
		        .isEmpty());
	}
	
	@Test
	public void testFindReports() {
		assertEquals(2,
		    reportsRepository.findReports(null, "biddemo", "", "6654kk-mnj45-mmnfgd-l45645", null, null, null, 20).size());
		
		List<Report> reports = reportsRepository.findReports(null, "biddemo", "", "6654kk-mnj45-mmnfgd-l45645", 0l, null,
		    null, 20);
		assertEquals(1, reports.size());
		
		assertEquals("07271855-4018-497a-b180-6af01f0fa62b", reports.get(0).getId());
		
		assertEquals(1, reportsRepository.findReports(null, "biddemo", "9e4fc064-d8e7-4fcb-942e-cbcf6524fb24",
		    "6654kk-mnj45-mmnfgd-l45645", null, null, null, 20).size());
		
		assertEquals(4, reportsRepository.findReports("", "biddemo", null, null, 0l, null, null, 20).size());
		
		assertEquals(5, reportsRepository.findReports("", "biddemo", null, null, null, null, null, 20).size());
		
		assertEquals(3, reportsRepository.findReports("", "biddemo", null, null, 1500307579515l, null, null, 20).size());
		
		assertEquals(2, reportsRepository.findReports("", "biddemo", null, null, 0l, null, null, 2).size());
		
		assertTrue(reportsRepository.findReports("ATeam", "biddemo", "9e4fc064-d8e7-4fcb-942e-cbcf6524fb24",
		    "6654kk-mnj45-mmnfgd-l45645", System.currentTimeMillis(), null, null, 20).isEmpty());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFindReportsNoParams() {
		reportsRepository.findReports("", null, null, null, null, null, null, 2);
	}
	
	@Test
	public void testFindReportsV2() {
		assertEquals(2, reportsRepository.findReports("6654kk-mnj45-mmnfgd-l45645", new DateTime("2017-05-01"),
		    new DateTime(), "HIA2", "biddemo", null, null, null).size());
		
		assertEquals(5, reportsRepository
		        .findReports(null, new DateTime("2017-05-01"), new DateTime(), "HIA2", "biddemo", null, null, null).size());
		
		assertEquals(4, reportsRepository.findReports(null, new DateTime("2017-05-01"), new DateTime(), "HIA2", "biddemo",
		    "9e4fc064-d8e7-4fcb-942e-cbcf6524fb24", new DateTime("2017-07-17"), new DateTime()).size());
		
		assertEquals(2, reportsRepository.findReports(null, new DateTime("2017-05-01"), new DateTime(), "HIA2", "biddemo",
		    "9e4fc064-d8e7-4fcb-942e-cbcf6524fb24", new DateTime("2017-07-18"), new DateTime()).size());
		
		List<Report> reports = reportsRepository.findReports("678343544-nhj7-jghdfgfd-mkjdkfg", new DateTime("2017-05-01"),
		    new DateTime(), "HIA2", "biddemo", "9e4fc064-d8e7-4fcb-942e-cbcf6524fb24", new DateTime("2017-07-17"),
		    new DateTime());
		
		assertEquals(1, reports.size());
		
		assertEquals("ecafd20f-c95b-4046-9355-9512e1908da4", reports.get(0).getId());
		
		assertTrue(
		    reportsRepository.findReports("6654kk-mnj45-mmnfgd-l45645", new DateTime("2017-05-01"), new DateTime(), "HIA2",
		        "biddemo", "9e4fc064-d8e7-4fcb-942e-cbcf6524fb24", new DateTime("2017-07-31"), new DateTime()).isEmpty());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFindReportsV2NoParams() {
		reportsRepository.findReports(null, null, null, null, null, null, null, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFindReportsByDynamicQuery() {
		reportsRepository.findReportsByDynamicQuery("reportType:HIA2");
	}
	
}
