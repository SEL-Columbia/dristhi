package org.opensrp.scheduler;

import static java.text.MessageFormat.format;

import java.util.Map;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.domain.SubFormData;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.scheduler.HealthSchedulerService.MetadataField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("AlertCreationAction")
public class AlertCreationAction implements HookedEvent {
    private static Logger logger = LoggerFactory.getLogger(AlertCreationAction.class.toString());

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
        String formSubmissionId = enr.getMetadata().get(MetadataField.enrollmentEvent.name());
        FormSubmission fs = formSubmissionService.findByInstanceId(formSubmissionId);
        String entityType = getEntityType(event.externalId(), fs);
        
        logger.debug(format("Generating alert for entity {0} of type {1} , formSubmission {2} "
        		+ "for schedule {3} in window {4} ", 
        		event.externalId(), entityType, fs.instanceId(), enr.getScheduleName(), event.windowName()));
		
        scheduler.alertFor(event.windowName(), entityType, event.externalId(), fs.anmId(), event.scheduleName(), event.milestoneName(), event.startOfDueWindow(), event.startOfLateWindow(), event.startOfMaxWindow());
    }
    
    String getEntityType(String externalId, FormSubmission formSubmission) {
		if(formSubmission.entityId().equalsIgnoreCase(externalId)){
			return formSubmission.bindType();
		}
		
		for (SubFormData sf : formSubmission.subForms()) {
			for (Map<String, String> fld : sf.instances()) {
				if(fld.get("id").equalsIgnoreCase(externalId)){
					return sf.bindType();
				}
			}
		}
		return "unknown";
	}
}
