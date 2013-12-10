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
import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregateReportsServiceTest {

    private AggregateReportsService aggregateReportsService;
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
        aggregateReportsService = new AggregateReportsService("bamboo.url", "bamboo.aggregated.url", httpAgent, tokenRepository, servicesProvidedRepository, reportMonth);
    }

    @Test
    public void shouldSendReportsWithNRHMReportingMonthToAggregator() throws Exception {
        when(tokenRepository.getAggregateReportsToken()).thenReturn(0);
        when(servicesProvidedRepository.getNewReports(0))
                .thenReturn(asList(new ServiceProvidedReport().withId(1).withDate(LocalDate.parse("2012-11-26").toDate()),
                        new ServiceProvidedReport().withId(2).withDate(LocalDate.parse("2012-12-28").toDate())));
        when(reportMonth.reportingMonth(LocalDate.parse("2012-11-26"))).thenReturn(12);
        when(reportMonth.reportingYear(LocalDate.parse("2012-11-26"))).thenReturn(2012);

        when(reportMonth.reportingMonth(LocalDate.parse("2012-12-28"))).thenReturn(1);
        when(reportMonth.reportingYear(LocalDate.parse("2012-12-28"))).thenReturn(2013);

        String firstReportJson = new Gson().toJson(new ServiceProvidedReportDTO().withDate(LocalDate.parse("2012-11-26")).withNRHMReportMonth(12).withNRHMReportYear(2012).withId(1));
        String secondReportJson = new Gson().toJson(new ServiceProvidedReportDTO().withDate(LocalDate.parse("2012-12-28")).withNRHMReportMonth(1).withNRHMReportYear(2013).withId(2));
        when(httpAgent.put("bamboo.url", mapOf("update", firstReportJson))).thenReturn(new HttpResponse(true, ""));
        when(httpAgent.put("bamboo.url", mapOf("update", secondReportJson))).thenReturn(new HttpResponse(true, ""));

        aggregateReportsService.sendReportsToAggregator();

        verify(httpAgent).put("bamboo.url", mapOf("update", firstReportJson));
        verify(httpAgent).put("bamboo.url", mapOf("update", secondReportJson));
        verify(tokenRepository).saveAggregateReportsToken(1);
        verify(tokenRepository).saveAggregateReportsToken(2);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenReportsAreNotSent() throws Exception {
        when(tokenRepository.getAggregateReportsToken()).thenReturn(0);
        ServiceProvidedReport firstServiceProvidedReport = new ServiceProvidedReport().withId(1);
        ServiceProvidedReport secondServiceProvidedReport = new ServiceProvidedReport().withId(2);
        when(servicesProvidedRepository.getNewReports(0)).thenReturn(asList(firstServiceProvidedReport, secondServiceProvidedReport));
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
        when(servicesProvidedRepository.getNewReports(12345)).thenReturn(Collections.<ServiceProvidedReport>emptyList());

        aggregateReportsService.sendReportsToAggregator();

        verify(httpAgent, never()).put(anyString(), anyMap());
        verify(tokenRepository, never()).saveAggregateReportsToken(anyInt());
    }

    @Test
    public void shouldCallAggregatorToGetAggregatedReports() throws Exception {
        when(httpAgent.get("bamboo.aggregated.url/summary?select=%7B%22indicator%22%3A1%7D&query=%7B%22anm_identifier%22%3A+%22demo1%22%2C%22nrhm_report_year%22%3A2013%2C+%22nrhm_report_month%22%3A3%7D"))
                .thenReturn(new HttpResponse(true, "{\"indicator\": {\"summary\": {\"OCP\": 1}}}"));

        AggregatorResponseDTO aggregatorResponse = aggregateReportsService.getAggregatedReports("demo1", 3, 2013);

        assertEquals(new AggregatorResponseDTO(mapOf("OCP", 1)), aggregatorResponse);
    }

    @Test
    public void shouldReturnEmptyDTOWhenThereAreNoAggregatedReports() throws Exception {
        when(httpAgent.get("bamboo.aggregated.url/summary?select=%7B%22indicator%22%3A1%7D&query=%7B%22anm_identifier%22%3A+%22demo1%22%2C%22nrhm_report_year%22%3A2013%2C+%22nrhm_report_month%22%3A3%7D"))
                .thenReturn(new HttpResponse(true, "{}"));

        AggregatorResponseDTO aggregatorResponse = aggregateReportsService.getAggregatedReports("demo1", 3, 2013);

        assertEquals(new AggregatorResponseDTO(new HashMap<String, Integer>()), aggregatorResponse);
    }
}
