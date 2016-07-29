package org.opensrp.service.formSubmission.handler;

import org.opensrp.domain.Event;

public interface EventsHandler {
    public void handle(Event event);
}
