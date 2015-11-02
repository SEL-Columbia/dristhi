package org.ei.drishti.form.repository;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.service.FormSubmissionService;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository
public class AllFormSubmissions extends MotechBaseRepository<FormSubmission> {
	private static Logger logger = LoggerFactory
			.getLogger(AllFormSubmissions.class.toString());
    @Autowired
    protected AllFormSubmissions(@Qualifier("drishtiFormDatabaseConnector") CouchDbConnector db) {
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

    @View(name = "formSubmission_by_server_version", map = "function(doc) { if (doc.type === 'FormSubmission') { emit([doc.serverVersion], null); } }")
    public List<FormSubmission> findByServerVersion(long serverVersion) {
        ComplexKey startKey = ComplexKey.of(serverVersion + 1);
        ComplexKey endKey = ComplexKey.of(Long.MAX_VALUE);
        return db.queryView(createQuery("formSubmission_by_server_version").startKey(startKey).endKey(endKey).includeDocs(true), FormSubmission.class);
    }

    public List<FormSubmission> allFormSubmissions(long serverVersion, Integer batchSize) {
        ComplexKey startKey = ComplexKey.of(serverVersion + 1);
        ComplexKey endKey = ComplexKey.of(Long.MAX_VALUE);
        ViewQuery query = createQuery("formSubmission_by_server_version")
                .startKey(startKey)
                .endKey(endKey)
                .includeDocs(true);

        if (batchSize != null) {
            query.limit(batchSize);
        }
        return db.queryView(query, FormSubmission.class);
    }

    @View(
            name = "formSubmission_by_anm_and_server_version",
            map = "function(doc) { if (doc.type === 'FormSubmission') { emit([doc.anmId, doc.serverVersion], null); } }")
    public List<FormSubmission> findByANMIDAndServerVersion(String anmId, long version, Integer batchSize) {
        ComplexKey startKey = ComplexKey.of(anmId, version + 1);
        ComplexKey endKey = ComplexKey.of(anmId, Long.MAX_VALUE);
        ViewQuery query = createQuery("formSubmission_by_anm_and_server_version")
                .startKey(startKey)
                .endKey(endKey)
                .includeDocs(true);
        if (batchSize != null) {
            query.limit(batchSize);
        }
        return db.queryView(query, FormSubmission.class);
    }
    
    
    @View(
            name = "formSubmission_by_village_and_server_version",
            map = "function(doc) { if (doc.type === 'FormSubmission') { for(id in doc.formInstance.form.fields){if(doc.formInstance.form.fields[id].name ==='village'){emit([doc.formInstance.form.fields[id].value, doc.serverVersion], null); }}} }")
    public List<FormSubmission> findByVillageAndServerVersion(String village, long version, Integer batchSize) {
        ComplexKey startKey = ComplexKey.of(village, version + 1);
        ComplexKey endKey = ComplexKey.of(village, Long.MAX_VALUE);
        ViewQuery query = createQuery("formSubmission_by_village_and_server_version")
                .startKey(startKey)
                .endKey(endKey)
                .includeDocs(true);
        if (batchSize != null) {
            query.limit(batchSize);
        }
        logger.info("********** query***"+query);
        return db.queryView(query, FormSubmission.class);
    }
}