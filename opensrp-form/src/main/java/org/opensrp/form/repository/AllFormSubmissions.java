package org.opensrp.form.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.form.domain.FormSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.opensrp.common.AllConstants;

import java.util.List;

@Repository
public class AllFormSubmissions extends MotechBaseRepository<FormSubmission> {
    @Autowired
    protected AllFormSubmissions(@Qualifier(AllConstants.OPENSRP_FORM_DATABASE_CONNECTOR) CouchDbConnector db) {
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

    @View(name = "formSubmission_by_anm_and_server_version",
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
    
    @View(name = "formSubmission_by_form_name_and_server_version", 
    		map = "function(doc) { if (doc.type === 'FormSubmission') { emit([doc.formName, doc.serverVersion]); } }")
    public List<FormSubmission> findByFormName(String formName, long version) {
    	ComplexKey startKey = ComplexKey.of(formName, version + 1);
        ComplexKey endKey = ComplexKey.of(formName, Long.MAX_VALUE);
        return db.queryView(createQuery("formSubmission_by_form_name_and_server_version")
        		.startKey(startKey)
                .endKey(endKey)
                .includeDocs(true), FormSubmission.class);
    }

    @View(name = "formSubmission_by_metadata_keyval", 
    		map = "function(doc) { if (doc.type === 'FormSubmission') { "
    				+ "if(doc.metadata){"
    				+ "for(var key in doc.metadata) {emit([key, doc.metadata[key]]);}"
    				+ "}"
    				+ "}}")
    public List<FormSubmission> findByMetadata(String key, Object value) {
        ComplexKey ckey = ComplexKey.of(key, value);
        return db.queryView(createQuery("formSubmission_by_metadata_keyval")
                .key(ckey)
                .includeDocs(true), FormSubmission.class);
    }
    /**
     * Get form submissions from the specified database
     * @param sourceDb
     * @param serverVersion
     * @param batchSize
     * @return
     */
    public List<FormSubmission> allFormSubmissions(CouchDbConnector sourceDb,long serverVersion, Integer batchSize) {
        ComplexKey startKey = ComplexKey.of(serverVersion + 1);
        ComplexKey endKey = ComplexKey.of(Long.MAX_VALUE);
        ViewQuery query = createQuery("formSubmission_by_server_version")
                .startKey(startKey)
                .endKey(endKey)
                .includeDocs(true);

        if (batchSize != null) {
            query.limit(batchSize);
        }
        return sourceDb.queryView(query, FormSubmission.class);
    }

}
