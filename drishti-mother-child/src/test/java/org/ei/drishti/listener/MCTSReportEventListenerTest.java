package org.ei.drishti.listener;

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.domain.MCTSReport;
import org.ei.drishti.service.MCTSSMSService;
import org.ei.drishti.service.formSubmission.MCTSSMSReportService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MCTSReportEventListenerTest {
    @Mock
    private HttpAgent agent;
    @Mock
    private MCTSSMSReportService mctsSMSReportService;
    @Mock
    private MCTSSMSService mctsSMSService;

    private MCTSReportEventListener listener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        listener = new MCTSReportEventListener(mctsSMSService, mctsSMSReportService);
    }

    @Test
    public void shouldFetchMCTSReport() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2014-01-01"));
        Map<String, Object> data = new HashMap<>();

        listener.fetchReports(new MotechEvent("SUBJECT", data));

        verify(mctsSMSReportService).fetch("2014-01-01");
    }

    @Test
    public void shouldSendSMSForMCTSReportsAndMarkReportsAsClosed() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2014-01-01"));
        Map<String, Object> data = new HashMap<>();
        MCTSReport mctsReport = new MCTSReport().withCaseId("case id 1").withEntityId("entity id 1").withSendDate("2014-01-01").withEntityRegistrationDate("2014-01-01").withReportText("report text 1").withReportSent("false");
        MCTSReport anotherMCTSReport = new MCTSReport().withCaseId("case id 2").withEntityId("entity id 2").withSendDate("2014-01-01").withEntityRegistrationDate("2014-01-01").withReportText("report text 2").withReportSent("false");
        when(mctsSMSReportService.fetch("2014-01-01")).thenReturn(asList(mctsReport, anotherMCTSReport));

        listener.fetchReports(new MotechEvent("SUBJECT", data));

        InOrder inOrder = inOrder(mctsSMSService);
        inOrder.verify(mctsSMSService).send("report text 1");
        inOrder.verify(mctsSMSService).send("report text 2");
        inOrder = inOrder(mctsSMSReportService);
        inOrder.verify(mctsSMSReportService).markReportAsSent(mctsReport);
        inOrder.verify(mctsSMSReportService).markReportAsSent(anotherMCTSReport);
    }
}
