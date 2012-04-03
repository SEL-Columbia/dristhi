package org.ei.drishti.repository;

import org.ei.drishti.domain.Reminder;
import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllReminders extends MotechBaseRepository<Reminder> {
    @Autowired
    protected AllReminders(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(Reminder.class, db);
    }

    public void add(Reminder reminder) {
        super.add(reminder);
    }
}
