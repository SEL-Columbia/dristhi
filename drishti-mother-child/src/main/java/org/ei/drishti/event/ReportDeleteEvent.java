package org.ei.drishti.event;

import org.ei.drishti.common.domain.ReportDataDeleteRequest;
import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

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
