package org.opensrp.scheduler.router;

import org.joda.time.DateTime;
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
import org.opensrp.dto.ActionData;
import org.opensrp.dto.AlertStatus;
import org.opensrp.scheduler.*;
import org.opensrp.scheduler.service.ActionService;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.service.EventService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.*;
import static org.opensrp.scheduler.Matcher.any;
import static org.opensrp.scheduler.Matcher.eq;

public class AlertRouterTest {
    public static final String SAMPLE_ID = "11";
    public static final String SCHEDULE_NAME = "scheduleName";
    public static final String MILESTONE_NAME = "milestoneName";
    public static final String WINDOW_NAME = "windowName";
    @Mock
    private HookedEvent firstAction;

    @Mock
    private HookedEvent secondAction;

    @Mock
    private HookedEvent thirdAction;

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private EventService eventService;

    @Mock
    private ActionService actionService;

    @InjectMocks
    private AlertRouter router = new AlertRouter();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldBeAbleToSetACatchAllRoute() {
        router.addRoute(any(), any(), any(), firstAction);

        assertRouteMatches("someSchedule", "someMilestone", "someWindow", firstAction, new HashMap<String, String>());
        assertRouteMatches("someOtherSchedule", "someOtherMilestone", "someOtherWindow", firstAction, new HashMap<String, String>());
    }


    @Test
    public void shouldBeAbleToSetARouteWhichMatchesBasedOnScheduleNameOnly() {
        router.addRoute(eq("Schedule X"), any(), any(), firstAction);

        assertNoRoutesMatch("someSchedule", "someMilestone", "someWindow");
        assertRouteMatches("Schedule X", "someMilestone", "someWindow", firstAction, new HashMap<String, String>());
    }

    @Test
    public void shouldBeAbleToSetARouteWhichMatchesBasedOnMilestoneNameOnly() {
        router.addRoute(any(), eq("Milestone X"), any(), firstAction);

        assertNoRoutesMatch("someSchedule", "someMilestone", "someWindow");
        assertRouteMatches("someOtherSchedule", "Milestone X", "someWindow", firstAction, new HashMap<String, String>());
    }

    @Test
    public void shouldBeAbleToSetARouteWhichMatchesBasedOnWindowNameOnly() {
        router.addRoute(any(), any(), eq("Window X"), firstAction);

        assertNoRoutesMatch("someSchedule", "someMilestone", "someWindow");
        assertRouteMatches("someOtherSchedule", "someOtherMilestone", "Window X", firstAction, new HashMap<String, String>());
    }

    @Test
    public void shouldBeAbleToSetARouteWhichMatchesBasedOnACombinationOfMatchers() {
        router.addRoute(eq("Milestone X"), any(), eq("Window X"), firstAction);

        assertNoRoutesMatch("someSchedule", "someMilestone", "someWindow");
        assertNoRoutesMatch("Milestone X", "someMilestone", "someWindow");
        assertNoRoutesMatch("someSchedule", "someMilestone", "Window X");
        assertRouteMatches("Milestone X", "someOtherMilestone", "Window X", firstAction, new HashMap<String, String>());
    }

    @Test
    public void shouldBeAbleToSetARouteWithExtraData() {
        router.addRoute(eq("Milestone X"), any(), eq("Window X"), firstAction).addExtraData("Unicorns", "AreFun");

        assertNoRoutesMatch("someSchedule", "someMilestone", "someWindow");
        assertNoRoutesMatch("Milestone X", "someMilestone", "someWindow");
        assertNoRoutesMatch("someSchedule", "someMilestone", "Window X");

        HashMap<String, String> extraData = new HashMap<>();
        extraData.put("Unicorns", "AreFun");
        assertRouteMatches("Milestone X", "someOtherMilestone", "Window X", firstAction, extraData);
    }

