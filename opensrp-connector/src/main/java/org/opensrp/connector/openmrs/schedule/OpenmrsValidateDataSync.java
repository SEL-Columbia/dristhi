package org.opensrp.connector.openmrs.schedule;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants.SchedulerConfig;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.domain.AppStateToken;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.service.ClientService;
import org.opensrp.service.ConfigService;
import org.opensrp.service.ErrorTraceService;
import org.opensrp.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenmrsValidateDataSync {
	
	private static final ReentrantLock lock = new ReentrantLock();
	
	private static Logger logger = LoggerFactory.getLogger(OpenmrsValidateDataSync.class.toString());
	
	private ConfigService config;
	
	private PatientService patientService;
	
	private ClientService clientService;
	
	private EventService eventService;
	
	private EncounterService encounterService;
	
	private ErrorTraceService errorTraceService;
	
	private static int WAIT_TIME_IN_HOURS = 12;
	
	@Autowired
	public OpenmrsValidateDataSync(ConfigService config, PatientService patientService, ClientService clientService,
	    EventService eventService, EncounterService encounterService, ErrorTraceService errorTraceService) {
		
		this.config = config;
		this.patientService = patientService;
		this.clientService = clientService;
		this.encounterService = encounterService;
		this.eventService = eventService;
		this.errorTraceService = errorTraceService;
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_client_sync_validator_timestamp, 0,
		    "OpenMRS data Sync validation to keep track of client records were not successfully pushed to OpenMRS", true);
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_event_sync_validator_timestamp, 0,
		    "OpenMRS data Sync validation to keep track of event records were not successfully pushed to OpenMRS", true);
		
	}
	
	public void syncToOpenMRS() {
		
		if (!lock.tryLock()) {
			logger.warn("Not fetching forms from Message Queue. It is already in progress.");
			return;
		}
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, -WAIT_TIME_IN_HOURS);
			
			logger.info("VALIDATING CLIENTS - EVENTS " + DateTime.now());
			
			AppStateToken lastValidated = config
			        .getAppStateTokenByName(SchedulerConfig.openmrs_client_sync_validator_timestamp);
			Long start = lastValidated == null || lastValidated.getValue() == null ? 0 : lastValidated.longValue();
			
			List<Client> cl = clientService.notInOpenMRSByServerVersion(start, calendar);
			logger.info("Clients_list_size " + cl.size());
			
			pushValidateClient(cl);
			
			lastValidated = config.getAppStateTokenByName(SchedulerConfig.openmrs_event_sync_validator_timestamp);
			start = lastValidated == null || lastValidated.getValue() == null ? 0 : lastValidated.longValue();
			
			List<Event> el = eventService.notInOpenMRSByServerVersion(start, calendar);
			logger.info("Events list size " + el.size());
			
			pushValidateEvent(el);
			
		}
		catch (Exception ex) {
			logger.error("", ex);
		}
		finally {
			lock.unlock();
		}
	}
	
	public void pushValidateClient(List<Client> cl) throws JSONException {
		try {
			
			JSONArray patientsJsonArray = new JSONArray();
			
			if (cl != null && !cl.isEmpty()) {
				patientService.processClients(cl, patientsJsonArray, SchedulerConfig.openmrs_client_sync_validator_timestamp,
				    "OPENMRS FAILED CLIENT VALIDATION");
				
				logger.info("RUNNING FOR RELATIONSHIPS");
				patientService.createRelationShip(cl, "OPENMRS FAILED CLIENT RELATIONSHIP VALIDATION");
			}
		}
		catch (Exception e) {
			logger.error("", e);
			errorTraceService.log("OPENMRS FAILED CLIENT VALIDATION", Client.class.getName(), "",
			    ExceptionUtils.getStackTrace(e), "");
		}
	}
	
	public JSONObject pushValidateEvent(List<Event> el) {
		JSONObject eventJson = null;
		for (Event e : el) {
			try {
				eventJson = encounterService.createEncounter(e);
				if (eventJson != null && eventJson.has("uuid")) {
					e.addIdentifier(EncounterService.OPENMRS_UUID_IDENTIFIER_TYPE, eventJson.getString("uuid"));
					eventService.updateEvent(e);
					config.updateAppStateToken(SchedulerConfig.openmrs_event_sync_validator_timestamp, e.getServerVersion());
				}
			}
			catch (Exception ex2) {
				logger.error("", ex2);
				errorTraceService.log("OPENMRS FAILED EVENT VALIDATION", Event.class.getName(), e.getId(),
				    ExceptionUtils.getStackTrace(ex2), "");
			}
		}
		return eventJson;
	}
	
}
