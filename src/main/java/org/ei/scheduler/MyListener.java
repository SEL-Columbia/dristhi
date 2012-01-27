package org.ei.scheduler;

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
        BeanFactory context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        SmsService smsservice = context.getBean(SmsService.class);

        smsservice.sendSMS("9590377135", "Hello World 3");
        System.out.println(event);
    }
}