    @Test
    public void shouldInvokeTheActionOfOnlyTheFirstRouteWhichMatchesInTheOrderInWhichTheyWereAdded() {
        router.addRoute(any(), any(), eq("Window X"), firstAction);
        router.addRoute(any(), any(), any(), secondAction);

        assertRouteMatches("someSchedule", "someMilestone", "Window X", firstAction, new HashMap<String, String>());
        verifyZeroInteractions(secondAction);

        assertRouteMatches("someSchedule", "someMilestone", "someOtherWindow", secondAction, new HashMap<String, String>());
        verifyZeroInteractions(firstAction);
    }

    @Test
    public void shouldNotTryAndLookAtTheBestMatchButJustTheFirstProperMatch() {
        router.addRoute(any(), any(), eq("Window X"), firstAction);
        router.addRoute(eq("Milestone X"), any(), eq("Window X"), secondAction);

        assertRouteMatches("Milestone X", "someMilestone", "Window X", firstAction, new HashMap<String, String>());
        verifyZeroInteractions(secondAction);
    }

    @Test(expected = NoRoutesMatchException.class)
    public void shouldFailIfNoRoutesMatch() {
        router.handleAlerts(event("scheduleName", "milestoneName", "windowName"));
    }

    @Test
    public void shouldManuallyCallActionServiceIfExceptionHappen() throws IOException {
        String entityType  = "eT";
        String providerId = "PI";
        String enrollmentId = SAMPLE_ID;
        String eventId = SAMPLE_ID;
        DateTime sampleDate = new DateTime(0l);

        Map<String, String> metaData = new HashMap<>();
        metaData.put(HealthSchedulerService.MetadataField.enrollmentEvent.name(), SAMPLE_ID);

        Event event = new Event();
        event.setEntityType(entityType);
        event.setProviderId(providerId);

        Enrollment enrollment = mock(Enrollment.class);

        when(scheduleService.getEnrollment(enrollmentId)).thenReturn(enrollment);
        when(enrollment.getMetadata()).thenReturn(metaData);
        when(enrollment.getScheduleName()).thenReturn(SCHEDULE_NAME);
        when(enrollment.getCurrentMilestoneName()).thenReturn(MILESTONE_NAME);
        when(enrollment.getCurrentMilestoneStartDate()).thenReturn(sampleDate);
        when(enrollment.getStartOfWindowForCurrentMilestone(WindowName.max)).thenReturn(sampleDate);
        when(eventService.getById(eventId)).thenReturn(event);


        ActionData actionData = ActionData.createAlert(entityType, SCHEDULE_NAME, MILESTONE_NAME,
                AlertStatus.defaulted, sampleDate, sampleDate);
        Action action = new Action(SAMPLE_ID, providerId, actionData);


        router.addRoute(null);
        router.handleAlerts(event(SCHEDULE_NAME, MILESTONE_NAME, WINDOW_NAME));

        verify(actionService).alertForBeneficiary(action);

    }

    private void assertRouteMatches(String schedule, String milestone, String window, HookedEvent action, HashMap<String, String> extraData) {
        MotechEvent event = handleEvent(schedule, milestone, window);
        verify(action, times(1)).invoke(new MilestoneEvent(event), extraData);
    }

    private void assertNoRoutesMatch(String schedule, String milestone, String window) {
        try {
            handleEvent(schedule, milestone, window);
            fail("Expected no routes to match! It matched a route.");
        } catch (NoRoutesMatchException e) {
        }
    }

    private MotechEvent handleEvent(String schedule, String milestone, String window) {
        MotechEvent event = event(schedule, milestone, window);
        router.handleAlerts(event);
        return event;
    }

    private MotechEvent event(String scheduleName, String milestoneName, String windowName) {
        MilestoneAlert alert = mock(MilestoneAlert.class);
        when(alert.getMilestoneName()).thenReturn(milestoneName);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKeys.SCHEDULE_NAME, scheduleName);
        parameters.put(EventDataKeys.MILESTONE_NAME, alert);
        parameters.put(EventDataKeys.WINDOW_NAME, windowName);
        parameters.put(EXTERNAL_ID, SAMPLE_ID);
        parameters.put(ENROLLMENT_ID, SAMPLE_ID);

        return new MotechEvent("Subject", parameters);
    }
}
