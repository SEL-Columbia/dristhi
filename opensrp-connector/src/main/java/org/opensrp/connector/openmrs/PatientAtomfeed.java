package org.opensrp.connector.openmrs;

import org.apache.commons.lang3.StringUtils;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.repository.datasource.WebClient;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWork;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.connector.atomfeed.AtomfeedService;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.opensrp.connector.openmrs.service.OpenmrsService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.domain.Client;
import org.opensrp.domain.Obs;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PatientAtomfeed extends OpenmrsService implements EventWorker, AtomfeedService {

	private static final String CATEGORY_URL = "/OpenSRP_Patient/recent.form";

	private Logger log = Logger.getLogger(getClass().getSimpleName());

	private AtomFeedProperties atomFeedProperties;

	private AFTransactionManager transactionManager;

	private AtomFeedClient client;

	private PatientService patientService;

	private ClientService clientService;

	private EventService eventService;

	@Autowired
	public PatientAtomfeed(AllMarkers allMarkers, AllFailedEvents allFailedEvents,
	                       @Value("#{opensrp['openmrs.url']}") String baseUrl, PatientService patientService,
	                       ClientService clientService, EventService eventService) throws URISyntaxException {
		if (baseUrl != null) {
			OPENMRS_BASE_URL = baseUrl;
		}

		this.atomFeedProperties = new AtomFeedProperties();
		this.transactionManager = new AFTransactionManager() {

			@Override
			public <T> T executeWithTransaction(AFTransactionWork<T> action) throws RuntimeException {
				return action.execute();
			}
		};
		WebClient webClient = new WebClient();

		URI uri = new URI(OPENMRS_BASE_URL + OpenmrsConstants.ATOMFEED_URL + CATEGORY_URL);
		this.client = new AtomFeedClient(new AllFeeds(webClient), allMarkers, allFailedEvents, atomFeedProperties,
				transactionManager, uri, this);

		this.patientService = patientService;
		this.clientService = clientService;
		this.eventService = eventService;
	}

	@Override
	public void process(Event event) {
		log.info("Processing item : " + event.getContent());
		try {
			String content = event.getContent().substring(event.getContent().lastIndexOf("/") + 1);
			JSONObject p = patientService.getPatientByUuid(content, true);
			if (p == null) {
				throw new RuntimeException(
						"Patient uuid specified in atomfeed content (" + content + ") did not return any patient.");
			}
			Client c = patientService.convertToClient(p);
			Client existing = clientService.findClient(c);
			if (existing == null) {
				c.setBaseEntityId(UUID.randomUUID().toString());
				clientService.addClient(c);

				JSONObject newId = patientService.addThriveId(c.getBaseEntityId(), p);
				log.info("New Client -> Posted Thrive ID back to OpenMRS : " + newId);
			} else {
				String srpIdInOpenmrs = c.getBaseEntityId();
				Client cmerged = clientService.mergeClient(c);
				//TODO what if in any case thrive id is assigned to some other patient 
				if (StringUtils.isBlank(srpIdInOpenmrs) || !srpIdInOpenmrs.equalsIgnoreCase(cmerged.getBaseEntityId())) {
					// if openmrs doesnot have openSRP UID or have a different UID then update
					JSONObject newId = patientService.addThriveId(cmerged.getBaseEntityId(), p);
					log.info("Existing Client missing Valid SRP UID -> Posted Thrive ID back to OpenMRS : " + newId);
				}

				// Trigger an update to the client
				createEmptyUpdateClientEvent(existing);
			}
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void cleanUp(Event event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processEvents() {
		Logger.getLogger(getClass().getName()).info("Processing PatientAtomfeeds");
		client.processEvents();
	}

	@Override
	public void processFailedEvents() {
		client.processFailedEvents();
	}

	void setUrl(String url) {
		OPENMRS_BASE_URL = url;
	}

	PatientService getPatientService() {
		return patientService;
	}

	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	ClientService getClientService() {
		return clientService;
	}

	void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	/**
	 * Add an update birth registration event to trigger client update
	 *
	 * @param client
	 */
	private void createEmptyUpdateClientEvent(Client client) {
		try {
			Date start = new Date();
			final String START_END_CONCEPT = "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			final String CONCEPT = "concept";
			final String START = "start";
			final String END = "end";

			List<org.opensrp.domain.Event> events = eventService.findByBaseEntityId(client.getBaseEntityId());
			org.opensrp.domain.Event birthRegEvent = null;
			org.opensrp.domain.Event emptyUpdateBirthRegEvent = null;

			if (events == null || events.isEmpty()) {
				return;
			}

			for (org.opensrp.domain.Event event : events) {
				if (event.getEventType().equals("Birth Registration")) {
					birthRegEvent = event;
					break;
				}
			}

			if (birthRegEvent == null) {
				return;
			}

			for (org.opensrp.domain.Event event : events) {
				if (event.getEventType().equals("Update Birth Registration") && event.getObs().size() == 2) {
					boolean startEvent = false;
					boolean endEvent = false;
					for (Obs obs : event.getObs()) {
						if (obs.getFieldCode().equals(START_END_CONCEPT) && obs.getFieldType().equals(CONCEPT)) {
							if (obs.getFieldDataType().equals(START) && obs.getFormSubmissionField().equals(START)) {
								startEvent = true;
							} else if (obs.getFieldDataType().equals(END) && obs.getFormSubmissionField().equals(END)) {
								endEvent = true;

							}
						}
					}
					if (startEvent && endEvent) {
						emptyUpdateBirthRegEvent = event;
						break;
					}

				}

			}

			if (emptyUpdateBirthRegEvent != null) {
				emptyUpdateBirthRegEvent.setServerVersion(null);
				eventService.updateEvent(emptyUpdateBirthRegEvent);
			} else {

				org.opensrp.domain.Event event = (org.opensrp.domain.Event) new org.opensrp.domain.Event()
						.withBaseEntityId(client.getBaseEntityId()).withEventDate(client.getDateEdited())
						.withEventType("Update Birth Registration").withLocationId(birthRegEvent.getLocationId())
						.withProviderId(birthRegEvent.getProviderId()).withEntityType(birthRegEvent.getEntityType())
						.withFormSubmissionId(UUID.randomUUID().toString()).withDateCreated(new DateTime());

				SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar calendar = Calendar.getInstance();
				String end = DATE_TIME_FORMAT.format(calendar.getTime());

				List<Obs> obses = new ArrayList<>();
				Obs obs = new Obs();
				obs.setFieldCode(START_END_CONCEPT);
				obs.setValue(DATE_TIME_FORMAT.format(start));
				obs.setFieldType(CONCEPT);
				obs.setFieldDataType(START);
				obs.setFormSubmissionField(START);
				obses.add(obs);

				obs = new Obs();
				obs.setFieldCode(START_END_CONCEPT);
				obs.setValue(end);
				obs.setFieldType(CONCEPT);
				obs.setFieldDataType(END);
				obs.setFormSubmissionField(END);
				obses.add(obs);

				event.setObs(obses);

				eventService.addEvent(event);
			}

		}
		catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
