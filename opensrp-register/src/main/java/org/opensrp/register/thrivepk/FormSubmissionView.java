package org.opensrp.register.thrivepk;

import java.io.InputStream;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.ListFunction;
import org.ektorp.support.Lists;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.form.domain.FormSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class FormSubmissionView extends MotechBaseRepository<FormSubmission>{
	@Autowired
	protected FormSubmissionView(@Qualifier(AllConstants.OPENSRP_FORM_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(FormSubmission.class, db);
	}

	@Lists(value = { @ListFunction(name = "dump_csv", file = "dump_csv.js") })
	public InputStream dump_csv(String formName) {
		ViewQuery query = createQuery("formSubmission_by_form_name").includeDocs(true).key(formName);
		query.listName("dump_csv");
		return db.queryForStream(query);
	}
	
	@Lists(value = { @ListFunction(name = "aggregate_child_enrollment_by_center_csv", file = "aggregate_child_enrollment_by_center_csv.js") })
	public InputStream aggregate_child_enrollment_by_center_csv(String formName) {
		ViewQuery query = createQuery("formSubmission_by_form_name").includeDocs(true).key(formName);
		query.listName("aggregate_child_enrollment_by_center_csv");
		return db.queryForStream(query);
	}
	
	@Lists(value = { @ListFunction(name = "aggregate_woman_enrollment_by_center_csv", file = "aggregate_woman_enrollment_by_center_csv.js") })
	public InputStream aggregate_woman_enrollment_by_center_csv(String formName) {
		ViewQuery query = createQuery("formSubmission_by_form_name").includeDocs(true).key(formName);
		query.listName("aggregate_woman_enrollment_by_center_csv");
		return db.queryForStream(query);
	}
	 
}
