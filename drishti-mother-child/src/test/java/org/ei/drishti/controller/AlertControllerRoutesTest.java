package org.ei.drishti.controller;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.AlertRouter;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.scheduler.DrishtiSchedules.SCHEDULE_ANC;
import static org.mockito.Mockito.*;
import static org.motechproject.scheduletracking.api.domain.WindowName.max;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.*;

public class AlertControllerRoutesTest {
    @Test
    public void shouldSendMaxEventsOfANCNormalToANCMissedAction() {
        Event.of(SCHEDULE_ANC, "ANC 1", max).shouldRouteToANCMissedAction();
        Event.of(SCHEDULE_ANC, "ANC 3", max).shouldRouteToANCMissedAction();
        Event.of("Some Other Schedule", "Some milestone", max).shouldRouteToGroupSMSAction();
    }

    private static class Event {
        private final String schedule;
        private final String milestone;
        private final WindowName window;

        private Event(String schedule, String milestone, WindowName window) {
            this.schedule = schedule;
            this.milestone = milestone;
            this.window = window;
        }

        public static Event of(String schedule, String milestone, WindowName window) {
            return new Event(schedule, milestone, window);
        }

        public void shouldRouteToANCMissedAction() {
            expectCalls(1, 0);
        }

        public void shouldRouteToGroupSMSAction() {
            expectCalls(0, 1);
        }

        private void expectCalls(int numberOfANCMissedActionCallsExpected, int numberOfGroupSMSActionCallsExpected) {
            Action groupSMSAction = mock(Action.class);
            Action ancMissedAction = mock(Action.class);

            MotechEvent event = routeEvent(groupSMSAction, ancMissedAction);

            verify(ancMissedAction, times(numberOfANCMissedActionCallsExpected)).invoke(new MilestoneEvent(event));
            verify(groupSMSAction, times(numberOfGroupSMSActionCallsExpected)).invoke(new MilestoneEvent(event));
        }

        private MotechEvent routeEvent(Action groupSMSAction, Action ancMissedAction) {
            AlertRouter router = new AlertRouter();
            new AlertController(router, groupSMSAction, ancMissedAction);
            MotechEvent event = eventFor(schedule, milestone, window);

            router.handle(event);

            return event;
        }

        private MotechEvent eventFor(String schedule, String milestone, WindowName window) {
            MilestoneAlert alert = mock(MilestoneAlert.class);
            when(alert.getMilestoneName()).thenReturn(milestone);

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put(SCHEDULE_NAME, schedule);
            parameters.put(MILESTONE_NAME, alert);
            parameters.put(WINDOW_NAME, window.toString());
            parameters.put(EXTERNAL_ID, "Case X");

            return new MotechEvent("Subject", parameters);
        }
    }
}
