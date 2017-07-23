package org.opensrp.scheduler.service;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllEnrollmentWrapper extends AllEnrollments{
	@Autowired
    private AllScheduleWrapper allSchedules;
	
	@Autowired
	public AllEnrollmentWrapper(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db) {
		super(db);
	}

	@View(name = "find_by_external_id_and_schedule_name", map = "function(doc) {if(doc.type === 'Enrollment') emit([doc.externalId, doc.scheduleName.toUpperCase()]);}")
    public Enrollment getEnrollment(String externalId, String scheduleName) {
		if(scheduleName.equalsIgnoreCase("Measles 1")){
			log.info("measles 1");
		}
        List<Enrollment> enrollments = queryView("find_by_external_id_and_schedule_name", ComplexKey.of(externalId, scheduleName.toUpperCase()));
        return enrollments.isEmpty() ? null : populateSchedule(enrollments.get(0));
    }
	
	private static final String FUNCTION_DOC_EMIT_DOC_STATUS_AND_ENROLLED_ON = "function(doc) { if(doc.type === 'Enrollment') emit([doc.status,doc.enrolledOn], doc._id);}";

    @View(name = "by_status_date_enrolled", map = FUNCTION_DOC_EMIT_DOC_STATUS_AND_ENROLLED_ON)
    public List<Enrollment> findByEnrollmentDate(String status, DateTime start, DateTime end) {
    	ComplexKey s = ComplexKey.of(status, start);
    	ComplexKey e = ComplexKey.of(status, end);
        List<Enrollment> enrollments = db.queryView(createQuery("by_status_date_enrolled").startKey(s).endKey(e).includeDocs(true), Enrollment.class);
        return populateWithSchedule(enrollments);
    }

    private static final String FUNCTION_DOC_EMIT_LAST_UPDATE = "function(doc) { if(doc.type === 'Enrollment') emit(doc.metadata.lastUpdate, doc._id);}";
    
    @View(name = "by_last_update", map = FUNCTION_DOC_EMIT_LAST_UPDATE)
    public List<Enrollment> findByLastUpDate(DateTime start, DateTime end) {
        List<Enrollment> enrollments = db.queryView(createQuery("by_last_update").startKey(start).endKey(end).includeDocs(true), Enrollment.class);
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
