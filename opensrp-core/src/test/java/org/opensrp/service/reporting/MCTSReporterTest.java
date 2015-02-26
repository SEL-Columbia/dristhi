package org.opensrp.service.reporting;

import org.opensrp.domain.MCTSReport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.repository.AllMCTSReports;
import org.opensrp.service.reporting.MCTSReporter;

public class MCTSReporterTest {

    @Mock
    AllMCTSReports allMCTSReports;

    private MCTSReporter reporter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        this.reporter = new MCTSReporter(allMCTSReports, "10");
    }

    @Test
    public void shouldSaveMCTSReportAfterTenDaysWhenServiceIsProvidedWithinTenDaysOfRegistration() {
        reporter.report("mother id 1", "7777777", "ANC1", "2014-01-01", "2014-01-05");

        verify(allMCTSReports).add(new MCTSReport("mother id 1",
                "ANMPW 7777777 ANC1 050114", "2014-01-01", "2014-01-05", "2014-01-15"));
    }

    @Test
    public void shouldSaveMCTSReportWithSendDateAsTenDaysFromServiceProvidedDate() {
        reporter.report("mother id 1", "7777777", "ANC1", "2014-01-01", "2014-01-05");

        verify(allMCTSReports).add(new MCTSReport("mother id 1",
                "ANMPW 7777777 ANC1 050114", "2014-01-01", "2014-01-05", "2014-01-15"));
    }
}
