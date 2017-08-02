package org.opensrp.scheduler.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.opensrp.domain.Event;
import org.opensrp.scheduler.ECAlertCreationAction;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.MilestoneEvent;
import org.opensrp.scheduler.router.AlertHandlerRoutesTest;
import org.opensrp.service.EventService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.MILESTONE_NAME;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.SCHEDULE_NAME;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.WINDOW_NAME;

public class ECAlertCreationTest {

    @Mock
    HealthSchedulerService healthSchedulerService;
    @Mock
    EventService eventService;

    @InjectMocks
    ECAlertCreationAction ecAlertCreationAction = new ECAlertCreationAction(healthSchedulerService, eventService);

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void test() {
        String externalId = "sss";
        String scheduleBCG = "BCG";
        String milestone = "milestone";
        MotechEvent motechEvent = AlertHandlerRoutesTest.Event.of(externalId, scheduleBCG, milestone, WindowName.max).createMotechEvent();
        MilestoneEvent milestoneEvent = new MilestoneEvent(motechEvent);
        String entityType = "type";
        String providerId = "anm 1";
        Enrollment enrollment = mock(Enrollment.class);
        Map<String, String> metaData = new HashMap<>();
        String entityId = "entityId";
        metaData.put(HealthSchedulerService.MetadataField.enrollmentEvent.name(), entityId);

        when(healthSchedulerService.getEnrollment(externalId, scheduleBCG)).thenReturn(enrollment);
        when(enrollment.getMetadata()).thenReturn(metaData);
        when(eventService.getById(entityId)).thenReturn(new Event().withEntityType(entityType).withProviderId(providerId));

        ecAlertCreationAction.invoke(milestoneEvent, Collections.EMPTY_MAP);

        verify(healthSchedulerService).alertFor(milestoneEvent.windowName(), entityType, milestoneEvent.externalId(), providerId,
                milestoneEvent.scheduleName(), milestoneEvent.milestoneName(), milestoneEvent.startOfDueWindow(),
                milestoneEvent.startOfLateWindow(), milestoneEvent.startOfMaxWindow());
    }
}
