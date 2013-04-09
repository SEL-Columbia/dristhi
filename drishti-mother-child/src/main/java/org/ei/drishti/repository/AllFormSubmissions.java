package org.ei.drishti.repository;

import org.ei.drishti.domain.FormSubmission;
import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllFormSubmissions extends MotechBaseRepository<FormSubmission> {
    @Autowired
    protected AllFormSubmissions(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(FormSubmission.class, db);
    }
}
