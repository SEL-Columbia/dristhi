package org.opensrp.service.reporting;

import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.opensrp.common.domain.ReportDataDeleteRequest;
import org.opensrp.common.domain.ReportDataUpdateRequest;
import org.opensrp.common.domain.ReportingData;
import org.opensrp.event.ReportDeleteEvent;
import org.opensrp.event.ReportEvent;
import org.opensrp.event.ReportUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {
    private OutboundEventGateway gateway;

    @Autowired
    public ReportingService(OutboundEventGateway gateway) {
        this.gateway = gateway;
    }

    public void sendReportData(ReportingData data) {
        gateway.sendEventMessage(new ReportEvent(data).toEvent());
    }

    public void updateReportData(ReportDataUpdateRequest dataRequest) {
        gateway.sendEventMessage(new ReportUpdateEvent(dataRequest).toEvent());
    }

    public void deleteReportData(ReportDataDeleteRequest deleteRequest) {
        gateway.sendEventMessage(new ReportDeleteEvent(deleteRequest).toEvent());
    }
}