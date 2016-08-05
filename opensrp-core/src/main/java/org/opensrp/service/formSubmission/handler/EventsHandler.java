package org.opensrp.service.formSubmission.handler;

import org.json.JSONObject;
import org.opensrp.domain.Event;

public interface EventsHandler {
    public void handle(Event event, JSONObject scheduleConfigEvent);
}
