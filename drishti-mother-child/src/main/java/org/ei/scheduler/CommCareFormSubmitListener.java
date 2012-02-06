package org.ei.scheduler;

import org.ei.commcare.listener.event.CommCareFormEvent;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

@Component
public class CommCareFormSubmitListener {
    @MotechListener(subjects = {CommCareFormEvent.EVENT_SUBJECT})
    public void handleX(MotechEvent event) {
        System.out.println(event);
    }
}
