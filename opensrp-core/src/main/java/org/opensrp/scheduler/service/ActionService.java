package org.opensrp.scheduler.service;

import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.dto.ActionData;
import org.opensrp.dto.AlertStatus;
import org.opensrp.dto.BeneficiaryType;
import org.opensrp.scheduler.Action;
import org.opensrp.scheduler.Alert;
import org.opensrp.scheduler.Alert.AlertType;
import org.opensrp.scheduler.Alert.TriggerType;
import org.opensrp.scheduler.repository.AllActions;
import org.opensrp.scheduler.repository.AllAlerts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionService {
    private AllActions allActions;
    private AllAlerts allAlerts;

    @Autowired
    public ActionService(AllActions allActions, AllAlerts allAlerts) {
        this.allActions = allActions;
        this.allAlerts = allAlerts;
    }

    public List<Action> getNewAlertsForANM(String anmIdentifier, long timeStamp) {
        return allActions.findByProviderIdAndTimeStamp(anmIdentifier, timeStamp);
    }
    
    public List<Alert> getAlertsForProvider(String providerId, long timeStamp) {
        return allAlerts.findByProviderAndTimestamp(providerId, timeStamp);
    }
    
    public List<Alert> getAlertsActiveForProvider(String providerId, long timeStamp) {
        return allAlerts.findActiveByProviderAndTimestamp(providerId, timeStamp);
    }
    
    public List<Action> findByCaseIdScheduleAndTimeStamp(String caseId, String schedule, DateTime start, DateTime end) {
		return allActions.findByCaseIdScheduleAndTimeStamp(caseId, schedule, start, end);
	}
    
    public List<Alert> findAlertByEntityIdScheduleAndTimeStamp(String entityId, String schedule, DateTime start, DateTime end) {
		return allAlerts.findByEntityIdTriggerAndTimeStamp(entityId, schedule, start, end);
	}

    public void alertForBeneficiary(String beneficiaryType, String caseID, String anmIdentifier, String scheduleName, String visitCode, AlertStatus alertStatus, DateTime startDate, DateTime expiryDate) {
    	allActions.addOrUpdateAlert(new Action(caseID, anmIdentifier, ActionData.createAlert(beneficiaryType, scheduleName, visitCode, alertStatus, startDate, expiryDate)));
    	allAlerts.add(new Alert(anmIdentifier, caseID, beneficiaryType, AlertType.notification, TriggerType.schedule, scheduleName, visitCode, startDate, expiryDate, alertStatus, null));
    }

    public void markAllAlertsAsInactive(String entityId) {
        allActions.markAllAsInActiveFor(entityId);
        allAlerts.markAllAsClosedFor(entityId, "unenrolled from schedule");
    }

    public void markAlertAsInactive(String anmId, String entityId, String scheduleName) {
        allActions.markAlertAsInactiveFor(anmId, entityId, scheduleName);
        allAlerts.markAlertAsClosedFor(anmId, entityId, scheduleName, "unenrolled from schedule");
    }

    public void markAlertAsClosed(String caseId, String anmIdentifier, String visitCode, String completionDate) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.markAlertAsClosed(visitCode, completionDate)));
        allAlerts.markAlertAsCompleteFor(anmIdentifier, caseId, visitCode, completionDate);
    }

    public void markAlertAsClosed(String caseId, String visitCode, String completionDate) {
    	allActions.add(new Action(caseId, "all_providers", ActionData.markAlertAsClosed(visitCode, completionDate)));
        allAlerts.markAlertAsCompleteFor(caseId, visitCode, completionDate);
    }
    
    public void closeBeneficiary(BeneficiaryType beneficiary, String caseId, String anmIdentifier, String reasonForClose) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.closeBeneficiary(beneficiary.name(), reasonForClose)));
        //TODO
        allAlerts.add(new Alert(anmIdentifier, caseId, beneficiary.name(), AlertType.notification, TriggerType.caseClosed, null, null, new DateTime(), new DateTime(), AlertStatus.urgent, null));
    }

    public void reportForIndicator(String anmIdentifier, ActionData actionData) {
        allActions.add(new Action("", anmIdentifier, actionData));
    }

    public void deleteReportActions() {
        allActions.deleteAllByTarget("report");
    }
}
