package org.ei.drishti.service;

import org.ei.drishti.domain.*;
import org.ei.drishti.repository.AllActions;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.text.MessageFormat.format;

@Service
public class ActionService {
    private AllActions allActions;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private AllEligibleCouples allEligibleCouples;
    private static Logger logger = LoggerFactory.getLogger(ActionService.class.toString());

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

    public void alertForMother(String caseID, String visitCode, String latenessStatus, DateTime dueDate) {
        Mother mother = allMothers.findByCaseId(caseID);

        allActions.add(new Action(caseID, mother.anmIdentifier(), ActionData.createAlert(mother.name(), mother.village(), mother.thaayiCardNo(), visitCode, latenessStatus, dueDate)));
    }

    public void alertForChild(String caseId, String visitCode, String latenessStatus, DateTime dueDate) {
        Child child = allChildren.findByCaseId(caseId);

        allActions.add(new Action(caseId, child.anmIdentifier(), ActionData.createAlert(child.name(), child.village(), child.thaayiCardNumber(), visitCode, latenessStatus, dueDate)));
    }

    public void deleteAlertForVisitForMother(String caseID, String visitCode) {
        Mother mother = allMothers.findByCaseId(caseID);

        allActions.add(new Action(caseID, mother.anmIdentifier(), ActionData.deleteAlert(visitCode)));
    }

    public void deleteAlertForVisitForChild(String caseID, String anmIdentifier, String visitCode) {
        allActions.add(new Action(caseID, anmIdentifier, ActionData.deleteAlert(visitCode)));
    }

    public void deleteAllAlertsForMother(String caseID) {
        Mother mother = allMothers.findByCaseId(caseID);

        allActions.addWithDelete(new Action(caseID, mother.anmIdentifier(), ActionData.deleteAllAlerts()), "alert");
    }

    public void deleteAllAlertsForChild(String caseID, String anmIdentifier) {
        allActions.addWithDelete(new Action(caseID, anmIdentifier, ActionData.deleteAllAlerts()), "alert");
    }

    public void registerEligibleCouple(String caseId, String ecNumber, String wife, String husband, String anmIdentifier, String village, String subCenter) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.createEligibleCouple(wife, husband, ecNumber, village, subCenter)));
    }

    public void closeEligibleCouple(String caseId, String anmIdentifier) {
        allActions.addWithDelete(new Action(caseId, anmIdentifier, ActionData.deleteEligibleCouple()), "alert");
    }

    public void registerPregnancy(String caseId, String ecNumber, String thaayiCardNumber, String anmIdentifier, String village, LocalDate lmpDate) {
        EligibleCouple eligibleCouple = allEligibleCouples.findByECNumberAndVillage(ecNumber, village);
        if (eligibleCouple == null) {
            logger.warn(format("Found pregnancy without registered eligible couple. Ignoring case: {0} for ecNumber: {1} for ANM: {2}",
                    caseId, ecNumber, anmIdentifier));
            return;
        }
        allActions.add(new Action(caseId, anmIdentifier, ActionData.createBeneficiary(eligibleCouple.caseId(), thaayiCardNumber, lmpDate)));
    }

    public void updateDeliveryOutcome(String caseId, String status) {
        Mother mother = allMothers.findByCaseId(caseId);
        EligibleCouple eligibleCouple = allEligibleCouples.findByECNumberAndVillage(mother.ecNumber(), mother.village());
        if (eligibleCouple == null) {
            logger.warn(format("Trying to update delivery outcome without registered eligible couple. Ignoring case: {0}.", caseId));
            return;
        }
        allActions.add(new Action(caseId, mother.anmIdentifier(), ActionData.updateBeneficiary(status)));
    }

    public void registerChildBirth(String caseId, String anmIdentifier, String thaayiCardNumber, LocalDate dateOfBirth) {
        Mother mother = allMothers.findByThaayiCardNumber(thaayiCardNumber);
        if (mother == null) {
            logger.warn(format("Found child birth without registered mother. Ignoring case: {0} for thaayiCardNumber: {1} for ANM: {2}",
                    caseId, thaayiCardNumber, anmIdentifier));
            return;
        }

        allActions.add(new Action(caseId, anmIdentifier, ActionData.registerChildBirth(mother.caseId(), dateOfBirth)));
    }
}
