/**
 * @author coder
 */
package org.opensrp.register.service.scheduling;

import static java.text.MessageFormat.format;

import org.joda.time.LocalDate;
import org.opensrp.register.RegisterConstants.MotherScheduleConstants;
import org.opensrp.scheduler.HealthSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnteNatalCareSchedulesService {
	
	private static Logger logger = LoggerFactory.getLogger(AnteNatalCareSchedulesService.class.toString());
	
	@Autowired
	private HealthSchedulerService scheduler;
	
	public void enrollMother(String entityId, LocalDate referenceDateForSchedule, String eventId) {
		
		scheduler.enrollIntoSchedule(entityId, MotherScheduleConstants.SCHEDULE_ANC, referenceDateForSchedule.toString(),
		    eventId);
	}
	
	public void fullfillSchedule(String caseID, String scheduleName, String instanceId, long timestamp) {
		try {
			//TODO
			//scheduler.fullfillSchedule(caseID, scheduleName, instanceId, timestamp);
			logger.info("fullfillSchedule a Schedule with id : " + caseID);
		}
		catch (Exception e) {
			logger.info("Does not fullfill a schedule:" + e.getMessage());
		}
	}
	
	public void unEnrollFromAllSchedules(String entityId, String eventId) {
		scheduler.unEnrollFromAllSchedules(entityId, eventId);
	}
	
	public void unEnrollFromSchedule(String entityId, String anmId, String scheduleName, String eventId) {
		logger.info(format("Un-enrolling ANC with Entity id:{0} from schedule: {1}", entityId, scheduleName));
		scheduler.unEnrollFromSchedule(entityId, anmId, scheduleName, eventId);
	}
	
	public void fullfillMilestone(String entityId, String providerId, String scheduleName, LocalDate completionDate,
	                              String eventId) {
		try {
			scheduler.fullfillMilestoneAndCloseAlert(entityId, providerId, scheduleName, completionDate, eventId);
			logger.info("fullfillMilestone with id: :" + entityId);
		}
		catch (Exception e) {
			logger.info("Does not a fullfillMilestone :" + e.getMessage());
		}
	}
	
}
