package org.opensrp.register.service.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.opensrp.service.formSubmission.handler.EventsHandler;
import org.opensrp.service.formSubmission.handler.IHandlerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
/**
 * 
 * @author coder
 *This is the class that's called in the eventsrouter to retrieve the handlers for the different schedules based on an event
 *NB: Due to circular referencing opensrp-register can't be imported in opensrp-core and therefore this class is only accessible through the 
 *IHandlerMapper in opensrp-core. That way to debug the eventsrouter class, you must do it from the webapp through JPA else create a test in opensrp-web which has all the modules 
 *and launch the class from there
 */
@Repository
public class EventsHandlerMapper implements IHandlerMapper{
	
	private static final Map<String, EventsHandler> handlerMap = new HashMap<String, EventsHandler>();
	
	@Autowired
	public EventsHandlerMapper(ANCScheduleHandler ancScheduleHanlder) {
		handlerMap.put("ANC", ancScheduleHanlder);
		
	}
	
	public Map<String, EventsHandler> handlerMap() {
		return Collections.unmodifiableMap(handlerMap);
	}
	
	public Map<String, EventsHandler> addHandler(String formName, EventsHandler handler) {
		handlerMap.put(formName, handler);
		return handlerMap;
	}

	
}
