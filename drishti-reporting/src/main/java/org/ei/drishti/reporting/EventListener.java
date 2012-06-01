package org.ei.drishti.reporting;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {
    @MotechListener(subjects = "REPORT")
    public void boo(MotechEvent event){
        System.out.println("Boo" + event);
    }
}
