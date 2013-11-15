package org.ei.drishti.reporting.controller;

import com.google.gson.Gson;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.repository.AllTokensRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregateReportsServiceTest {

    private AggregateReportsService aggregateReportsService;
    @Mock
    private HttpAgent httpAgent;
    @Mock
    private AllTokensRepository tokenRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        aggregateReportsService = new AggregateReportsService("bamboo.url", httpAgent, tokenRepository);
    }

    @Test
    public void shouldSendReportsToAggregator() throws Exception {
        ServiceProvidedReport firstServiceProvidedReport = new ServiceProvidedReport().withId(1);
        ServiceProvidedReport secondServiceProvidedReport = new ServiceProvidedReport().withId(2);
        String firstReportJson = new Gson().toJson(firstServiceProvidedReport);
        String secondReportJson = new Gson().toJson(secondServiceProvidedReport);
        when(httpAgent.put("bamboo.url", "update=" + firstReportJson, "application/json")).thenReturn(new HttpResponse(true, ""));
        when(httpAgent.put("bamboo.url", "update=" + secondReportJson, "application/json")).thenReturn(new HttpResponse(true, ""));

        aggregateReportsService.update(asList(firstServiceProvidedReport, secondServiceProvidedReport));

        verify(httpAgent).put("bamboo.url", "update=" + firstReportJson, "application/json");
        verify(httpAgent).put("bamboo.url", "update=" + secondReportJson, "application/json");
        verify(tokenRepository).saveAggregateReportsToken(1);
        verify(tokenRepository).saveAggregateReportsToken(2);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenReportsAreNotSent() throws Exception {
        ServiceProvidedReport firstServiceProvidedReport = new ServiceProvidedReport().withId(1);
        ServiceProvidedReport secondServiceProvidedReport = new ServiceProvidedReport().withId(2);
        String firstReportJson = new Gson().toJson(firstServiceProvidedReport);
        String secondReportAsJson = new Gson().toJson(secondServiceProvidedReport);
        when(httpAgent.put("bamboo.url", "update=" + firstReportJson, "application/json")).thenReturn(new HttpResponse(false, ""));

        aggregateReportsService.update(asList(firstServiceProvidedReport, secondServiceProvidedReport));

        verify(httpAgent).put("bamboo.url", "update=" + firstReportJson, "application/json");
        verify(httpAgent, never()).put("bamboo.url", "update=" + secondReportAsJson, "application/json");
    }
}
