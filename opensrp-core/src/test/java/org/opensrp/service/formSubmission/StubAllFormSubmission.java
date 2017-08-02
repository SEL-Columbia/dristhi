package org.opensrp.service.formSubmission;


import org.ektorp.CouchDbConnector;
import org.opensrp.form.repository.AllFormSubmissions;

public class StubAllFormSubmission extends AllFormSubmissions{

    public StubAllFormSubmission(CouchDbConnector db) {
        super(db);
    }
}
