package org.ei.drishti.service;

import org.ei.drishti.domain.Action;
import org.ei.drishti.dto.ActionData;
import org.ei.drishti.dto.AlertStatus;
import org.ei.drishti.dto.BeneficiaryType;
import org.ei.drishti.repository.AllActions;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.ei.drishti.dto.BeneficiaryType.*;

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

        allActions.add(new Action(caseID, anmIdentifier, ActionData.createAlert(beneficiaryType, scheduleName, visitCode, alertStatus, startDate, expiryDate)));
    }

    public void markAllAlertsAsInactive(String entityId) {
        allActions.markAllAsInActiveFor(entityId);
    }

    public void markAlertAsClosed(String caseId, String anmIdentifier, String visitCode, String completionDate) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.markAlertAsClosed(visitCode, completionDate)));
    }

    public void registerChildBirth(String caseId, String anmIdentifier, String motherCaseId, String thaayiCardNumber, LocalDate dateOfBirth, String gender, Map<String, String> details) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.registerChildBirth(motherCaseId, thaayiCardNumber, dateOfBirth, gender, details)));
    }

    public void updateMotherDetails(String caseId, String anmIdentifier, Map<String, String> details) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.updateMotherDetails(details)));
    }

    public void ancCareProvided(String caseId, String anmIdentifier, int visitNumber, LocalDate visitDate, int numberOfIFATabletsGiven, String ttDose, Map<String, String> details) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.ancCareProvided(visitNumber, visitDate, numberOfIFATabletsGiven, ttDose, details)));
    }

    public void updateANCOutcome(String caseId, String anmIdentifier, Map<String, String> details) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.updateANCOutcome(details)));
    }

    public void pncVisitHappened(BeneficiaryType beneficiaryType, String caseId, String anmIdentifier, LocalDate visitDate, int visitNumber, String numberOfIFATabletsProvided, Map<String, String> details) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.pncVisitHappened(beneficiaryType, visitDate, visitNumber, numberOfIFATabletsProvided, details)));
    }

    public void updateBirthPlanning(String caseId, String anmIdentifier, Map<String, String> details) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.updateBirthPlanning(details)));
    }

    public void updateImmunizations(String caseId, String anmIdentifier, Map<String, String> details, String immunizationsGiven, LocalDate immunizationsProvidedDate, String vitaminADose) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.updateImmunizations(immunizationsGiven, immunizationsProvidedDate, vitaminADose, details)));
    }

    public void closeChild(String caseId, String anmIdentifier) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.deleteChild()));
        allActions.markAllAsInActiveFor(caseId);
    }

    public void reportForIndicator(String anmIdentifier, ActionData actionData) {
        allActions.add(new Action("", anmIdentifier, actionData));
    }

    public void deleteReportActions() {
        allActions.deleteAllByTarget("report");
    }
}
