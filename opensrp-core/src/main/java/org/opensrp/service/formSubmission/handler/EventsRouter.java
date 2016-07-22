package org.opensrp.service.formSubmission.handler;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

import org.opensrp.domain.Event;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventsRouter {
	private static Logger logger = LoggerFactory.getLogger(EventsRouter.class.toString());
	private FormSubmissionService formSubmissionService;
	@Autowired
	private HandlerMapper handlerMapper;


	
	public void route(Event event) throws Exception {
//		CustomFormSubmissionHandler handler = handlerMapper.handlerMap().get(event.getEventType());
//		if (handler == null) {
//			logger.warn(format("Could not find a handler due to unknown form submission ( {0} ) with instance Id: {1} for entity: {2}",
//				formSubmission.formName(), formSubmission.instanceId(), formSubmission.entityId()));
//			return;
//		}
//		logger.info(format("Handling {0} form submission with instance Id: {1} for entity: {2}",
//				formSubmission.formName(), formSubmission.instanceId(), formSubmission.entityId()));
//		try {
//			handler.handle(formSubmission);
//		} catch (Exception e) {
//			logger.error(format("Handling {0} form submission with instance Id: {1} for entity: {2} failed with exception : {3}",
//					formSubmission.formName(), formSubmission.instanceId(), formSubmission.entityId(), getFullStackTrace(e)));
//			throw e;
//		}
	}
}
