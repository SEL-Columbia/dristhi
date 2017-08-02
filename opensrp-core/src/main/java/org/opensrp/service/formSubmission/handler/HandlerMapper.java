package org.opensrp.service.formSubmission.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class HandlerMapper {

	private static final Map<String, CustomFormSubmissionHandler> customFormSubmissionHandlerMap = new HashMap<String, CustomFormSubmissionHandler>();
	private static final Map<String, FormSubmissionProcessedListener> onFormSubmissionProcessedListenerMap = new HashMap<>();
	
	public Map<String, CustomFormSubmissionHandler> customFormSubmissionHandlerMap() {
		return Collections.unmodifiableMap(customFormSubmissionHandlerMap);
	}

	public Map<String, FormSubmissionProcessedListener> formSubmissionProcessedListenerMap() {
		return Collections.unmodifiableMap(onFormSubmissionProcessedListenerMap);
	}
	
	public HandlerMapper addFormSubmissionProcessedListener(String formName, FormSubmissionProcessedListener listener) {
		onFormSubmissionProcessedListenerMap.put(formName, listener);
		return this;
	}
	
	public HandlerMapper addCustomFormSubmissionHandler(String formName, CustomFormSubmissionHandler handler) {
		customFormSubmissionHandlerMap.put(formName, handler);
		return this;
	}
}
