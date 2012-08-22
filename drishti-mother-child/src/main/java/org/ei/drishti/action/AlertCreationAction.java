package org.ei.drishti.action;

import org.ei.drishti.dto.BeneficiaryType;
import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.service.ActionService;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.ei.drishti.dto.AlertPriority.normal;
import static org.ei.drishti.dto.AlertPriority.urgent;

@Component
@Qualifier("AlertCreationAction")
public class AlertCreationAction implements Action {
    ActionService actionService;

    @Autowired
    public AlertCreationAction(ActionService actionService) {
        this.actionService = actionService;
    }

    @Override
    public void invoke(MilestoneEvent event, Map<String, String> extraData) {
        BeneficiaryType beneficiaryType = BeneficiaryType.from(extraData.get("beneficiaryType"));

        if (WindowName.late.toString().equals(event.windowName())) {
            actionService.alertForBeneficiary(beneficiaryType, event.externalId(), event.milestoneName(), urgent, event.startOfLateWindow(), event.startOfMaxWindow());
        } else {
            actionService.alertForBeneficiary(beneficiaryType, event.externalId(), event.milestoneName(), normal, event.startOfDueWindow(), event.startOfLateWindow());
        }
    }
}
