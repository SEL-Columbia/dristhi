package org.ei.drishti.event;

import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

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
