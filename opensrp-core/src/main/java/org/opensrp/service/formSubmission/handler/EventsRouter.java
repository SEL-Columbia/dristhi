package org.opensrp.service.formSubmission.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class EventsRouter {
	
	private static Logger logger = LoggerFactory.getLogger(EventsRouter.class.toString());
	 @Autowired
	 private EventsHandlerMapper handlerMapper;
	
	// @Value("#{opensrp['schedule.config.json.path']}")
	// String scheduleConfigPath;
	@Value("#{opensrp['schedules.config.files.dir']}")
	String scheduleConfigFilesPath;
	
	// private static final String JSON_KEY_SCHEDULES = "schedules";
	private static final String JSON_KEY_HANDLER = "handler";
	
	private static final String JSON_KEY_EVENTS = "events";
	
	ResourceLoader loader = new DefaultResourceLoader();
	
	/**
	 * @param event
	 */
	public void route(Event event) {
		try {
			
			// scheduleConfigPath =
			// loader.getResource(scheduleConfigPath).getURI().getPath();
			// String scheduleConfigMappingStr = FileUtils.readFileToString(new
			// File(scheduleConfigPath), "UTF-8");
			
			if (scheduleConfigFilesPath != null && !scheduleConfigFilesPath.isEmpty()) {
				// JSONObject jsonObject = new
				// JSONObject(scheduleConfigMappingStr);
				// JSONArray schedulesRules =
				// jsonObject.getJSONArray("schedules_handling_rules");
				
				// JSONObject schedules = schedulesRules.getJSONObject(0);
				
				String schedulesStr = getScheduleConfigs();
				System.out.println(schedulesStr);
				
				JSONArray schedulesJsonObject = new JSONArray("[" + schedulesStr + "]");
				// schedules.put("schedules", schedulesJsonObject);
				
				for (int i = 0; i < schedulesJsonObject.length(); i++) {
					JSONObject scheduleJsonObject = schedulesJsonObject.getJSONObject(i);
					String handler = scheduleJsonObject.getString(JSON_KEY_HANDLER);
					JSONArray eventsJsonArray = scheduleJsonObject.getJSONArray(JSON_KEY_EVENTS);
					//iterate through the events to see if the current event (the one passed to this route method) has a schedule handler
					List<String> eventsList = jsonArrayToList(eventsJsonArray);
					if (eventsList.contains(event.getEntityType())) {
						EventsHandler eventHandler = handlerMapper.handlerMap().get(handler);
						eventHandler.handle(event,eventsJsonArray);
					}
					
				}
				
			}
		}
		catch (IOException | JSONException e) {
			logger.error("", e);
		}
		
		// CustomFormSubmissionHandler handler =
		// handlerMapper.handlerMap().get(event.getEventType());
		
	}
	
	// private void routeEvent()
	/**
	 * This method merges all the files in the schedule-configs folder to create one
	 * jsonobject/array
	 * 
	 * @return
	 * @throws IOException
	 */
	private String getScheduleConfigs() throws IOException {
		scheduleConfigFilesPath = loader.getResource(scheduleConfigFilesPath).getURI().getPath();
		File scheduleConfigsFolder = new File(scheduleConfigFilesPath);
		String scheduleConfigMapping = "";
		File[] scheduleFiles = scheduleConfigsFolder.listFiles();
		for (int i = 0; i < scheduleFiles.length; i++) {
			final File fileEntry = scheduleFiles[i];
			String scheduleConfig = FileUtils.readFileToString(new File(fileEntry.getAbsolutePath()), "UTF-8");
			scheduleConfigMapping += (i + 1 == scheduleFiles.length) ? scheduleConfig : scheduleConfig.concat(",");
			
		}
		return scheduleConfigMapping;
	}
	
	private List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
		List<String> values = new ArrayList<String>();
		if (jsonArray == null) {
			return values;
		}
		
		for (int i = 0; i < jsonArray.length(); i++) {
			values.add((String) jsonArray.get(i));
		}
		return values;
	}
}
