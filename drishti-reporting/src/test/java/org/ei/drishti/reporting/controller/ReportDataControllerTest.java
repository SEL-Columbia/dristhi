package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.reporting.ReportDataMissingException;
import org.ei.drishti.reporting.repository.ANMReportsRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.drishti.common.domain.Indicator.*;
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
        ReportingData data = serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")).withQuantity("40");

        controller.submit(data);

        verify(servicesProvidedRepository).save("ANM X", "ANM", "TC 1", "BCG", "2012-01-01", "bherya", "Sub Center", "PHC X", "40");
    }

    @Test
    public void shouldSaveANMReportDataInDB() throws Exception {
        ReportingData data = anmReportData("ANM X", "EC Number 1", FP_IUD, "2012-01-01").withQuantity("40");

        controller.submit(data);

        verify(anmReportsRepository).save("ANM X", "EC Number 1", "IUD", "2012-01-01", "40");
    }

    @Test(expected = ReportDataMissingException.class)
    public void shouldThrowExceptionWhenANMReportDataDoesNotHaveAllTheNecessaryInformation() throws Exception {
        ReportingData data = anmReportData(null, null, FP_CONDOM, null);

        controller.submit(data);
    }

    @Test(expected = ReportDataMissingException.class)
    public void shouldThrowExceptionWhenServiceProvidedReportDataDoesNotHaveAllTheNecessaryInformation() throws Exception {
        ReportingData data = serviceProvidedData("", null, BCG, null, new Location(null, null, null));

        controller.submit(data);
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
