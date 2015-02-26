package org.opensrp.action;

import org.opensrp.scheduler.router.Action;
import org.opensrp.scheduler.router.MilestoneEvent;
import org.opensrp.service.ActionService;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.opensrp.dto.BeneficiaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.opensrp.dto.AlertStatus.normal;
import static org.opensrp.dto.AlertStatus.upcoming;
import static org.opensrp.dto.AlertStatus.urgent;


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
            actionService.alertForBeneficiary(beneficiaryType, event.externalId(), event.scheduleName(), event.milestoneName(), urgent, event.startOfLateWindow(), event.startOfMaxWindow());
        } else if (WindowName.earliest.toString().equals(event.windowName())) {
            actionService.alertForBeneficiary(beneficiaryType, event.externalId(), event.scheduleName(), event.milestoneName(), upcoming, event.startOfDueWindow(), event.startOfLateWindow());
        } else {
            actionService.alertForBeneficiary(beneficiaryType, event.externalId(), event.scheduleName(), event.milestoneName(), normal, event.startOfDueWindow(), event.startOfLateWindow());
        }
    }
}
