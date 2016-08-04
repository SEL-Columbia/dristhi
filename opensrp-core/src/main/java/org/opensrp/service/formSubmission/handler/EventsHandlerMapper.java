package org.opensrp.service.formSubmission.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class EventsHandlerMapper {

	private static final Map<String, EventsHandler> handlerMap = new HashMap<String, EventsHandler>();
	
	public Map<String, EventsHandler> handlerMap() {
		return Collections.unmodifiableMap(handlerMap);
	}

	public EventsHandlerMapper addHandler(String formName, EventsHandler handler) {
		handlerMap.put(formName, handler);
		return this;
	}
}
