package org.opensrp.connector.schedule;

import java.text.ParseException;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants.ScheduleTrackerConfig;
import org.opensrp.connector.openmrs.service.OpenmrsSchedulerService;
import org.opensrp.domain.AppStateToken;
import org.opensrp.scheduler.service.ActionService;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenmrsSyncerListener {

	private OpenmrsSchedulerService openmrsSchedulerService;
	private ScheduleService opensrpScheduleService;
	private ActionService actionService;
	private ConfigService config;
	
	@Autowired
	public OpenmrsSyncerListener(OpenmrsSchedulerService openmrsSchedulerService, 
			ScheduleService opensrpScheduleService, ActionService actionService, ConfigService config) {
		this.openmrsSchedulerService = openmrsSchedulerService;
		this.opensrpScheduleService = opensrpScheduleService;
		this.actionService = actionService;
		this.config = config;
	}
	
    @MotechListener(subjects = OpenmrsConstants.SCHEDULER_TRACKER_SYNCER_SUBJECT)
	public void scheduletrackerSyncer(MotechEvent event) {
    	try{
    	AppStateToken lastsync = config.getAppStateTokenByName(ScheduleTrackerConfig.openmrs_syncer_last_timestamp_non_active_enrollment);
    	DateTime start = lastsync == null?new DateTime().minusYears(33):new DateTime(lastsync.getStringValue());
		DateTime end = new DateTime().plusYears(2);
		List<Enrollment> el = opensrpScheduleService.findEnrollmentByStatusAndEnrollmentDate(EnrollmentStatus.COMPLETED.name(), start, end);
    	for (Enrollment e : el){
			DateTime alertstart = e.getStartOfSchedule();
			DateTime alertend = e.getLastFulfilledDate();
			if(alertend == null){
				alertend = e.getCurrentMilestoneStartDate();
			}
    		try {
				openmrsSchedulerService.createTrack(e, actionService.findByCaseIdScheduleAndTimeStamp(e.getExternalId(), e.getScheduleName(), alertstart, alertend));
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
	}
}
