package org.opensrp.register.service.scheduling;

import static java.text.MessageFormat.format;

import org.joda.time.LocalDate;
import org.opensrp.scheduler.HealthSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ENCCSchedulesService {
	
	private static Logger logger = LoggerFactory.getLogger(ENCCSchedulesService.class.toString());
	
	private HealthSchedulerService scheduler;
	
	@Autowired
	public ENCCSchedulesService(HealthSchedulerService scheduler) {
		this.scheduler = scheduler;
	}
	
	public void enrollIntoCorrectMilestoneOfENCCCare(String entityId,String scheduleName, LocalDate referenceDateForSchedule,
	                                                 String eventId) {
		
		logger.info(format("Enrolling with Entity id:{0} to ENCC schedule, milestone: {1}.", entityId, scheduleName));
		scheduler.enrollIntoSchedule(entityId, scheduleName,referenceDateForSchedule.toString(), eventId);
	}
	
	public void fullfillMilestone(String entityId, String providerId, String scheduleName, LocalDate completionDate,
	                              String eventId) {
		try {
			//scheduler.fullfillMilestone(entityId, providerId, scheduleName, completionDate);
			scheduler.fullfillMilestoneAndCloseAlert(entityId, providerId, scheduleName, completionDate, eventId);
			logger.info("fullfillMilestone with id: :" + entityId);
		}
		catch (Exception e) {
			logger.info("Does not a fullfillMilestone :" + e.getMessage());
		}
	}
	
	public void unEnrollFromSchedule(String entityId, String providerId, String scheduleName, String eventId) {
		logger.info(format("Un-enrolling ENCC with Entity id:{0} from schedule: {1}", entityId, scheduleName));
		scheduler.unEnrollFromSchedule(entityId, providerId, scheduleName, eventId);
	}
}
