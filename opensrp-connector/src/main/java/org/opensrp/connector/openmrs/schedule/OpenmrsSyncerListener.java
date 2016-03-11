package org.opensrp.connector.openmrs.schedule;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants.ScheduleTrackerConfig;
import org.opensrp.connector.openmrs.constants.OpenmrsHouseHold;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.OpenmrsSchedulerService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.domain.AppStateToken;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.scheduler.service.ActionService;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.service.ClientService;
import org.opensrp.service.ConfigService;
import org.opensrp.service.ErrorTraceService;
import org.opensrp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenmrsSyncerListener {

	private OpenmrsSchedulerService openmrsSchedulerService;
	private ScheduleService opensrpScheduleService;
	private ActionService actionService;
	private ConfigService config;
	private ErrorTraceService errorTraceService;
	private PatientService patientService;
	private EncounterService encounterService;
	private EventService eventService;
	private ClientService clientService;
	
	@Autowired
	public OpenmrsSyncerListener(OpenmrsSchedulerService openmrsSchedulerService, 
			ScheduleService opensrpScheduleService, ActionService actionService, 
			ConfigService config, ErrorTraceService errorTraceService,
			PatientService patientService, EncounterService encounterService,
			ClientService clientService, EventService eventService) {
		this.openmrsSchedulerService = openmrsSchedulerService;
		this.opensrpScheduleService = opensrpScheduleService;
		this.actionService = actionService;
		this.config = config;
		this.errorTraceService = errorTraceService;
		this.patientService = patientService;
		this.encounterService = encounterService;
		this.eventService = eventService;
		this.clientService = clientService;
		
		this.config.registerAppStateToken(ScheduleTrackerConfig.openmrs_syncer_sync_by_last_update_enrollment, 
			0, "ScheduleTracker token to keep track of enrollment synced with OpenMRS", true);
	}
	
    @MotechListener(subjects = OpenmrsConstants.SCHEDULER_TRACKER_SYNCER_SUBJECT)
	public void scheduletrackerSyncer(MotechEvent event) {
    	try{
    		System.out.println("RUNNING SCHEDULER_TRACKER_SYNCER_SUBJECT");
	    	AppStateToken lastsync = config.getAppStateTokenByName(ScheduleTrackerConfig.openmrs_syncer_sync_by_last_update_enrollment);
	    	DateTime start = lastsync==null||lastsync.getValue()==null?new DateTime().minusYears(33):new DateTime(lastsync.stringValue());
			DateTime end = new DateTime();
			List<Enrollment> el = opensrpScheduleService.findEnrollmentByLastUpDate(start, end);
	    	for (Enrollment e : el){
				DateTime alertstart = e.getStartOfSchedule();
				DateTime alertend = e.getLastFulfilledDate();
				if(alertend == null){
					alertend = e.getCurrentMilestoneStartDate();
				}
	    		try {
	    			if(e.getMetadata().get(OpenmrsConstants.ENROLLMENT_TRACK_UUID) != null){
	    				openmrsSchedulerService.updateTrack(e, actionService.findByCaseIdScheduleAndTimeStamp(e.getExternalId(), e.getScheduleName(), alertstart, alertend));
	    			}
	    			else{
	    				JSONObject tr = openmrsSchedulerService.createTrack(e, actionService.findByCaseIdScheduleAndTimeStamp(e.getExternalId(), e.getScheduleName(), alertstart, alertend));
	    				opensrpScheduleService.updateEnrollmentWithMetadata(e.getId(), OpenmrsConstants.ENROLLMENT_TRACK_UUID, tr.getString("uuid"));
	    			}
				} catch (Exception e1) {
					e1.printStackTrace();
					errorTraceService.log("ScheduleTracker Syncer Inactive Schedule", Enrollment.class.getName(), e.getId(), e1.getStackTrace().toString(), "");
				}
	    	}
	    	config.updateAppStateToken(ScheduleTrackerConfig.openmrs_syncer_sync_by_last_update_enrollment, end);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
	}
    
    @SuppressWarnings("unchecked")
	@MotechListener(subjects = "PUSH FORM SUBMISSION TO OPENMRS")
	public void pushFormSubmissionToOpenMRS(MotechEvent event) {
    	try{
    		System.out.println("RUNNING PUSH FORM SUBMISSION TO OPENMRS");
    		Map<String, Object> d = (Map<String, Object>)event.getParameters().get("data");
    		Client c = (Client)d.get("client");
    		Event e = (Event)d.get("event");
    		Map<String, Map<String, Object>> dep = (Map<String, Map<String, Object>>) d.get("dependents");
    		
    		e.setEventId(addEventToOpenMRS(c, e));
    		
    		eventService.updateEvent(e);
    		
    		for (String k : dep.keySet()) {
				addEventToOpenMRS((Client)dep.get(k).get("client"), (Event)dep.get(k).get("event"));
			}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
	}
    
    private String addEventToOpenMRS(Client c, Event e) throws ParseException, IllegalStateException, JSONException{
//    	if(formEntityConverter.isOpenmrsForm(formSubmission)){
    			JSONObject p = patientService.getPatientByIdentifier(c.getBaseEntityId());//TODO by all identifiers
    			if(p == null){
    				System.out.println(patientService.createPatient(c));
    			}
        	
    			JSONObject ej = encounterService.createEncounter(e);
    			return ej.getString("uuid");
    	//}
    }

}
