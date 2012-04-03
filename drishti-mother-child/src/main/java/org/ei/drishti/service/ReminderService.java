package org.ei.drishti.service;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.Reminder;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.repository.AllReminders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {
    private AllReminders allReminders;
    private AllMothers allMothers;

    @Autowired
    public ReminderService(AllReminders allReminders, AllMothers allMothers) {
        this.allReminders = allReminders;
        this.allMothers = allMothers;
    }

    public void reminderForMother(String caseID, String visitCode, String latenessStatus) {
        Mother mother = allMothers.findByCaseId(caseID);
        allReminders.add(new Reminder(mother.name(), mother.thaayiCardNo(), mother.anmPhoneNo(), caseID, visitCode, latenessStatus));
    }
}
