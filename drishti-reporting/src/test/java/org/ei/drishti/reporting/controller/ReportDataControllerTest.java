package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.reporting.ReportDataMissingException;
import org.ei.drishti.reporting.domain.ANMReportData;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.domain.ServiceProvided;
import org.ei.drishti.reporting.repository.ANMReportsRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void shouldFetchServiceProvidedReportForTheGivenMonth() throws ReportDataMissingException {
        Map<String, String> map = new HashMap<>();
        map.put("startDate", "2013-10-26");
        map.put("endDate", "2013-11-25");
        ReportingData reportingData = new ReportingData(SERVICE_PROVIDED_DATA_TYPE, map);
        when(servicesProvidedRepository.getReportsFor("2013-10-26", "2013-11-25")).thenReturn(asList(serviceProvided));
        when(serviceProvided.id()).thenReturn("123");
        when(serviceProvided.indicator()).thenReturn("INDICATOR 1");
        when(serviceProvided.location()).thenReturn(new Location("village", "subcenter", "phc"));
        when(serviceProvided.date()).thenReturn("2013-10-26");
        when(serviceProvided.serviceProviderType()).thenReturn("ANM");

        String json = controller.reportForCurrentReportingMonth("serviceProvided", "2013-10-26", "2013-11-25", null);

        verify(servicesProvidedRepository).getReportsFor("2013-10-26", "2013-11-25");
        assertEquals(json, "[{\"id\":\"123\",\"serviceProviderType\":\"ANM\",\"indicator\":\"INDICATOR 1\",\"date\":\"2013-10-26\",\"location\":{\"village\":\"village\",\"subCenter\":\"subcenter\",\"phc\":\"phc\"}}]");
    }

    @Test
    public void shouldFetchANMReportForTheGivenMonth() throws ReportDataMissingException {
        Map<String, String> map = new HashMap<>();
        map.put("anmId", "ANM X");
        map.put("startDate", "2013-10-26");
        map.put("endDate", "2013-11-25");
        ReportingData reportingData = new ReportingData(ANM_REPORT_DATA_TYPE, map);

        when(anmReportsRepository.getReportsFor("ANM X", "2013-10-26", "2013-11-25")).thenReturn(asList(anmReportData));
        when(anmReportData.id()).thenReturn("123");
        when(anmReportData.anmIdentifier()).thenReturn("ANM X");
        when(anmReportData.indicator()).thenReturn(new Indicator("INDICATOR 1"));
        when(anmReportData.date()).thenReturn(dates);
        when(dates.date()).thenReturn(date);
        when(date.toString()).thenReturn("2013-10-26");


            String json = controller.reportForCurrentReportingMonth("anmReportData", "2013-10-26", "2013-11-25", "ANM X");

        verify(anmReportsRepository).getReportsFor("ANM X", "2013-10-26", "2013-11-25");
        assertEquals(json, "[{\"anmIdentifier\":\"ANM X\",\"indicator\":\"INDICATOR 1\",\"id\":\"123\",\"date\":\"2013-10-26\"}]");
    }
}
