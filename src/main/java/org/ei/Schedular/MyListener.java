package org.ei.Schedular;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MyListener {
	@MotechListener(subjects = {EventSubject.ENROLLED_ENTITY_MILESTONE_ALERT})
    public void handleX(MotechEvent event) {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
			    new String[] {"applicationContext.xml"});
		BeanFactory factory = (BeanFactory) appContext;
		SmsService smsservice = factory.getBean(SmsService.class);
	    smsservice.sendSMS("9590377135", "Hello World2");
        System.out.println("Hello");
    }
}
