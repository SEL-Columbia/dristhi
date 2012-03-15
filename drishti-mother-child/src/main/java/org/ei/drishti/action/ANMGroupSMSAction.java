package org.ei.drishti.action;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static java.text.MessageFormat.format;

@Component
@Qualifier("ANMGroupSMSAction")
public class ANMGroupSMSAction implements Action {
    private final AllMothers allMothers;

    @Autowired
    public ANMGroupSMSAction(AllMothers allMothers) {
        this.allMothers = allMothers;
    }

    @Override
    public void invoke(MilestoneEvent event) {
        String caseId = event.externalId();
        Mother mother = allMothers.findByCaseId(caseId);

        String message = format("======= {4}: Event for {0}: Schedule => {1}, Milestone => {2}, Window => {3}", mother,
                event.scheduleName(), event.milestoneName(), event.windowName(), DateUtil.now());

        System.out.println(message);
        // smsService.sendSMS("9590377135", message);
    }
}
