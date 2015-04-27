package org.opensrp.event;

import java.util.HashMap;

import org.motechproject.scheduler.domain.MotechEvent;
import org.opensrp.common.domain.ReportDataUpdateRequest;

public class ReportUpdateEvent {
    public static final String SUBJECT = "UPDATEREPORT";
    private ReportDataUpdateRequest data;

    public ReportUpdateEvent(ReportDataUpdateRequest data) {
        this.data = data;
    }

    public MotechEvent toEvent() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("data", data);
        return new MotechEvent(SUBJECT, parameters);
    }
}
