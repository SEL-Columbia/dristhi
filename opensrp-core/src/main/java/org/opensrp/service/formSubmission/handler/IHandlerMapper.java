package org.opensrp.service.formSubmission.handler;

import java.util.Map;

public interface IHandlerMapper {
	
	public Map<String, EventsHandler> handlerMap();
	
	public Map<String, EventsHandler> addHandler(String name, EventsHandler handler);
}
