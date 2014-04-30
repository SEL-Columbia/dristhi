package org.ei.drishti.repository.it;

import org.ei.drishti.domain.MCTSReport;
import org.ei.drishti.repository.AllMCTSReports;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllMCTSReportsIntegrationTest {
    @Autowired
    private AllMCTSReports allMCTSReports;

    @Before
    public void setUp() throws Exception {
        allMCTSReports.removeAll();
    }

    @Test
    public void shouldSaveAndRetrieveReports() throws Exception {
        MCTSReport mctsReport = new MCTSReport("entity id 1", "report text 1", "2014-01-01", "2014-01-11", "2014-01-11");
        MCTSReport anotherMCTSReport = new MCTSReport("entity id 2", "report text 2", "2013-01-01", "2013-01-11", "2013-01-11");
        allMCTSReports.add(mctsReport);
        allMCTSReports.add(anotherMCTSReport);

        assertEquals(asList(mctsReport, anotherMCTSReport), allMCTSReports.getAll());
    }

//    @Test
//    public void shouldGetReportsWhichHaveTheGivenSendDate() throws Exception {
//        MCTSReport reportWithGivenSendDate = new MCTSReport("entity id 1", "report text 1", "2014-01-01", "2014-01-11", "2014-01-11");
//        MCTSReport anotherReportWithGivenSendDate = new MCTSReport("entity id 2", "report text 2", "2014-01-01", "2014-01-11", "2014-01-11");
//        MCTSReport reportWithSendDateLessThanGivenDate = new MCTSReport("entity id 3", "report text 3", "2013-12-20", "2013-12-31", "2013-12-31");
//        MCTSReport reportWithSendDateGreaterThanGivenDate = new MCTSReport("entity id 4", "report text 4", "2014-01-05", "2014-01-05", "2014-01-15");
//        MCTSReport reportWhichIsAlreadySent = new MCTSReport("entity id 2", "report text 2", "2014-01-01", "2014-01-11", "2014-01-11", true);
//        allMCTSReports.add(reportWithGivenSendDate);
//        allMCTSReports.add(anotherReportWithGivenSendDate);
//
//        assertEquals(asList(), allMCTSReports.allReportsToBeSentAsOf("2014-01-11"));
//    }
}
