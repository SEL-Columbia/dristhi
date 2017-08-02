package org.opensrp.connector.openmrs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.connector.atomfeed.AtomfeedService;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.OpenmrsService;
import org.opensrp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncounterAtomfeed extends OpenmrsService implements EventWorker, AtomfeedService{
	private Logger log = Logger.getLogger(getClass().getSimpleName());

	private static final String CATEGORY_URL = "/OpenSRP_Encounter/recent.form";
	private AtomFeedProperties atomFeedProperties;

	private AFTransactionManager transactionManager;

	private WebClient webClient;
	private AtomFeedClient client;
	private EncounterService encounterService;
	private EventService eventService;
	
	@Autowired
	public EncounterAtomfeed(AllMarkers allMarkers, AllFailedEvents allFailedEvents, 
			@Value("#{opensrp['openmrs.url']}") String baseUrl, EncounterService encounterService,
			EventService eventService) throws URISyntaxException {
		if(baseUrl != null){
			OPENMRS_BASE_URL = baseUrl;
		}
		
		this.atomFeedProperties = new AtomFeedProperties();
		this.transactionManager = new AFTransactionManager(){
			@Override
			public <T> T executeWithTransaction(AFTransactionWork<T> action) throws RuntimeException {
				return action.execute();
			}
		};
		this.webClient = new WebClient();
		
		URI uri = new URI(OPENMRS_BASE_URL+OpenmrsConstants.ATOMFEED_URL+CATEGORY_URL);
		this.client = new AtomFeedClient(new AllFeeds(webClient), allMarkers, allFailedEvents, atomFeedProperties, transactionManager, uri, this);
		
		this.encounterService = encounterService;
		this.eventService = eventService;
	}
	
	@Override
	public void process(Event event) {
		log.info("Processing item : "+event.getContent());
		try {
			String content = event.getContent().substring(event.getContent().lastIndexOf("/")+1);
			JSONObject e = encounterService.getEncounterByUuid(content, true);
			if(e == null){
				throw new RuntimeException("Encounter uuid specified in atomfeed content ("+content+") did not return any encounter.");
			}
			
			log.info(e.toString());
			
			org.opensrp.domain.Event enc = encounterService.convertToEvent(e);
			org.opensrp.domain.Event existing = eventService.find(e.getString("uuid"));
			if(existing == null){
				log.info("New Event");
				eventService.addEvent(enc);
			}
			else {
				log.info("Update existing Event");
				enc = eventService.mergeEvent(enc);
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void cleanUp(Event event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processEvents() {
		log.info("Updating EncounterAtomfeeds");
		client.processEvents();
	}

	@Override
	public void processFailedEvents() {
		client.processFailedEvents();
	}
	
	public void setUrl(String url) {
		OPENMRS_BASE_URL = url;
	}
}
