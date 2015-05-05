package org.opensrp.register.action;

import static org.opensrp.dto.BeneficiaryType.child;
import static org.opensrp.dto.BeneficiaryType.ec;
import static org.opensrp.dto.BeneficiaryType.mother;

import java.util.Map;

import org.opensrp.dto.BeneficiaryType;
import org.opensrp.register.repository.AllChildren;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.register.repository.AllMothers;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.HookedEvent;
import org.opensrp.scheduler.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("AlertCreationAction")
public class AlertCreationAction implements HookedEvent {
    private HealthSchedulerService scheduler;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private AllEligibleCouples allEligibleCouples;

    @Autowired
    public AlertCreationAction(HealthSchedulerService scheduler, AllMothers allMothers, AllChildren allChildren, AllEligibleCouples allEligibleCouples) {
        this.scheduler = scheduler;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.allEligibleCouples = allEligibleCouples;
    }

    @Override
    public void invoke(MilestoneEvent event, Map<String, String> extraData) {
        BeneficiaryType beneficiaryType = BeneficiaryType.from(extraData.get("beneficiaryType"));

     // TODO: Get rid of this horrible if-else after Motech-Platform fixes the bug related to metadata in motech-schedule-tracking.
        String anmIdentifier;
        String caseID = event.externalId();
		if (mother.equals(beneficiaryType)) {
            anmIdentifier = allMothers.findByCaseId(caseID ).anmIdentifier();
        } else if (child.equals(beneficiaryType)) {
            anmIdentifier = allChildren.findByCaseId(caseID).anmIdentifier();
        } else if (ec.equals(beneficiaryType)) {
            anmIdentifier = allEligibleCouples.findByCaseId(caseID).anmIdentifier();
        } else {
            throw new IllegalArgumentException("Beneficiary Type : " + beneficiaryType + " is of unknown type");
        }
        
		scheduler.alertFor(event.windowName(), beneficiaryType, caseID, anmIdentifier, event.scheduleName(), event.milestoneName(), event.startOfDueWindow(), event.startOfLateWindow(), event.startOfMaxWindow());
    }
}
