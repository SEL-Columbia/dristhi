package org.opensrp.connector.openmrs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

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
import org.opensrp.connector.openmrs.service.OpenmrsService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.domain.Client;
import org.opensrp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PatientAtomfeed extends OpenmrsService implements EventWorker, AtomfeedService{

	private static final String CATEGORY_URL = "/patient/recent.form";
	private AtomFeedProperties atomFeedProperties;

	private AFTransactionManager transactionManager;

	private WebClient webClient;
	private AtomFeedClient client;

	@Autowired
	public PatientAtomfeed(AllMarkers allMarkers, AllFailedEvents allFailedEvents, 
			@Value("#{opensrp['openmrs.url']}") String baseUrl, PatientService patientService, ClientService clientService) throws URISyntaxException {
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
	
		this.patientService = patientService;
		this.clientService = clientService;
	}
	
	private PatientService patientService;
	
	private ClientService clientService;
	

	@Override
	public void process(Event event) {
		System.out.println(event.getContent());
		try {
			JSONObject p = patientService.getPatientByUuid(event.getContent().substring(event.getContent().lastIndexOf("/")+1), true);
			System.out.println(p);//TODO check in couch and if not exists update thrive id on opermrs side
			Client c = patientService.convertToClient(p);
			Client existing = clientService.findClient(c);
			if(existing == null){
				c.setBaseEntityId(UUID.randomUUID().toString());
				clientService.addClient(c);

				System.out.println(patientService.addThriveId(c.getBaseEntityId(), p));
			}
			else {
				c = clientService.mergeClient(c);
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
	
}