package org.opensrp.event;

import java.util.HashMap;

import org.motechproject.scheduler.domain.MotechEvent;
import org.opensrp.common.domain.ReportingData;

public class ReportEvent {
    public static final String SUBJECT = "REPORT";
    private ReportingData data;

    public ReportEvent(ReportingData data) {
        this.data = data;
    }

    public MotechEvent toEvent() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("data", data);
        return new MotechEvent(SUBJECT, parameters);
    }
}
