package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.reporting.ReportDataMissingException;
import org.ei.drishti.reporting.domain.ANMReportData;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.ServiceProvided;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.repository.ANMReportsRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.ei.drishti.reporting.service.AggregateReportsService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.*;
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
    @Mock
    private AggregateReportsService aggregateReportsService;
    @Mock
    private ServiceProvided serviceProvided;
    @Mock
    private ANMReportData anmReportData;
    @Mock
    private Dates dates;
    @Mock
    private Date date;

    private ReportDataController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new ReportDataController(servicesProvidedRepository, anmReportsRepository, aggregateReportsService);
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

    @Test
    public void shouldUpdateListOfServiceProvidedIndicatorRowsForReportingMonthForAllANMs() throws Exception {

        HashMap<String, String> data = new HashMap<String, String>();
        String type = "serviceProvided";
        data.put(SERVICE_PROVIDER_TYPE, type);
        data.put(EXTERNAL_ID, "external id 1");
        data.put(SERVICE_PROVIDED_DATE, "2013-01-26");
        data.put(VILLAGE, "village 1");
        data.put(SUB_CENTER, "sub center 1");
        data.put(PHC, "phc");
        data.put(QUANTITY, "1");
        data.put(INDICATOR, "INDICATOR 1");
        data.put(ANM_IDENTIFIER, "anm id 1");
        ReportingData reportingData = new ReportingData("serviceProvided", data);

        ReportDataUpdateRequest reportDataUpdateRequest = new ReportDataUpdateRequest()
                .withType(type)
                .withStartDate("2013-01-26")
                .withEndDate("2013-02-25")
                .withReportingData(asList(reportingData));

        String result = controller.updateReports(reportDataUpdateRequest);

        verify(servicesProvidedRepository).update(reportDataUpdateRequest);
        assertEquals("Success.", result);
    }

    @Test
    public void shouldDeleteListOfANMReportsIndicatorRowsForReportingMonthForAllANMs() throws Exception {
        String indicator = "INDICATOR 1";
        String startDate = "2013-01-26";
        String endDate = "2013-02-25";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(SERVICE_PROVIDER_TYPE, "anmReport");
        data.put(EXTERNAL_ID, "external id 1");
        data.put(SERVICE_PROVIDED_DATE, startDate);
        data.put(VILLAGE, "village 1");
        data.put(SUB_CENTER, "sub center 1");
        data.put(PHC, "phc");
        data.put(QUANTITY, "1");
        data.put(INDICATOR, indicator);
        data.put(ANM_IDENTIFIER, "anm id 1");

        ReportingData reportingData = new ReportingData("anmReportData", data);
        ReportDataUpdateRequest reportDataUpdateRequest = new ReportDataUpdateRequest()
                .withType("anmReportData")
                .withReportingData(asList(reportingData))
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withIndicator(indicator);

        String result = controller.updateReports(reportDataUpdateRequest);

        verify(anmReportsRepository).update(reportDataUpdateRequest);
        assertEquals("Success.", result);
    }

    private ServiceProvidedReport serviceProvidedReport(String anmId) {
        return new ServiceProvidedReport(1, anmId, "ANM", "INDICATOR 1", LocalDate.parse("2013-10-26").toDate(), "village", "subCenter", "phc", "taluka", "district", "state");
    }
}
