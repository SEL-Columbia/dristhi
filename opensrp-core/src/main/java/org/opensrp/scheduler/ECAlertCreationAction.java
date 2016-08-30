package org.opensrp.scheduler;

import static java.text.MessageFormat.format;

import java.util.Map;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.opensrp.domain.Event;
import org.opensrp.scheduler.HealthSchedulerService.MetadataField;
import org.opensrp.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ECAlertCreationAction")
public class ECAlertCreationAction implements HookedEvent {
    private static Logger logger = LoggerFactory.getLogger(ECAlertCreationAction.class.toString());

    private HealthSchedulerService scheduler;
    private EventService eventService;

    @Autowired
    public ECAlertCreationAction(HealthSchedulerService scheduler, EventService eventService) {
        this.scheduler = scheduler;
        this.eventService = eventService;
    }

    @Override
    public void invoke(MilestoneEvent motechEvent, Map<String, String> extraData) {
        Enrollment enr = scheduler.getEnrollment(motechEvent.externalId(), motechEvent.scheduleName());
        String eventId = enr.getMetadata().get(MetadataField.enrollmentFormSubmission.name());
        Event event = eventService.getById(eventId);
        String entityType = event.getEntityType();
        
        logger.debug(format("Generating alert for entity {0} of type {1} , formSubmission {2} "
        		+ "for schedule {3} in window {4} ", 
        		motechEvent.externalId(), entityType, event.getId(), enr.getScheduleName(), motechEvent.windowName()));
		
        scheduler.alertFor(motechEvent.windowName(), entityType, motechEvent.externalId(), event.getProviderId(), motechEvent.scheduleName(), motechEvent.milestoneName(), motechEvent.startOfDueWindow(), motechEvent.startOfLateWindow(), motechEvent.startOfMaxWindow());
    }
    
   
}
