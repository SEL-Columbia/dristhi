/**
 * @author james
 */
package org.opensrp.register.service.scheduling;

import static java.text.MessageFormat.format;

import org.joda.time.LocalDate;
import org.opensrp.scheduler.HealthSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ELCOScheduleService {
	
	private static Logger logger = LoggerFactory.getLogger(ELCOScheduleService.class.toString());
	
	
	private HealthSchedulerService scheduler;
	
	
	@Autowired
	public ELCOScheduleService(HealthSchedulerService scheduler) {
		this.scheduler = scheduler;
		
	}
	
	public void enrollIntoMilestoneOfMisElco(String caseId, String date,String eventId,String scheduleName) {
		logger.info(format("Enrolling Elco into MisElco schedule. Id: {0}", caseId));
		
		scheduler.enrollIntoSchedule(caseId, scheduleName, date, eventId);
	}
	
	
	
	public void unEnrollFromScheduleCensus(String caseId, String providerId, String scheduleName,String eventId) {
		//scheduler.unEnrollFromScheduleCensus(caseId, providerId, HH_SCHEDULE_CENSUS);
		try {
			scheduler.fullfillMilestoneAndCloseAlert(caseId, providerId, scheduleName, new LocalDate(), eventId);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		
	}
	
	public void unEnrollFromScheduleOfPSRF(String caseId, String providerId, String scheduleName,String eventId) {
		logger.info(format("Unenrolling Elco from PSRF schedule. Id: {0}", caseId));
		try {
			//scheduler.unEnrollFromSchedule(caseId, providerId, ELCO_SCHEDULE_PSRF);
			scheduler.fullfillMilestoneAndCloseAlert(caseId, providerId, scheduleName, new LocalDate(), eventId);

		}
		catch (Exception e) {
			logger.info(format("Failed to UnEnrollFromSchedule PSRF"));
		}
		
		
	}
	
	
	
	public void imediateEnrollIntoMilestoneOfPSRF(String caseId, String date, String provider, String scheduleName,String eventId) {
		logger.info(format("Enrolling Elco into PSRF schedule. Id: {0}", caseId));
		scheduler.enrollIntoSchedule(caseId, scheduleName, date, eventId);
		// scheduleLogService.createImmediateScheduleAndScheduleLog(caseId, date, provider, instanceId, BeneficiaryType.elco, ELCO_SCHEDULE_PSRF, duration,ELCOSchedulesConstantsImediate.IMD_ELCO_SCHEDULE_PSRF);
		
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
