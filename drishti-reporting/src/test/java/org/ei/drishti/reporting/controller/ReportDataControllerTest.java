package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ANMIndicatorSummary;
import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.util.ANMIndicatorSummaryFactory;
import org.ei.drishti.domain.Location;
import org.ei.drishti.reporting.repository.ANMReportsRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.drishti.common.domain.Indicator.BCG;
import static org.ei.drishti.common.domain.Indicator.IUD;
import static org.ei.drishti.common.domain.ReportingData.anmReportData;
import static org.ei.drishti.common.domain.ReportingData.serviceProvidedData;
import static org.ei.drishti.common.util.ANMIndicatorSummaryFactory.createSummaryForANC;
import static org.ei.drishti.common.util.ANMIndicatorSummaryFactory.createSummaryForIUD;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportDataControllerTest {
    @Mock
    private ServicesProvidedRepository servicesProvidedRepository;
    @Mock
    private ANMReportsRepository anmReportsRepository;

    private ReportDataController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new ReportDataController(servicesProvidedRepository, anmReportsRepository);
    }

    @Test
    public void shouldSaveServiceProvidedInDB() throws Exception {
        ReportingData data = serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));

        controller.submit(data);

        verify(servicesProvidedRepository).save("ANM X", "TC 1", "BCG", "2012-01-01", "bherya", "Sub Center", "PHC X");
    }

    @Test
    public void shouldSaveANMReportDataInDB() throws Exception {
        ReportingData data = anmReportData("ANM X", "EC Number 1", IUD, "2012-01-01");

        controller.submit(data);

        verify(anmReportsRepository).save("ANM X", "EC Number 1", "IUD", "2012-01-01");
    }

    @Test
    public void shouldReturnListOfANMIndicatorSummary() throws Exception {
        ANMIndicatorSummary anmIUDSummary = createSummaryForIUD();
        ANMIndicatorSummary anmANCSummary = ANMIndicatorSummaryFactory.createSummaryForANC();
        when(anmReportsRepository.fetchANMSummary("ANM X")).thenReturn(asList(anmIUDSummary, anmANCSummary));

        List<ANMIndicatorSummary> summaries = controller.getANMIndicatorSummaries("ANM X");

        assertEquals(asList(anmIUDSummary, anmANCSummary), summaries);
    }

    @Test
    public void shouldReturnListOfANMIndicatorSummaryForAllANMs() throws Exception {
        ANMReport anmXReport = new ANMReport("ANM X", asList(createSummaryForIUD()));
        ANMReport anmYReport = new ANMReport("ANM Y", asList(createSummaryForANC()));
        when(anmReportsRepository.fetchAllANMsReport()).thenReturn(asList(anmXReport, anmYReport));

        List<ANMReport> summaries = controller.getAllANMsIndicatorSummaries();

        assertEquals(asList(anmXReport, anmYReport), summaries);
    }
}
