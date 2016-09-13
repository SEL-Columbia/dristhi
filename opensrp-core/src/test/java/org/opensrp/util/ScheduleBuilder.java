package org.opensrp.util;

import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.argThat;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.mockito.ArgumentMatcher;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.opensrp.scheduler.HealthSchedulerService.MetadataField;
import org.opensrp.scheduler.MilestoneEvent;

public class ScheduleBuilder{

	public static EnrollmentRequest enrollmentFor(final String entityId, final String scheduleName, final String name, final String referenceDate) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return entityId.equals(request.getExternalId()) && parse(referenceDate).equals(request.getReferenceDate())
                        && scheduleName.equals(request.getScheduleName()) && name.equals(request.getStartingMilestoneName());
            }
        });
    }

	public static EnrollmentRecord enrollmentRecord(String entityId, String scheduleName, String currentMilestone) {
		return new EnrollmentRecord(entityId, scheduleName, currentMilestone, null, null, null, null, null, null, null);
	}	
	
	public static Enrollment enrollment(String entityId, String scheduleName, String currentMilestone, DateTime startOfSchedule, DateTime enrolledOn, EnrollmentStatus enrollmentStatus, String formSubmissionId) {
		Schedule schedule = new Schedule(scheduleName);
		Map<String, String> map = new HashMap<>();
		
		map.put(MetadataField.lastUpdate.name(), new DateTime().toString());
		map.put(MetadataField.enrollmentEvent.name(), formSubmissionId);
		return new Enrollment(entityId, schedule, currentMilestone, startOfSchedule, enrolledOn, null, enrollmentStatus, map);
	}	
	
    public static MilestoneEvent event(String externalID, String scheduleName, String milestone, WindowName window, DateTime dueWindowStart, DateTime lateWindowStart, DateTime maxWindowStart) {
        return new MilestoneEvent(Event.create().withSchedule(scheduleName).withMilestone(milestone).withWindow(window).withExternalId(externalID)
                .withDueWindowStartDate(dueWindowStart).withLateWindowStartDate(lateWindowStart).withMaxWindowStartDate(maxWindowStart).build());
    }
}
