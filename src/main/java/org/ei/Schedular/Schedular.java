package org.ei.Schedular;

import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.server.event.annotations.EventAnnotationBeanPostProcessor;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Schedular {

	public static void main(String[] args)
	{
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
	    new String[] {"applicationContext.xml"});
	    BeanFactory factory = (BeanFactory) appContext;
	    ScheduleTrackingService objTracking = factory.getBean(ScheduleTrackingService.class);
	    int x = 435;
	    objTracking.enroll(new EnrollmentRequest(String.valueOf(x), "IPTI Schedule", new Time(x / 100, x % 100), DateUtil.newDate(2012, 1, 2)));

	}
	
}
