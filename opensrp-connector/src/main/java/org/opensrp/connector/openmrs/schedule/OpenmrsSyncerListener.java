package org.opensrp.connector.openmrs.schedule;

import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants.SchedulerConfig;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.OpenmrsSchedulerService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.domain.AppStateToken;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
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
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_syncer_sync_schedule_tracker_by_last_update_enrollment, 
				0, "ScheduleTracker token to keep track of enrollment synced with OpenMRS", true);
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_syncer_sync_client_by_date_updated, 
				0, "OpenMRS data pusher token to keep track of new / updated clients synced with OpenMRS", true);
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_syncer_sync_client_by_date_voided, 
				0, "OpenMRS data pusher token to keep track of voided clients synced with OpenMRS", true);
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_syncer_sync_event_by_date_updated, 
				0, "OpenMRS data pusher token to keep track of new / updated events synced with OpenMRS", true);
		
		this.config.registerAppStateToken(SchedulerConfig.openmrs_syncer_sync_event_by_date_voided, 
				0, "OpenMRS data pusher token to keep track of voided events synced with OpenMRS", true);
	}
	
    @MotechListener(subjects = OpenmrsConstants.SCHEDULER_TRACKER_SYNCER_SUBJECT)
	public void scheduletrackerSyncer(MotechEvent event) {
    	try{
    		System.out.println("RUNNING "+event.getSubject());
	    	AppStateToken lastsync = config.getAppStateTokenByName(SchedulerConfig.openmrs_syncer_sync_schedule_tracker_by_last_update_enrollment);
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
	    	config.updateAppStateToken(SchedulerConfig.openmrs_syncer_sync_schedule_tracker_by_last_update_enrollment, end);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
	}
    
	@MotechListener(subjects = OpenmrsConstants.SCHEDULER_OPENMRS_DATA_PUSH_SUBJECT)
	public void pushToOpenMRS(MotechEvent event) {
    	try{
    		System.out.println("RUNNING "+event.getSubject());
    		
    		AppStateToken lastsync = config.getAppStateTokenByName(SchedulerConfig.openmrs_syncer_sync_client_by_date_updated);
	    	Long start = lastsync==null||lastsync.getValue()==null?0:lastsync.longValue();
			Long end = System.currentTimeMillis();
			
    		System.out.println("START "+start+" , END "+end);

			List<Client> cl = clientService.findByServerVersion(start);
			System.out.println("Clients list size "+cl.size());
			for (Client c : cl) {
				try{
					String uuid = c.getIdentifier(PatientService.OPENMRS_UUID_IDENTIFIER_TYPE);
					
	    			if(uuid == null){
	    				JSONObject p = patientService.getPatientByIdentifier(c.getBaseEntityId());
	    				for (Entry<String, String> id : c.getIdentifiers().entrySet()) {
	    					p = patientService.getPatientByIdentifier(id.getValue());
	    					if(p != null){
	    						break;
	    					}
						}
	    				if(p != null){
	    					uuid = p.getString("uuid");
	    				}
	    			}
	    			if(uuid != null){
	    				System.out.println("Updating patient "+uuid);
	    				patientService.updatePatient(c, uuid);
	    			}
	    			else {
	    				System.out.println(patientService.createPatient(c));
	    			}
				}
				catch(Exception ex1){
					ex1.printStackTrace();
					errorTraceService.log("OPENMRS FAILED CLIENT PUSH", Client.class.getName(), c.getBaseEntityId(), ExceptionUtils.getStackTrace(ex1), "");
				}				
			}
	    	config.updateAppStateToken(SchedulerConfig.openmrs_syncer_sync_client_by_date_updated, end);
	    	
	    	System.out.println("RUNNING FOR EVENTS");
    		
    		lastsync = config.getAppStateTokenByName(SchedulerConfig.openmrs_syncer_sync_event_by_date_updated);
	    	start = lastsync==null||lastsync.getValue()==null?0:lastsync.longValue();
			end = System.currentTimeMillis();
			
			List<Event> el = eventService.findByServerVersion(start);
			for (Event e : el) {
				try{
					String uuid = e.getIdentifier(EncounterService.OPENMRS_UUID_IDENTIFIER_TYPE);
	    			if(uuid != null){
	    				encounterService.updateEncounter(e);
	    			}
	    			else {
	    				System.out.println(encounterService.createEncounter(e));
	    			}
				}
				catch(Exception ex2){
					ex2.printStackTrace();
					errorTraceService.log("OPENMRS FAILED EVENT PUSH", Event.class.getName(), e.getId(), ExceptionUtils.getStackTrace(ex2), "");
				}				
			}
	    	config.updateAppStateToken(SchedulerConfig.openmrs_syncer_sync_event_by_date_updated, end);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    	}
	}
}
