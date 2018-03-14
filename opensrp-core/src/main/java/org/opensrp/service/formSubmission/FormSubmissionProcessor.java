package org.opensrp.service.formSubmission;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.domain.SubFormData;
import org.opensrp.repository.couch.AllClients;
import org.opensrp.repository.couch.AllEvents;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.Schedule;
import org.opensrp.scheduler.Schedule.ActionType;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.service.formSubmission.handler.FormSubmissionRouter;
import org.opensrp.service.formSubmission.ziggy.ZiggyService;
import org.opensrp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;

@Service
public class FormSubmissionProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(FormSubmissionListener.class.toString());
	
	private ZiggyService ziggyService;
	
	private FormSubmissionRouter formSubmissionRouter;
	
	private FormEntityConverter formEntityConverter;
	
	private ClientService clientService;
	
	private EventService eventService;
	
	private HealthSchedulerService scheduleService;
	
	private final AllClients allClients;
	
	private final AllEvents allEvents;
	
	@Autowired
	public FormSubmissionProcessor(ZiggyService ziggyService, FormSubmissionRouter formSubmissionRouter,
	    FormEntityConverter formEntityConverter, HealthSchedulerService scheduleService, ClientService clientService,
	    @Qualifier("couchClientsRepository") AllClients allClients, EventService eventService,
	    @Qualifier("couchEventsRepository") AllEvents allEvents) throws IOException {
		this.ziggyService = ziggyService;
		this.formSubmissionRouter = formSubmissionRouter;
		this.formEntityConverter = formEntityConverter;
		this.scheduleService = scheduleService;
		this.clientService = clientService;
		this.eventService = eventService;
		this.allClients = allClients;
		this.allEvents=allEvents;
	}
	
	public void processFormSubmission(FormSubmission submission) throws Exception {
		// parse and into client and event model
		logger.info("Creating model entities");
		makeModelEntities(submission);
		logger.info("Handling xls configured schedules");
		handleSchedules(submission);
		if (ziggyService.isZiggyCompliant(submission.bindType())) {
			passToZiggy(submission);
			//and skip form submission routing as ziggy does it automatically
		} else {//if not ziggy entity call custom route handler explicitly
			logger.info("Routing to custom handler");
			formSubmissionRouter.route(submission);
		}
	}
	
	void handleSchedules(FormSubmission submission) throws JSONException, IOException {
		List<Schedule> schl = scheduleService.findAutomatedSchedules(submission.formName());
		for (Schedule sch : schl) {
			Map<String, String> entsch = getEntitiesQualifyingForSchedule(submission, sch);
			System.out.println("creating schedule for : " + entsch);
			System.out.println(new Gson().toJson(sch));
			
			for (String enid : entsch.keySet()) {
				if (sch.action().equals(ActionType.enroll)) {
					scheduleService.enrollIntoSchedule(enid, sch.schedule(), sch.milestone(), entsch.get(enid),
					    submission.instanceId());
				} else if (sch.action().equals(ActionType.fulfill)) {
					scheduleService.fullfillMilestoneAndCloseAlert(enid, submission.anmId(), sch.schedule(),
					    LocalDate.parse(entsch.get(enid)), submission.instanceId());
				} else if (sch.action().equals(ActionType.unenroll)) {
					scheduleService.unEnrollFromSchedule(enid, submission.anmId(), sch.schedule(), submission.instanceId());
				} else if (sch.action().equals(ActionType.unenroll) && sch.schedule().equalsIgnoreCase("*")) {
					scheduleService.unEnrollFromAllSchedules(enid, submission.instanceId());
				}
			}
		}
	}
	
	Map<String, String> getEntitiesQualifyingForSchedule(FormSubmission submission, Schedule schedule) throws JSONException {
		Map<String, String> entityIds = new HashMap<String, String>();
		if (schedule.applicableForEntity(submission.bindType())) {
			String res = evaluateScheduleFor(schedule, submission.instance().form().getFieldsAsMap());
			if (!StringUtils.isEmptyOrWhitespaceOnly(res)) {
				entityIds.put(submission.entityId(), res);
			}
		}
		
		if (submission.subForms() != null) {
			for (SubFormData subFormData : submission.subForms()) {
				if (schedule.applicableForEntity(subFormData.bindType())) {
					for (Map<String, String> instance : subFormData.instances()) {
						String res = evaluateScheduleFor(schedule, instance);
						if (!StringUtils.isEmptyOrWhitespaceOnly(res)) {
							entityIds.put(instance.get("id"), res);
						}
					}
				}
			}
		}
		return entityIds;
	}
	
	String evaluateScheduleFor(Schedule schedule, Map<String, String> flvl) {
		//find first field in submission that qualifies triggerdate field and has a value
		for (String tf : schedule.triggerDateFields()) {
			String flv = flvl.get(tf);
			// if field has value and schedule flag field is empty or has value 1 or true
			if (!StringUtils.isEmptyOrWhitespaceOnly(flv) && schedule.passesValidations(flvl)) {
				return flv;
			}
		}
		return null;
	}
	
	public void makeModelEntities(FormSubmission submission) throws JSONException {
		if (submission.getInstanceId().equalsIgnoreCase("b7dfb183-97a9-4bd1-8f1c-d85f88189d6a")) {
			logger.debug("" + submission.getInstanceId());
		}
		Client c = formEntityConverter.getClientFromFormSubmission(submission);
		Event e = formEntityConverter.getEventFromFormSubmission(submission);
		Map<String, Map<String, Object>> dep = formEntityConverter.getDependentClientsFromFormSubmission(submission);
		
		if (clientService.findClient(c) != null) {
			clientService.mergeClient(c);
		} else
			clientService.addClient(c);
		
		eventService.addEvent(e);
		// TODO relationships b/w entities
		
		for (Map<String, Object> cm : dep.values()) {
			Client cin = (Client) cm.get("client");
			Event evin = (Event) cm.get("event");
			clientService.addClient(cin);
			eventService.addEvent(evin);
		}
	}
	
	/**
	 * Find a client from the specified db
	 *
	 * @param targetDb
	 * @param client
	 * @return
	 */
	public Client findClient(CouchDbConnector targetDb, Client client) {
		// find by auto assigned entity id
		try {
			Client c = allClients.findByBaseEntityId(client.getBaseEntityId());
			if (c != null) {
				return c;
			}
			
			//still not found!! search by generic identifiers
			
			for (String idt : client.getIdentifiers().keySet()) {
				List<Client> cl = allClients.findAllByIdentifier(targetDb, client.getIdentifier(idt));
				if (cl.size() > 1) {
					throw new IllegalArgumentException("Multiple clients with identifier type " + idt + " and ID "
					        + client.getIdentifier(idt) + " exist.");
				} else if (cl.size() != 0) {
					return cl.get(0);
				}
			}
			return c;
		}
		catch (Exception e) {
			
			return null;
		}
	}
	
	public Client addClient(CouchDbConnector targetDb, Client client) {
		if (client.getBaseEntityId() == null) {
			throw new IllegalArgumentException("No baseEntityId");
		}
		Client c = findClient(targetDb, client);
		if (c != null) {
			throw new IllegalArgumentException(
			        "A client already exists with given list of identifiers. Consider updating data.[" + c + "]");
		}
		
		client.setDateCreated(new DateTime());
		allClients.add(targetDb, client);
		return client;
	}
	
	public synchronized Event addEvent(CouchDbConnector targetDb, Event event) {
		//		Event e = find(targetDb,event);
		//		if(e != null){
		//			throw new IllegalArgumentException("An event already exists with given list of identifiers. Consider updating data.["+e+"]");
		//		}
		if (event.getFormSubmissionId() != null && getByBaseEntityAndFormSubmissionId(targetDb, event.getBaseEntityId(),
		    event.getFormSubmissionId()) != null) {
			throw new IllegalArgumentException(
			        "An event already exists with given baseEntity and formSubmission combination. Consider updating");
		}
		
		event.setDateCreated(new DateTime());
		
		allEvents.add(targetDb, event);
		return event;
	}
	
	public Event getByBaseEntityAndFormSubmissionId(CouchDbConnector targetDb, String baseEntityId,
	                                                String formSubmissionId) {
		try {
			List<Event> el = allEvents.findByBaseEntityAndFormSubmissionId(targetDb, baseEntityId, formSubmissionId);
			if (el.size() > 1) {
				throw new IllegalStateException("Multiple events for baseEntityId and formSubmissionId combination ("
				        + baseEntityId + "," + formSubmissionId + ")");
			}
			if (el.size() == 0) {
				return null;
			}
			return el.get(0);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Break down form submission and save it to a target db
	 *
	 * @param targetDb
	 * @param submission
	 * @throws JSONException
	 */
	public void makeModelEntities(CouchDbConnector targetDb, FormSubmission submission) throws JSONException {
		Client c = formEntityConverter.getClientFromFormSubmission(submission);
		Event e = formEntityConverter.getEventFromFormSubmission(submission);
		Map<String, Map<String, Object>> dep = formEntityConverter.getDependentClientsFromFormSubmission(submission);
		
		if (findClient(targetDb, c) != null) {
			clientService.mergeClient(c);
		} else
			addClient(targetDb, c);
		
		addEvent(targetDb, e);
		// TODO relationships b/w entities
		
		for (Map<String, Object> cm : dep.values()) {
			Client cin = (Client) cm.get("client");
			Event evin = (Event) cm.get("event");
			addClient(targetDb, cin);
			addEvent(targetDb, evin);
		}
	}
	
	private void passToZiggy(FormSubmission submission) {
		String params = Utils.getZiggyParams(submission);
		ziggyService.saveForm(params, new Gson().toJson(submission.instance()));
	}
	
}
