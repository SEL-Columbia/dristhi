package org.opensrp.service.formSubmission.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class HandlerMapper {

	private static final Map<String, CustomFormSubmissionHandler> handlerMap = new HashMap<String, CustomFormSubmissionHandler>();
	
	public Map<String, CustomFormSubmissionHandler> handlerMap() {
		return Collections.unmodifiableMap(handlerMap);
	}

	public HandlerMapper addHandler(String formName, CustomFormSubmissionHandler handler) {
		handlerMap.put(formName, handler);
		return this;
	}
}
