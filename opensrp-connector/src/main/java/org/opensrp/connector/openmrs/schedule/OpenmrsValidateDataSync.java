package org.opensrp.connector.openmrs.schedule;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
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
	
	final String BIRTH_REGISTRATION_EVENT = "Birth Registration";
	
	final String GROWTH_MONITORING_EVENT = "Growth Monitoring";
	
	final String VACCINATION_EVENT = "Vaccination";
	
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
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_birth_reg_event_sync_validator_timestamp, 0,
		    "OpenMRS data Sync validation to keep track of Birth Registration event records were not successfully pushed to OpenMRS",
		    true);
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_growth_mon_event_sync_validator_timestamp, 0,
		    "OpenMRS data Sync validation to keep track of Growth Monitoring event records were not successfully pushed to OpenMRS",
		    true);
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_vaccine_event_sync_validator_timestamp, 0,
		    "OpenMRS data Sync validation to keep track of Vaccination event records were not successfully pushed to OpenMRS",
		    true);
		
	}
	
	public void syncToOpenMRS() {
		if (!lock.tryLock()) {
			logger.warn("Not fetching forms from Message Queue. It is already in progress.");
			return;
		}
		
		try {
			logger.info("VALIDATING CLIENTS - EVENTS " + DateTime.now());
			
			validateClient();
			
			validateEvent();
		}
		catch (Exception ex) {
			logger.error("", ex);
		}
		finally {
			lock.unlock();
		}
	}
	
	// Validate Client
	
	private void validateClient() {
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
		}
		catch (Exception e) {
			logger.error("", e);
			errorTraceService.log("OPENMRS FAILED CLIENT VALIDATION", Client.class.getName(), "",
			    ExceptionUtils.getStackTrace(e), "");
		}
	}
	
	private void pushValidateClient(List<Client> cl) throws JSONException {
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
	
	// Validate Event
	
	private void validateEvent() {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, -WAIT_TIME_IN_HOURS);
			
			boolean processed = processEvents(BIRTH_REGISTRATION_EVENT, calendar);
			if (processed) {
				return;
			}
			
			processed = processEvents(GROWTH_MONITORING_EVENT, calendar);
			if (processed) {
				return;
			}
			
			processed = processEvents(VACCINATION_EVENT, calendar);
			if (processed) {
				return;
			}
			
			AppStateToken lastValidated = config
			        .getAppStateTokenByName(SchedulerConfig.openmrs_event_sync_validator_timestamp);
			long start = lastValidated == null || lastValidated.getValue() == null ? 0 : lastValidated.longValue();
			
			List<Event> el = eventService.notInOpenMRSByServerVersion(start, calendar);
			logger.info("Events list size " + el.size());
			
			if (el != null && !el.isEmpty()) {
				pushValidateEvent(el, SchedulerConfig.openmrs_event_sync_validator_timestamp);
			}
			
		}
		catch (Exception e) {
			logger.error("", e);
			errorTraceService.log("OPENMRS FAILED EVENT VALIDATION", Event.class.getName(), "",
			    ExceptionUtils.getStackTrace(e), "");
		}
	}
	
	private boolean processEvents(String type, Calendar calendar) {
		try {
			if (StringUtils.isBlank(type)) {
				return false;
			}
			
			SchedulerConfig schedulerConfig = null;
			if (type.equals(BIRTH_REGISTRATION_EVENT)) {
				schedulerConfig = SchedulerConfig.openmrs_birth_reg_event_sync_validator_timestamp;
			} else if (type.equals(GROWTH_MONITORING_EVENT)) {
				schedulerConfig = SchedulerConfig.openmrs_growth_mon_event_sync_validator_timestamp;
			} else if (type.equals(VACCINATION_EVENT)) {
				schedulerConfig = SchedulerConfig.openmrs_vaccine_event_sync_validator_timestamp;
			} else {
				return false;
			}
			
			AppStateToken lastValidated = config.getAppStateTokenByName(schedulerConfig);
			long start = lastValidated == null || lastValidated.getValue() == null ? 0 : lastValidated.longValue();
			
			List<Event> el = eventService.notInOpenMRSByServerVersionAndType(type, start, calendar);
			logger.info(type + " Events list size " + el.size());
			
			if (el != null && !el.isEmpty()) {
				pushValidateEvent(el, schedulerConfig);
				return true;
			}
			
		}
		catch (Exception e) {
			logger.error("", e);
			errorTraceService.log("OPENMRS FAILED EVENT VALIDATION", Event.class.getName(), "",
			    ExceptionUtils.getStackTrace(e), "");
		}
		
		return false;
	}
	
	private JSONObject pushValidateEvent(List<Event> el, SchedulerConfig schedulerConfig) {
		JSONObject eventJson = null;
		for (Event e : el) {
			try {
				eventJson = encounterService.createEncounter(e);
				if (eventJson != null && eventJson.has("uuid")) {
					e.addIdentifier(EncounterService.OPENMRS_UUID_IDENTIFIER_TYPE, eventJson.getString("uuid"));
					eventService.updateEvent(e);
				}
			}
			catch (Exception ex2) {
				logger.error("", ex2);
				errorTraceService.log("OPENMRS FAILED EVENT VALIDATION", Event.class.getName(), e.getId(),
				    ExceptionUtils.getStackTrace(ex2), "");
			}
			
			config.updateAppStateToken(schedulerConfig, e.getServerVersion());
			
		}
		return eventJson;
	}
	
}
