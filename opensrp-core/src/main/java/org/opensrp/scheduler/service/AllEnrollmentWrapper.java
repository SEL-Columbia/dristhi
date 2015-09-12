package org.opensrp.scheduler.service;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
class AllEnrollmentWrapper extends AllEnrollments{
	@Autowired
    private AllSchedules allSchedules;
	
	@Autowired
	public AllEnrollmentWrapper(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db) {
		super(db);
	}

	 private static final String FUNCTION_DOC_EMIT_DOC_STATUS_AND_ENROLLED_ON = "function(doc) { if(doc.type === 'Enrollment') emit([doc.status,doc.enrolledOn], doc._id);}";

	    @View(name = "by_status_date_enrolled", map = FUNCTION_DOC_EMIT_DOC_STATUS_AND_ENROLLED_ON)
	    public List<Enrollment> findByEnrollmentDate(String status, DateTime start, DateTime end) {
	        List<Enrollment> enrollments = queryView("by_status_date_enrolled", ComplexKey.of(status, start, end));
	        return populateWithSchedule(enrollments);
	    }
	    
	    private List<Enrollment> populateWithSchedule(List<Enrollment> enrollments) {
	        for (Enrollment enrollment : enrollments)
	            populateSchedule(enrollment);
	        return enrollments;
	    }

	    private Enrollment populateSchedule(Enrollment enrollment) {
	        enrollment.setSchedule(allSchedules.getByName(enrollment.getScheduleName()));
	        return enrollment;
	    }
}
