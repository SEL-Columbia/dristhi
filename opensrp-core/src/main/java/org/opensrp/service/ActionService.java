package org.opensrp.service;

import org.opensrp.domain.Action;
import org.joda.time.DateTime;
import org.opensrp.dto.ActionData;
import org.opensrp.dto.AlertStatus;
import org.opensrp.dto.BeneficiaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.opensrp.dto.BeneficiaryType.*;
import org.opensrp.repository.AllActions;
import org.opensrp.repository.AllChildren;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllMothers;

@Service
public class ActionService {
    private AllActions allActions;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private AllEligibleCouples allEligibleCouples;

    @Autowired
    public ActionService(AllActions allActions, AllMothers allMothers, AllChildren allChildren, AllEligibleCouples allEligibleCouples) {
        this.allActions = allActions;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.allEligibleCouples = allEligibleCouples;
    }

    public List<Action> getNewAlertsForANM(String anmIdentifier, long timeStamp) {
        return allActions.findByANMIDAndTimeStamp(anmIdentifier, timeStamp);
    }

    public void alertForBeneficiary(BeneficiaryType beneficiaryType, String caseID, String scheduleName, String visitCode, AlertStatus alertStatus, DateTime startDate, DateTime expiryDate) {
        // TODO: Get rid of this horrible if-else after Motech-Platform fixes the bug related to metadata in motech-schedule-tracking.
        String anmIdentifier;
        if (mother.equals(beneficiaryType)) {
            anmIdentifier = allMothers.findByCaseId(caseID).anmIdentifier();
        } else if (child.equals(beneficiaryType)) {
            anmIdentifier = allChildren.findByCaseId(caseID).anmIdentifier();
        } else if (ec.equals(beneficiaryType)) {
            anmIdentifier = allEligibleCouples.findByCaseId(caseID).anmIdentifier();
        } else {
            throw new IllegalArgumentException("Beneficiary Type : " + beneficiaryType + " is of unknown type");
        }

        allActions.addOrUpdateAlert(new Action(caseID, anmIdentifier, ActionData.createAlert(beneficiaryType, scheduleName, visitCode, alertStatus, startDate, expiryDate)));
    }

    public void markAllAlertsAsInactive(String entityId) {
        allActions.markAllAsInActiveFor(entityId);
    }

    public void markAlertAsInactive(String anmId, String entityId, String scheduleName) {
        allActions.markAlertAsInactiveFor(anmId, entityId, scheduleName);
    }

    public void markAlertAsClosed(String caseId, String anmIdentifier, String visitCode, String completionDate) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.markAlertAsClosed(visitCode, completionDate)));
    }

    public void closeMother(String caseId, String anmIdentifier, String reasonForClose) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.closeMother(reasonForClose)));
    }

    public void reportForIndicator(String anmIdentifier, ActionData actionData) {
        allActions.add(new Action("", anmIdentifier, actionData));
    }

    public void deleteReportActions() {
        allActions.deleteAllByTarget("report");
    }
}
