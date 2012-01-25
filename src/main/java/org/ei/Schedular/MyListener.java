package org.ei.Schedular;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

@Component
public class MyListener {
	@MotechListener(subjects = {EventSubject.ENROLLED_ENTITY_MILESTONE_ALERT})
    public void handleX(MotechEvent event) {
        System.out.println("Hello");
    }
}
