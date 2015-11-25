package org.opensrp.scheduler;

import java.util.Map;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.opensrp.form.service.FormSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("AlertCreationAction")
public class AlertCreationAction implements HookedEvent {
    private HealthSchedulerService scheduler;
    private FormSubmissionService formSubmissionService;

    @Autowired
    public AlertCreationAction(HealthSchedulerService scheduler, FormSubmissionService formSubmissionService) {
        this.scheduler = scheduler;
        this.formSubmissionService = formSubmissionService;
    }

    @Override
    public void invoke(MilestoneEvent event, Map<String, String> extraData) {
        Enrollment enr = scheduler.getEnrollment(event.externalId(), event.scheduleName());
        String entityType = enr.getMetadata().get("enrollmentEntityType");
        String formSubmissionId = enr.getMetadata().get("enrollmentFormSubmissionId");
        String providerId = formSubmissionService.findByInstanceId(formSubmissionId).anmId();
		scheduler.alertFor(event.windowName(), entityType, event.externalId(), providerId, event.scheduleName(), event.milestoneName(), event.startOfDueWindow(), event.startOfLateWindow(), event.startOfMaxWindow());
    }
}
