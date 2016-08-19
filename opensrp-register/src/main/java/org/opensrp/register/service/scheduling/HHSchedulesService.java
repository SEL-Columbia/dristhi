package org.opensrp.register.service.scheduling;

import static java.text.MessageFormat.format;

import org.opensrp.scheduler.HealthSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HHSchedulesService {
	
	private static Logger logger = LoggerFactory.getLogger(HHSchedulesService.class.toString());
	
	private HealthSchedulerService scheduler;
	
	@Autowired
	public HHSchedulesService(HealthSchedulerService scheduler) {
		this.scheduler = scheduler;
	}
	
	public void enrollIntoMilestoneOfCensus(String entityId, String referenceDate, String provider, String scheduleName,
	                                        String eventId) {
		logger.info(format("Enrolling household into Census schedule. Id: {0}", entityId));
		
		scheduler.enrollIntoSchedule(entityId, scheduleName, referenceDate, eventId);
		
		//scheduleLogService.scheduleCloseAndSave(entityId, instanceId, provider, HH_SCHEDULE_CENSUS, HH_SCHEDULE_CENSUS, BeneficiaryType.household, AlertStatus.normal, new DateTime(),  new DateTime().plusHours(duration));
		
	}
}
