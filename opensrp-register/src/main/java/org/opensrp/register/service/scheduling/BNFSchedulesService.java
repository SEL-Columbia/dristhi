/**
 * @author james 
 */
package org.opensrp.register.service.scheduling;

import static java.text.MessageFormat.format;

import org.joda.time.LocalDate;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.repository.couch.AllActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BNFSchedulesService {
	
	private static Logger logger = LoggerFactory.getLogger(BNFSchedulesService.class.toString());
	
	private HealthSchedulerService scheduler;
	
	@Autowired
	public BNFSchedulesService(HealthSchedulerService scheduler, AllActions allActions) {
		this.scheduler = scheduler;
	}
	
	public void enrollBNF(String entityId,String scheduleName, LocalDate referenceDateForSchedule, String eventId) {
		logger.info(format("Enrolling Mother into BNF schedule. Id: {0}", eventId));
		scheduler.enrollIntoSchedule(entityId, scheduleName, referenceDateForSchedule, eventId);
	}
	
}
