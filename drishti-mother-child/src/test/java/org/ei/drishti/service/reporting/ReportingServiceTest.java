package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportDataDeleteRequest;
import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.event.ReportDeleteEvent;
import org.ei.drishti.event.ReportEvent;
import org.ei.drishti.event.ReportUpdateEvent;
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

    @Test
    public void shouldPutReportingDataUpdateRequestIntoQueue() throws Exception {
        ReportingService service = new ReportingService(gateway);

        ReportDataUpdateRequest request = ReportDataUpdateRequest.buildReportDataRequest("anmReport", Indicator.ANC, "2012-12-25", "2013-01-01", null);
        service.updateReportData(request);

        verify(gateway).sendEventMessage(new ReportUpdateEvent(request).toEvent());
    }

    @Test
    public void shouldPutReportingDataDeleteRequestIntoQueue() throws Exception {
        ReportingService service = new ReportingService(gateway);

        ReportDataDeleteRequest request = ReportDataDeleteRequest.serviceProvidedDataDeleteRequest("entity id 1");
        service.deleteReportData(request);

        verify(gateway).sendEventMessage(new ReportDeleteEvent(request).toEvent());
    }
}
