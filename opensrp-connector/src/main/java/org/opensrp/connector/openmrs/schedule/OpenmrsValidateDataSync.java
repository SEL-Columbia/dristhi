package org.opensrp.connector.openmrs.schedule;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
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
import org.opensrp.service.ErrorTraceService;
import org.opensrp.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class OpenmrsValidateDataSync {

	private static final ReentrantLock lock = new ReentrantLock();

	private static Logger logger = LoggerFactory.getLogger(OpenmrsValidateDataSync.class.toString());

	private ConfigService config;

	private ErrorTraceService errorTraceService;

	private PatientService patientService;

	private ClientService clientService;

	private EventService eventService;

	private EncounterService encounterService;

	private static int WAIT_TIME_IN_HOURS = 12;

	@Autowired
	public OpenmrsValidateDataSync(ConfigService config, ErrorTraceService errorTraceService, PatientService patientService,
	                               ClientService clientService, EventService eventService,
	                               EncounterService encounterService) {

		this.config = config;
		this.errorTraceService = errorTraceService;
		this.patientService = patientService;
		this.clientService = clientService;
		this.encounterService = encounterService;
		this.eventService = eventService;

		this.config.registerAppStateToken(SchedulerConfig.openmrs_client_sync_validator_timestamp, 0,
				"OpenMRS data Sync validation to keep track of records were not successfully pushed to OpenMRS", true);

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

			logger.info("VALIDATING CLIENTS " + DateTime.now());

			AppStateToken lastValidated = config
					.getAppStateTokenByName(SchedulerConfig.openmrs_client_sync_validator_timestamp);
			Long start = lastValidated == null || lastValidated.getValue() == null ? 0 : lastValidated.longValue();

			List<Client> cl = clientService.notInOpenMRSByServerVersion(start, calendar);
			logger.info("Clients list size " + cl.size());
			for (Client c : cl) {
				try {

					String uuid = null;
					JSONObject p = null;

					for (Entry<String, String> id : c.getIdentifiers().entrySet()) {
						p = patientService.getPatientByIdentifier(id.getValue());
						if (p != null) {
							break;
						}
					}
					if (p != null) {
						uuid = p.getString("uuid");
					}

					if (c.getIdentifiers().containsKey("M_ZEIR_ID")) {
						if (c.getBirthdate() == null) {
							c.setBirthdate(new DateTime("01-01-1960"));
						}
						c.setGender("Female");
					}

					if (uuid != null) {
						patientService.updatePatient(c, uuid);

						config.updateAppStateToken(SchedulerConfig.openmrs_client_sync_validator_timestamp,
								c.getServerVersion());
						clientService.addorUpdate(c, false);
					} else {
						JSONObject patientJson = patientService.createPatient(c);
						if (patientJson != null && patientJson.has("uuid")) {
							c.addIdentifier(PatientService.OPENMRS_UUID_IDENTIFIER_TYPE, patientJson.getString("uuid"));
							clientService.addorUpdate(c, false);

							config.updateAppStateToken(SchedulerConfig.openmrs_client_sync_validator_timestamp,
									c.getServerVersion());
						}

					}
				}
				catch (Exception ex1) {
					ex1.printStackTrace();
					errorTraceService.log("OPENMRS FAILED CLIENT VALIDATION", Client.class.getName(), c.getBaseEntityId(),
							ExceptionUtils.getStackTrace(ex1), "");
				}
			}

			//			running for events
			lastValidated = config.getAppStateTokenByName(SchedulerConfig.openmrs_event_sync_validator_timestamp);
			start = lastValidated == null || lastValidated.getValue() == null ? 0 : lastValidated.longValue();

			List<Event> el = eventService.notInOpenMRSByServerVersion(start, calendar);

			for (Event e : el) {
				try {
					JSONObject eventJson = encounterService.createEncounter(e);
					encounterService.processUpdateEvents(e);
					if (eventJson != null && eventJson.has("uuid")) {
						e.addIdentifier(EncounterService.OPENMRS_UUID_IDENTIFIER_TYPE, eventJson.getString("uuid"));
						eventService.updateEvent(e);
						config.updateAppStateToken(SchedulerConfig.openmrs_event_sync_validator_timestamp,
								e.getServerVersion());
					}

				}
				catch (Exception ex2) {
					logger.error("", ex2);
					errorTraceService.log("OPENMRS FAILED EVENT VALIDATION", Event.class.getName(), e.getId(),
							ExceptionUtils.getStackTrace(ex2), "");
				}
			}

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
