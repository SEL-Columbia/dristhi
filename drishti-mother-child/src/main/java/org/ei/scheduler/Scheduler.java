package main.java.org.ei.scheduler;

import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Scheduler {

	public static void main(String[] args) throws Throwable {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
        new String[] {"applicationContext.xml"});
        ScheduleTrackingService objTracking = appContext.getBean(ScheduleTrackingService.class);

        int x = 1234;
        objTracking.enroll(new EnrollmentRequest(String.valueOf(x), "IPTI Schedule", new Time(x / 100, x % 100), DateUtil.newDate(2012, 1, 2)));
	}
}
