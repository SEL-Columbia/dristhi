package org.opensrp.register.service.handler;

import org.opensrp.domain.Event;
import org.opensrp.register.service.scheduling.ANCSchedulesService;
import org.opensrp.service.formSubmission.handler.EventsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ANCScheduleHandler implements EventsHandler {

	@Autowired
	private ANCSchedulesService ancScheduleService;

	@Override
	public void handle(Event event) {
		ancScheduleService.processEvent(event);
	}
}