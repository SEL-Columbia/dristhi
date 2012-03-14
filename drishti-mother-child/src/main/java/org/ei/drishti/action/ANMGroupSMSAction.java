package org.ei.drishti.action;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.scheduler.router.Action;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static java.text.MessageFormat.format;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.*;

@Component
@Qualifier("anmGroupSMSAction")
public class ANMGroupSMSAction implements Action {
    private final AllMothers allMothers;

    @Autowired
    public ANMGroupSMSAction(AllMothers allMothers) {
        this.allMothers = allMothers;
    }

    @Override
    public void invoke(MotechEvent event) {
        String caseId = event.getParameters().get(EventDataKeys.EXTERNAL_ID).toString();
        Mother mother = allMothers.findByCaseId(caseId);

        String scheduleName = (String) event.getParameters().get(SCHEDULE_NAME);
        String windowName = (String) event.getParameters().get(WINDOW_NAME);
        MilestoneAlert alert = (MilestoneAlert) event.getParameters().get(MILESTONE_NAME);

        System.out.println(format("======= {4}: Event for {0}: Schedule => {1}, Milestone => {2}, Window => {3}", mother, scheduleName, alert.getMilestoneName(), windowName, DateUtil.now()));
        // smsService.sendSMS("9590377135", event.getParameters().toString());
    }
}
