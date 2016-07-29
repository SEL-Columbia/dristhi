package org.opensrp.service.formSubmission.handler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventsRouter {
	private static Logger logger = LoggerFactory.getLogger(EventsRouter.class.toString());
	@Autowired
	private HandlerMapper handlerMapper;

	@Value("#{opensrp['schedule.config.path']}")
	String scheduleConfigPath;
	private static final String JSON_KEY_SCHEDULES="schedules";

	public void route(Event event) {
		try {
			String scheduleConfigMappingStr = FileUtils.readFileToString(new File(scheduleConfigPath), "UTF-8");
			if (scheduleConfigMappingStr != null && !scheduleConfigMappingStr.isEmpty()) {
				JSONObject jsonObject = new JSONObject(scheduleConfigMappingStr);
				
			}
		} catch (IOException | JSONException e) {
			logger.error("", e);
		}

		// CustomFormSubmissionHandler handler =
		// handlerMapper.handlerMap().get(event.getEventType());

	}
}
