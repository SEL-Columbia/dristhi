package org.opensrp.scheduler;

import static org.motechproject.scheduletracking.api.domain.WindowName.due;
import static org.motechproject.scheduletracking.api.domain.WindowName.earliest;
import static org.motechproject.scheduletracking.api.domain.WindowName.late;
import static org.opensrp.scheduler.Matcher.any;
import static org.opensrp.scheduler.Matcher.anyOf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AlertHandler {
    @Autowired
    public AlertHandler(TaskSchedulerService scheduler,
                          // @Qualifier("ForceFulfillAction") HookedEvent forceFulfill,
                          /* @Qualifier("AutoClosePNCAction") HookedEvent autoClosePNCAction,*/
                           @Qualifier("ECAlertCreationAction") HookedEvent alertCreation) {
    	scheduler.addHookedEvent(any(), any(), anyOf(earliest.toString(), due.toString(), late.toString()),
                alertCreation);
    	
//   TODO 	scheduler.addHookedEvent(eq(SCHEDULE_ANC), any(), eq(max.toString()), forceFulfill);
 //   	scheduler.addHookedEvent(eq(SCHEDULE_LAB), any(), eq(max.toString()), forceFulfill);
   // 	scheduler.addHookedEvent(eq(SCHEDULE_AUTO_CLOSE_PNC), any(), any(), autoClosePNCAction);
    }
}
