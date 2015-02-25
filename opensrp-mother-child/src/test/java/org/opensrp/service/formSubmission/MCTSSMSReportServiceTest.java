package org.opensrp.service.formSubmission;

import org.opensrp.domain.MCTSReport;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.repository.AllMCTSReports;

public class MCTSSMSReportServiceTest {
    @Mock
    private AllMCTSReports allMCTSReports;
    private MCTSSMSReportService reportService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        this.reportService = new MCTSSMSReportService(allMCTSReports);
    }

    @Test
    public void shouldFetchAllReportsForAGivenDate() throws Exception {
        MCTSReport mctsReport = new MCTSReport().withCaseId("case id 1").withEntityId("entity id 1").withSendDate("2014-01-01").withEntityRegistrationDate("2014-01-01").withReportText("report text 1").withReportSent("false");
        when(allMCTSReports.allReportsToBeSentAsOf("2014-01-01")).thenReturn(asList(mctsReport));

        List<MCTSReport> reports = reportService.fetch("2014-01-01");

        verify(allMCTSReports).allReportsToBeSentAsOf("2014-01-01");
        assertEquals(asList(new MCTSReport().withCaseId("case id 1").withEntityId("entity id 1")
                .withSendDate("2014-01-01").withEntityRegistrationDate("2014-01-01")
                .withReportText("report text 1").withReportSent("false")), reports);
    }

    @Test
    public void shouldDelegateToAllMCTSReportsToCloseMCTSReport() throws Exception {
        MCTSReport mctsReport = new MCTSReport().withCaseId("case id 1").withEntityId("entity id 1").withSendDate("2014-01-01").withEntityRegistrationDate("2014-01-01").withReportText("report text 1").withReportSent("false");

        reportService.markReportAsSent(mctsReport);

        verify(allMCTSReports).markReportAsSent(mctsReport);
    }
}