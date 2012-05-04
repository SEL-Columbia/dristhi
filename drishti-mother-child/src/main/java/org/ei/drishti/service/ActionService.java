package org.ei.drishti.service;

import org.ei.drishti.domain.Action;
import org.ei.drishti.domain.ActionData;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllActions;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionService {
    private AllActions allActions;
    private AllMothers allMothers;

    @Autowired
    public ActionService(AllActions allActions, AllMothers allMothers) {
        this.allActions = allActions;
        this.allMothers = allMothers;
    }

    public List<Action> getNewAlertsForANM(String anmIdentifier, long timeStamp) {
        return allActions.findByANMIDAndTimeStamp(anmIdentifier, timeStamp);
    }

    public void alertForMother(String caseID, String visitCode, String latenessStatus, DateTime dueDate) {
        Mother mother = allMothers.findByCaseId(caseID);

        allActions.add(new Action(caseID, mother.anmIdentifier(), ActionData.createAlert(mother.name(), mother.thaayiCardNo(), visitCode, latenessStatus, dueDate)));
    }

    public void alertForChild(String caseId, String childName, String anmIdentifier, String thaayiCardNumber, String visitCode, String latenessStatus, DateTime dueDate) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.createAlert(childName, thaayiCardNumber, visitCode, latenessStatus, dueDate)));
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

        allActions.addWithDelete(new Action(caseID, mother.anmIdentifier(), ActionData.deleteAllAlerts()));
    }

    public void deleteAllAlertsForChild(String caseID, String anmIdentifier) {
        allActions.addWithDelete(new Action(caseID, anmIdentifier, ActionData.deleteAllAlerts()));
    }

    public void registerEligibleCouple(String caseId, String ecNumber, String wife, String husband, String anmIdentifier) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.createEligibleCouple(wife, husband, ecNumber)));
    }

    public void closeEligibleCouple(String caseId, String anmIdentifier) {
        allActions.addWithDelete(new Action(caseId, anmIdentifier, ActionData.deleteEligibleCouple()));
    }
}
