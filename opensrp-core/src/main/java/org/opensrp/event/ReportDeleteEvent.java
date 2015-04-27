package org.opensrp.event;

import java.util.HashMap;

import org.motechproject.scheduler.domain.MotechEvent;
import org.opensrp.common.domain.ReportDataDeleteRequest;

public class ReportDeleteEvent {
    public static final String SUBJECT = "DELETEREPORT";
    private ReportDataDeleteRequest data;

    public ReportDeleteEvent(ReportDataDeleteRequest data) {
        this.data = data;
    }

    public MotechEvent toEvent() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("data", data);
        return new MotechEvent(SUBJECT, parameters);
    }
}
