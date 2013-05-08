package org.ei.drishti.form.repository;

import org.ei.drishti.form.domain.FormSubmission;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSubmissions extends MotechBaseRepository<FormSubmission> {
    @Autowired
    protected AllSubmissions(@Qualifier("drishtiFormDatabaseConnector") CouchDbConnector db) {
        super(FormSubmission.class, db);
    }

    public boolean exists(String instanceId) {
        return findByInstanceId(instanceId) != null;
    }

    @GenerateView
    public FormSubmission findByInstanceId(String instanceId) {
        List<FormSubmission> submissions = queryView("by_instanceId", instanceId);
        if (submissions == null || submissions.isEmpty()) {
            return null;
        }
        return submissions.get(0);
    }

}
