package org.ei.drishti.service;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.listener.ReportEvent;
import org.ei.drishti.service.reporting.ReportingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.gateway.OutboundEventGateway;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportingServiceTest {
    @Mock
    private OutboundEventGateway gateway;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldPutReportingDataIntoQueue() throws Exception {
        ReportingService service = new ReportingService(gateway);

        ReportingData data = new ReportingData("Registration").with("mom", "Theresa");
        service.sendReportData(data);

        verify(gateway).sendEventMessage(new ReportEvent(data).toEvent());
    }
}
