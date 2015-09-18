package org.opensrp.connector.schedule;

import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants.ScheduleTrackerConfig;
import org.opensrp.connector.openmrs.service.OpenmrsSchedulerService;
import org.opensrp.domain.AppStateToken;
import org.opensrp.scheduler.service.ActionService;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.service.ConfigService;
import org.opensrp.service.ErrorTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenmrsSyncerListener {

	private OpenmrsSchedulerService openmrsSchedulerService;
	private ScheduleService opensrpScheduleService;
	private ActionService actionService;
	private ConfigService config;
	private ErrorTraceService errorTraceService;
	
	@Autowired
	public OpenmrsSyncerListener(OpenmrsSchedulerService openmrsSchedulerService, 
			ScheduleService opensrpScheduleService, ActionService actionService, 
			ConfigService config, ErrorTraceService errorTraceService) {
		this.openmrsSchedulerService = openmrsSchedulerService;
		this.opensrpScheduleService = opensrpScheduleService;
		this.actionService = actionService;
		this.config = config;
		this.errorTraceService = errorTraceService;
		try{
			AppStateToken at = this.config.getAppStateTokenByName(ScheduleTrackerConfig.openmrs_syncer_sync_by_last_update_enrollment);
			if(at == null){
				this.config.registerAppStateToken(ScheduleTrackerConfig.openmrs_syncer_sync_by_last_update_enrollment, 
						null, "ScheduleTracker token to keep track of enrollment synced with OpenMRS");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
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
}
