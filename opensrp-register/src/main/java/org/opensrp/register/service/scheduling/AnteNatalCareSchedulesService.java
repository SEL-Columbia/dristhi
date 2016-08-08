/**
 * @author coder
 */
package org.opensrp.register.service.scheduling;

import static java.text.MessageFormat.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.opensrp.common.util.DateUtil;
import org.opensrp.domain.Event;
import org.opensrp.dto.AlertStatus;
import org.opensrp.register.RegisterConstants.MotherScheduleConstants;
import org.opensrp.scheduler.HealthSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnteNatalCareSchedulesService {
	
	private static Logger logger = LoggerFactory.getLogger(AnteNatalCareSchedulesService.class.toString());
	
	private HealthSchedulerService scheduler;
	
	public void processEvent(Event event) {
		
	}
	
	public void enrollMother(String caseId, LocalDate referenceDateForSchedule, String provider, String startDate) {
		
		enrollIntoCorrectMilestoneOfANCCare(caseId, referenceDateForSchedule, provider, startDate);
	}
	
	/**
	 * Create ANC Schedule depends on LMP Date
	 * 
	 * @param entityId form entity Id
	 * @param referenceDateForSchedule LMP Date convert to Local Date
	 * @param provider FW user name
	 * @param instanceId form instance id
	 * @param startDate LMP Date as String format
	 */
	private void enrollIntoCorrectMilestoneOfANCCare(String entityId, LocalDate referenceDateForSchedule, String provider,
	                                                 String startDate) {
		String milestone = null;
		DateTime ancStartDate = null;
		DateTime ancExpireDate = null;
		AlertStatus alertStaus = null;
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = format.parse(startDate);
			
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DateTime start = new DateTime(date);
		if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(24).toPeriod().minusDays(6))) {
			milestone = MotherScheduleConstants.SCHEDULE_ANC_1;
			ancStartDate = new DateTime(start);
			
			if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
			    Weeks.weeks(8).toPeriod().minusDays(6)))
				alertStaus = AlertStatus.normal;
			else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
			    Weeks.weeks(8).toPeriod().minusDays(1)))
				alertStaus = AlertStatus.upcoming;
			else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
			    Weeks.weeks(23).toPeriod().minusDays(1)))
				alertStaus = AlertStatus.urgent;
			else
				alertStaus = AlertStatus.expired;
			
		} else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
		    Weeks.weeks(32).toPeriod().minusDays(6))) {
			milestone = MotherScheduleConstants.SCHEDULE_ANC_2;
			
			if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
			    Weeks.weeks(24).toPeriod().minusDays(1)))
				alertStaus = AlertStatus.upcoming;
			else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
			    Weeks.weeks(31).toPeriod().minusDays(1)))
				alertStaus = AlertStatus.urgent;
			else
				alertStaus = AlertStatus.expired;
			
		} else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
		    Weeks.weeks(36).toPeriod().minusDays(6))) {
			milestone = MotherScheduleConstants.SCHEDULE_ANC_3;
			
			if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
			    Weeks.weeks(32).toPeriod().minusDays(1)))
				alertStaus = AlertStatus.upcoming;
			else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
			    Weeks.weeks(35).toPeriod().minusDays(1)))
				alertStaus = AlertStatus.urgent;
			else
				alertStaus = AlertStatus.expired;
			
		} else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
		    Weeks.weeks(94).toPeriod().minusDays(5))) {
			milestone = MotherScheduleConstants.SCHEDULE_ANC_4;
			
			if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule,
			    Weeks.weeks(36).toPeriod().minusDays(1)))
				alertStaus = AlertStatus.upcoming;
			else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(93).toPeriod()))
				alertStaus = AlertStatus.urgent;
			else
				alertStaus = AlertStatus.expired;
			
		}
		logger.info(format("Enrolling ANC with Entity id:{0} to ANC schedule, milestone: {1}.", entityId, milestone));
		scheduler.enrollIntoSchedule(entityId, MotherScheduleConstants.SCHEDULE_ANC, milestone,
		    referenceDateForSchedule.toString());
		
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
	
	public void enrollANCSchedule() {
		
	}
	
}
