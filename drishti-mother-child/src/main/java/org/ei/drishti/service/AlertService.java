package org.ei.drishti.service;

import org.ei.drishti.domain.AlertAction;
import org.ei.drishti.domain.AlertData;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllAlertActions;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {
    private AllAlertActions allAlertActions;
    private AllMothers allMothers;

    @Autowired
    public AlertService(AllAlertActions allAlertActions, AllMothers allMothers) {
        this.allAlertActions = allAlertActions;
        this.allMothers = allMothers;
    }

    public void alertForMother(String caseID, String visitCode, String latenessStatus, DateTime dueDate) {
        Mother mother = allMothers.findByCaseId(caseID);

        allAlertActions.add(new AlertAction(caseID, mother.anmPhoneNo(), AlertData.create(mother.name(), mother.thaayiCardNo(), visitCode, latenessStatus, DateTime.now())));
    }

    public List<AlertAction> getNewAlertsForANM(String anmIdentifier, long timeStamp) {
        return allAlertActions.findByANMIDAndTimeStamp(anmIdentifier, timeStamp);
    }
}
