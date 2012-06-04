package org.ei.drishti.listener;

import org.ei.drishti.common.domain.ReportingData;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;

public class ReportEvent {
    public static final String SUBJECT = "REPORT";
    private ReportingData data;

    public ReportEvent(ReportingData data) {
        this.data = data;
    }

    public MotechEvent toEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("data", data);
        return new MotechEvent(SUBJECT, parameters);
    }
}
