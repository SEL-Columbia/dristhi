package org.opensrp.service.formSubmission;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.domain.SubFormData;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.service.formSubmission.handler.FormSubmissionRouter;
import org.opensrp.service.formSubmission.ziggy.ZiggyService;
import org.opensrp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class FormSubmissionProcessor{

    private ZiggyService ziggyService;
    private FormSubmissionRouter formSubmissionRouter;
    private FormEntityConverter formEntityConverter;
    private ClientService clientService;
    private EventService eventService;
    private HealthSchedulerService scheduleService;
    private String scheduleConfigPath;
    
    @Autowired
    public FormSubmissionProcessor(@Value("#{opensrp['schedule.config.path']}") String scheduleConfigPath,
    		ZiggyService ziggyService, FormSubmissionRouter formSubmissionRouter,
    		FormEntityConverter formEntityConverter, HealthSchedulerService scheduleService, 
    		ClientService clientService, EventService eventService) {
    	this.scheduleConfigPath = scheduleConfigPath;
		this.ziggyService = ziggyService;
		this.formSubmissionRouter = formSubmissionRouter;
		this.formEntityConverter = formEntityConverter;
		this.scheduleService = scheduleService;
		this.clientService = clientService;
		this.eventService = eventService;
    }

    public void processFormSubmission(FormSubmission submission) throws Exception {
    	// parse and into client and event model
    	makeModelEntities(submission);
    	handleSchedules(submission);
    	if(ziggyService.isZiggyCompliant(submission.bindType())){
    		passToZiggy(submission);
    		//and skip form submission routing as ziggy does it automatically
    	}
    	else {//if not ziggy entity call custom route handler explicitly
    		formSubmissionRouter.route(submission);
    	}
	}
    
    private void handleSchedules(FormSubmission submission) throws JSONException, IOException {
    	JSONArray schapp = Utils.getSchedules(submission, scheduleConfigPath);
    	for (int i = 0; i < schapp.length(); i++) {
			JSONObject sch = schapp.getJSONObject(i);
			Map<String, String> entsch = getEntitiesQualifyingForSchedule(submission, sch);
			System.out.println("creating schedule for : "+entsch);
			for (String enid : entsch.keySet()) {
				if(sch.getString("action").startsWith("enroll")){
					scheduleService.enrollIntoSchedule(enid, sch.getString("schedule"), 
							sch.getString("milestone"), entsch.get(enid), submission.instanceId(), sch.getString("entityType"));
				}
				else if(sch.getString("action").startsWith("fulfill") && entsch.get(enid) != null){
					scheduleService.fullfillMilestoneAndCloseAlert(enid, submission.anmId(), sch.getString("schedule") 
							, LocalDate.parse(entsch.get(enid)), submission.instanceId(), sch.getString("entityType"));
				}
			}
		}
    	
	}

	private Map<String, String> getEntitiesQualifyingForSchedule(FormSubmission submission, JSONObject schedule) throws JSONException {
		String entity = schedule.getString("entityType");
		Map<String, String> entityIds = new HashMap<String, String>();
		if(submission.bindType().equalsIgnoreCase(entity)){
			entityIds.put(submission.entityId(), submission.getField(schedule.getString("triggerDateField")));
		}
		
		if(submission.subForms() != null)
		for (SubFormData sbf : submission.subForms()) {
			if(sbf.bindType().equalsIgnoreCase(entity)){
				for (Map<String, String> inst : sbf.instances()) {
					entityIds.put(inst.get("id"), inst.get(schedule.getString("triggerDateField")));
				}
			}
		}
		return entityIds;
	}

	private void makeModelEntities(FormSubmission submission) {
    	Client c = formEntityConverter.getClientFromFormSubmission(submission);
		Event e = formEntityConverter.getEventFromFormSubmission(submission);
		Map<String, Map<String, Object>> dep = formEntityConverter.getDependentClientsFromFormSubmission(submission);

		clientService.addClient(c);
		eventService.addEvent(e);
		// TODO relationships b/w entities
			
		for (Map<String, Object> cm : dep.values()) {
			Client cin = (Client)cm.get("client");
			Event evin = (Event)cm.get("event");
			clientService.addClient(cin);
			eventService.addEvent(evin);
		}
	}
    
    private void passToZiggy(FormSubmission submission) {
    	String params = Utils.getZiggyParams(submission);
        ziggyService.saveForm(params, new Gson().toJson(submission.instance()));
	}

}
