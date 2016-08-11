/**
 * @author james 
 */
package org.opensrp.register.service.scheduling;

import static java.text.MessageFormat.format;

import org.joda.time.LocalDate;
import org.opensrp.register.RegisterConstants.MotherScheduleConstants;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.repository.AllActions;
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
	
	public void enrollBNF(String caseId, LocalDate referenceDateForSchedule, String provider, String instanceId,
	                      String startDate) {
		logger.info(format("Enrolling Mother into BNF schedule. Id: {0}", caseId));
		this.immediateEnrollIntoMilestoneOfBNF(caseId, startDate, provider, instanceId);
	}
	
	public void unEnrollBNFSchedule(String caseId, String providerId, String eventId) {
		logger.info(format("Unenrolling Mother from BNF schedule. Id: {0}", caseId));
		try {
			scheduler.fullfillMilestoneAndCloseAlert(caseId, providerId, MotherScheduleConstants.SCHEDULE_BNF,
			    new LocalDate(), eventId);
			//scheduler.unEnrollFromSchedule(caseId, providerId, SCHEDULE_BNF);        	
			//scheduler.fullfillMilestoneAndCloseAlert(caseId, providerId, SCHEDULE_BNF, SCHEDULE_BNF, new LocalDate());
			
		}
		catch (Exception e) {
			logger.info(format("Failed to UnEnrollFromSchedule BNF" + e.getMessage()));
		}
		
		try {
			scheduler.fullfillMilestoneAndCloseAlert(caseId, providerId, MotherScheduleConstants.SCHEDULE_BNF_IME,
			    new LocalDate(), eventId);
			
			//scheduler.unEnrollFromScheduleimediate(caseId, providerId, SCHEDULE_BNF_IME);        	
			//scheduler.fullfillMilestoneAndCloseAlert(caseId, providerId, SCHEDULE_BNF_IME, SCHEDULE_BNF_IME, new LocalDate());
			
		}
		catch (Exception e) {
			logger.info(format("Failed to UnEnrollFromSchedule BNF" + e.getMessage()));
		}
	}
	
	public void enrollIntoMilestoneOfBNF(String caseId, String referenceDate, String provider, String milestone,
	                                     String eventId) {
		logger.info(format("Enrolling Mother into BNF schedule. Id: {0}", caseId));
		try {
			scheduler.fullfillMilestoneAndCloseAlert(caseId, provider, MotherScheduleConstants.SCHEDULE_BNF, new LocalDate(),
			    eventId);
		}
		catch (Exception e) {
			logger.info(format("Failed to COmplete  BNF Schedule:" + e.getMessage()));
		}
		scheduler.enrollIntoSchedule(caseId, MotherScheduleConstants.SCHEDULE_BNF, milestone, referenceDate, eventId);
		
		// scheduleLogService.createNewScheduleLogandUnenrollImmediateSchedule(caseId, date, provider, instanceId, SCHEDULE_BNF_IME, SCHEDULE_BNF, BeneficiaryType.mother, DateTimeDuration.bnf_due_duration);
	}
	
	public void immediateEnrollIntoMilestoneOfBNF(String caseId, String referenceDate, String provider, String eventId) {
		logger.info(format("Enrolling Mother into Immediate BNF schedule. Id: {0}", caseId));
		scheduler.enrollIntoSchedule(caseId, MotherScheduleConstants.SCHEDULE_BNF_IME, referenceDate, eventId);
		//scheduleLogService.createImmediateScheduleAndScheduleLog(caseId, date, provider, instanceId, BeneficiaryType.mother, SCHEDULE_BNF, bnf_duration,SCHEDULE_BNF_IME);
		
	}
	
}
