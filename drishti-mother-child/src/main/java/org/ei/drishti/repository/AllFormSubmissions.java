package org.ei.drishti.repository;

import org.ei.drishti.domain.form.FormSubmission;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllFormSubmissions extends MotechBaseRepository<FormSubmission> {
    @Autowired
    protected AllFormSubmissions(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(FormSubmission.class, db);
    }

    @View(name = "formSubmission_by_anm_and_time", map = "function(doc) { if (doc.type === 'FormSubmission') { emit([doc.anmId, doc.timestamp], null); } }")
    public List<FormSubmission> findByANMIDAndTimeStamp(String anmId, long timestamp) {
        ComplexKey startKey = ComplexKey.of(anmId, timestamp + 1);
        ComplexKey endKey = ComplexKey.of(anmId, Long.MAX_VALUE);
        return db.queryView(createQuery("formSubmission_by_anm_and_time").startKey(startKey).endKey(endKey).includeDocs(true), FormSubmission.class);
    }

    public boolean exists(String instanceId) {
        return findByInstanceId(instanceId) != null;
    }

    @GenerateView
    private FormSubmission findByInstanceId(String instanceId) {
        List<FormSubmission> submissions = queryView("by_instanceId", instanceId);
        if (submissions == null || submissions.isEmpty()) {
            return null;
        }
        return submissions.get(0);
    }
}
