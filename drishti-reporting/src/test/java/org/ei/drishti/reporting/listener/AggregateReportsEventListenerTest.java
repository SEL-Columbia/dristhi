package org.ei.drishti.reporting.listener;

import org.ei.drishti.reporting.controller.BambooService;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.ei.drishti.reporting.repository.TokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregateReportsEventListenerTest {

    private AggregateReportsEventListener aggregateReportsEventListener;
    @Mock
    private ServicesProvidedRepository servicesProvidedRepository;
    @Mock
    private BambooService bambooService;
    @Mock
    private TokenRepository tokenRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        aggregateReportsEventListener = new AggregateReportsEventListener(servicesProvidedRepository, bambooService, tokenRepository);
    }

    @Test
    public void shouldAggregateReports() throws Exception {
        when(tokenRepository.getAggregateReportsToken()).thenReturn(12345);
        List<ServiceProvidedReport> serviceProvidedReportList = asList(new ServiceProvidedReport());
        when(servicesProvidedRepository.getNewerReportsAfter(12345)).thenReturn(serviceProvidedReportList);

        aggregateReportsEventListener.aggregate(new MotechEvent("REPORT_AGGREGATOR_SCHEDULE"));

        verify(bambooService).update(serviceProvidedReportList);
    }

    @Test
    public void shouldNotAggregateReportsWhenThereIsNoNewReport() throws Exception {
        when(tokenRepository.getAggregateReportsToken()).thenReturn(12345);
        when(servicesProvidedRepository.getNewerReportsAfter(12345)).thenReturn(Collections.<ServiceProvidedReport>emptyList());

        aggregateReportsEventListener.aggregate(new MotechEvent("REPORT_AGGREGATOR_SCHEDULE"));

        verify(bambooService, never()).update(anyList());
    }
}
