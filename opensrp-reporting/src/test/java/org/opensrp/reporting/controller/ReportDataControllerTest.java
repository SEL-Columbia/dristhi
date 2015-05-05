package org.opensrp.reporting.controller;

import org.opensrp.common.domain.ANMReport;
import org.opensrp.common.domain.ReportDataDeleteRequest;
import org.opensrp.common.domain.ReportDataUpdateRequest;
import org.opensrp.common.domain.ReportingData;
import org.opensrp.common.domain.Location;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.opensrp.common.AllConstants.ReportDataParameters.*;
import static org.opensrp.common.domain.Indicator.*;
import static org.opensrp.common.domain.ReportingData.anmReportData;
import static org.opensrp.common.domain.ReportingData.serviceProvidedData;
import static org.opensrp.common.util.ANMIndicatorSummaryFactory.createSummaryForANC;
import static org.opensrp.common.util.ANMIndicatorSummaryFactory.createSummaryForIUD;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.reporting.DristhiEntityIdMissingException;
import org.opensrp.reporting.ReportDataMissingException;
import org.opensrp.reporting.controller.ReportDataController;
import org.opensrp.reporting.domain.ANMReportData;
import org.opensrp.reporting.domain.ServiceProvided;
import org.opensrp.reporting.repository.ANMReportsRepository;
import org.opensrp.reporting.repository.ServicesProvidedRepository;

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
    private Date date;

    private ReportDataController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new ReportDataController(servicesProvidedRepository, anmReportsRepository);
    }

    @Test
    public void shouldSaveServiceProvidedInDB() throws Exception {
        ReportingData data = serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", 
        		new Location("bherya", "Sub Center", "PHC X"), "entity id 1").withQuantity("40");

        controller.submit(data);

        verify(servicesProvidedRepository).save("ANM X", "ANM", "TC 1", "BCG", "2012-01-01", "bherya", "Sub Center", "PHC X", "40", "entity id 1");
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
        ReportingData data = serviceProvidedData("", null, BCG, null, new Location(null, null, null), null);

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
        data.put(DRISTHI_ENTITY_ID, "entity id 1");
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
    public void shouldDeleteAllServiceProvidedReportsGivenAnEntityID() throws Exception {
        ReportDataDeleteRequest deleteRequest = new ReportDataDeleteRequest()
                .withType("serviceProvided")
                .withDristhiEntityId("entity id 1");

        String result = controller.deleteReports(deleteRequest);

        verify(servicesProvidedRepository).delete(deleteRequest);
        verifyZeroInteractions(anmReportsRepository);
        assertEquals("Success.", result);
    }

    @Test
    public void shouldDeleteAllANMReportsGivenAnEntityID() throws Exception {
        ReportDataDeleteRequest deleteRequest = new ReportDataDeleteRequest()
                .withType("anmReportData")
                .withDristhiEntityId("entity id 1");

        String result = controller.deleteReports(deleteRequest);

        verify(anmReportsRepository).delete(deleteRequest);
        verifyZeroInteractions(servicesProvidedRepository);
        assertEquals("Success.", result);
    }

    @Test(expected = DristhiEntityIdMissingException.class)
    public void shouldThrowExceptionWhenDristhiEntityIdIsNotPresent() throws Exception {
        ReportDataDeleteRequest deleteRequestWithoutDristhiEntityId = new ReportDataDeleteRequest()
                .withType("serviceProvided");
        controller.deleteReports(deleteRequestWithoutDristhiEntityId);
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
}
