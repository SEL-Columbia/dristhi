package org.ei.drishti.reporting.service;

import com.google.gson.Gson;
import org.ei.drishti.common.domain.ReportMonth;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.aggregatorResponse.AggregatorResponseDTO;
import org.ei.drishti.dto.report.ServiceProvidedReportDTO;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.repository.AllTokensRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregateReportsServiceTest {

    private AggregateReportsService aggregateReportsService;
    private int batchSizeToUpdate = 10;
    @Mock
    private HttpAgent httpAgent;
    @Mock
    private AllTokensRepository tokenRepository;
    @Mock
    private ServicesProvidedRepository servicesProvidedRepository;
    @Mock
    private ReportMonth reportMonth;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        aggregateReportsService = new AggregateReportsService("bamboo.url", "bamboo.aggregated.url", batchSizeToUpdate, httpAgent, tokenRepository, servicesProvidedRepository, reportMonth);
    }

    @Test
    public void shouldSendReportsWithNRHMReportingMonthToAggregator() throws Exception {
        when(tokenRepository.getAggregateReportsToken()).thenReturn(0);
        when(servicesProvidedRepository.getNewReports(0, batchSizeToUpdate))
                .thenReturn(asList(new ServiceProvidedReport().withId(1).withDate(LocalDate.parse("2012-11-26").toDate()),
                        new ServiceProvidedReport().withId(2).withDate(LocalDate.parse("2012-12-28").toDate())));
        when(reportMonth.reportingMonth(LocalDate.parse("2012-11-26"))).thenReturn(12);
        when(reportMonth.reportingYear(LocalDate.parse("2012-11-26"))).thenReturn(2012);

        when(reportMonth.reportingMonth(LocalDate.parse("2012-12-28"))).thenReturn(1);
        when(reportMonth.reportingYear(LocalDate.parse("2012-12-28"))).thenReturn(2013);

        String reportJson = new Gson().toJson(asList(
                new ServiceProvidedReportDTO().withDate(LocalDate.parse("2012-11-26")).withNRHMReportMonth(12).withNRHMReportYear(2012).withId(1),
                new ServiceProvidedReportDTO().withDate(LocalDate.parse("2012-12-28")).withNRHMReportMonth(1).withNRHMReportYear(2013).withId(2)));
        when(httpAgent.put("bamboo.url", mapOf("update", reportJson))).thenReturn(new HttpResponse(true, ""));

        aggregateReportsService.sendReportsToAggregator();

        verify(httpAgent).put("bamboo.url", mapOf("update", reportJson));
        verify(tokenRepository).saveAggregateReportsToken(2);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenReportsAreNotSent() throws Exception {
        when(tokenRepository.getAggregateReportsToken()).thenReturn(0);
        ServiceProvidedReport firstServiceProvidedReport = new ServiceProvidedReport().withId(1);
        ServiceProvidedReport secondServiceProvidedReport = new ServiceProvidedReport().withId(2);
        when(servicesProvidedRepository.getNewReports(0, batchSizeToUpdate)).thenReturn(asList(firstServiceProvidedReport, secondServiceProvidedReport));
        String firstReportJson = new Gson().toJson(firstServiceProvidedReport);
        String secondReportAsJson = new Gson().toJson(secondServiceProvidedReport);
        when(httpAgent.put("bamboo.url", mapOf("update", firstReportJson))).thenReturn(new HttpResponse(false, ""));

        aggregateReportsService.sendReportsToAggregator();

        verify(httpAgent).put("bamboo.url", mapOf("update", firstReportJson));
        verify(httpAgent, never()).put("bamboo.url", mapOf("update", secondReportAsJson));
    }

    @Test
    public void shouldNotAggregateReportsWhenThereIsNoNewReport() throws Exception {
        when(tokenRepository.getAggregateReportsToken()).thenReturn(12345);
        when(servicesProvidedRepository.getNewReports(12345, batchSizeToUpdate)).thenReturn(Collections.<ServiceProvidedReport>emptyList());

        aggregateReportsService.sendReportsToAggregator();

        verify(httpAgent, never()).put(anyString(), anyMap());
        verify(tokenRepository, never()).saveAggregateReportsToken(anyInt());
    }

    @Test
    public void shouldCallAggregatorToGetAggregatedReports() throws Exception {
        when(httpAgent.get("bamboo.aggregated.url?query=%7B%22anm_identifier%22%3A+%22demo1%22%2C%22nrhm_report_year%22%3A2013%2C+%22nrhm_report_month%22%3A10%7D"))
                .thenReturn(new HttpResponse(true, "[{\"nrhm_report_month\": 10, \"indicator\": \"OCP\", \"nrhm_report_indicator_count\": 2, \"nrhm_report_year\": 2013, \"anm_identifier\": \"demo1\"}, {\"nrhm_report_month\": 10, \"indicator\": \"OPV_1\", \"nrhm_report_indicator_count\": 1, \"nrhm_report_year\": 2013, \"anm_identifier\": \"demo1\"}]"));

        List<AggregatorResponseDTO> aggregatorResponse = aggregateReportsService.getAggregatedReports("demo1", 10, 2013);

        assertEquals(asList(new AggregatorResponseDTO("OCP", 2), new AggregatorResponseDTO("OPV_1", 1)), aggregatorResponse);
    }
}
