package org.ei.drishti.action;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.scheduler.router.Action;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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

        System.out.println("======= Event for " + mother + ": " + event);
        // smsService.sendSMS("9590377135", event.getParameters().toString());
    }
}
