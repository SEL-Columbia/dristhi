package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ANMIndicatorSummary;
import org.ei.drishti.common.domain.MonthSummary;
import org.ei.drishti.common.domain.ReportingData;
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
        ANMIndicatorSummary anmIUDSummary = new ANMIndicatorSummary("ANM X", "IUD", "40", asList(new MonthSummary("1", "2012", "23", "45", asList("Case 1", "Case 2")),
                new MonthSummary("2", "2012", "20", "67", asList("Case 3", "Case 4"))));
        ANMIndicatorSummary anmANCSummary = new ANMIndicatorSummary("ANM X", "ANC", "50", asList(new MonthSummary("3", "2012", "30", "48", asList("Case 5", "Case 6"))));
        when(anmReportsRepository.fetchANMSummary("ANM X")).thenReturn(asList(anmIUDSummary, anmANCSummary));

        List<ANMIndicatorSummary> summaries = controller.getANMIndicatorSummaries("ANM X");

        assertEquals(asList(anmIUDSummary, anmANCSummary), summaries);
    }
}
