package org.opensrp.connector.openmrs.schedule;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants.SchedulerConfig;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.domain.AppStateToken;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.service.ClientService;
import org.opensrp.service.ConfigService;
import org.opensrp.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class OpenmrsValidateDataSync {

	private static final ReentrantLock lock = new ReentrantLock();

	private static Logger logger = LoggerFactory.getLogger(OpenmrsValidateDataSync.class.toString());

	private ConfigService config;

	private PatientService patientService;

	private ClientService clientService;

	private EventService eventService;

	private EncounterService encounterService;

	private static int WAIT_TIME_IN_HOURS = 12;

	@Autowired
	public OpenmrsValidateDataSync(ConfigService config, PatientService patientService, ClientService clientService,
	                               EventService eventService, EncounterService encounterService) {

		this.config = config;
		this.patientService = patientService;
		this.clientService = clientService;
		this.encounterService = encounterService;
		this.eventService = eventService;

		this.config.registerAppStateToken(SchedulerConfig.openmrs_client_sync_validator_timestamp, 0,
				"OpenMRS data Sync validation to keep track of client records were not successfully pushed to OpenMRS", true);

		this.config.registerAppStateToken(SchedulerConfig.openmrs_event_sync_validator_timestamp, 0,
				"OpenMRS data Sync validation to keep track of event records were not successfully pushed to OpenMRS", true);

	}

	@MotechListener(subjects = OpenmrsConstants.SCHEDULER_OPENMRS_SYNC_VALIDATOR_SUBJECT)
	public void syncToOpenMRS(MotechEvent event) {

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
			JSONArray patientsJsonArray = new JSONArray();

			patientService.processClients(cl, patientsJsonArray, SchedulerConfig.openmrs_client_sync_validator_timestamp,
					"OPENMRS FAILED CLIENT VALIDATION");

			logger.info("Events list size " + cl.size());
			lastValidated = config.getAppStateTokenByName(SchedulerConfig.openmrs_event_sync_validator_timestamp);
			start = lastValidated == null || lastValidated.getValue() == null ? 0 : lastValidated.longValue();
			List<Event> el = eventService.notInOpenMRSByServerVersion(start, calendar);

			encounterService.pushEvent(el, OpenmrsConstants.SchedulerConfig.openmrs_event_sync_validator_timestamp,
					"OPENMRS FAILED EVENT VALIDATION");

			logger.info("RUNNING FOR RELATIONSHIPS");
			patientService.createRelationShip(cl);

		}
		catch (Exception ex) {
			logger.error("", ex);
		}
		finally {
			lock.unlock();
		}
	}

}
