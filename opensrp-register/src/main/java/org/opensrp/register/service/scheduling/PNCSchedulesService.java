/**
 * @author james 
 */
package org.opensrp.register.service.scheduling;

import static java.text.MessageFormat.format;
import static org.opensrp.register.RegisterConstants.MotherScheduleConstants.SCHEDULE_PNC;

import org.joda.time.LocalDate;
import org.opensrp.scheduler.HealthSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PNCSchedulesService {
	
	private static Logger logger = LoggerFactory.getLogger(PNCSchedulesService.class.toString());
	
	
	private HealthSchedulerService scheduler;
	
	@Autowired
	public PNCSchedulesService(HealthSchedulerService scheduler) {
		this.scheduler = scheduler;
	}
	
	public void enrollPNCRVForMother(String entityId, LocalDate referenceDateForSchedule,String milestone,String eventId) {
		
		scheduler.enrollIntoSchedule(entityId, SCHEDULE_PNC, milestone, referenceDateForSchedule.toString(), eventId);
	}
	
	public void fullfillMilestone(String entityId, String providerId, String scheduleName, LocalDate completionDate,
	                              String eventId) {
		try {
			scheduler.fullfillMilestoneAndCloseAlert(entityId, providerId, scheduleName, completionDate, eventId);
			logger.info("Fullfill Milestone with id: :" + entityId);
		}
		catch (Exception e) {
			logger.info("Not a fullfillMilestone :" + e.getMessage());
		}
	}
	
	public void unEnrollFromSchedule(String entityId, String providerId, String scheduleName, String eventId) {
		logger.info(format("Un-enrolling PNC with Entity id:{0} from schedule: {1}", entityId, scheduleName));
		scheduler.unEnrollFromSchedule(entityId, providerId, scheduleName, eventId);
	}
	
	public void unEnrollFromAllSchedules(String entityId, String eventId) {
		scheduler.unEnrollFromAllSchedules(entityId, eventId);
	}
}
