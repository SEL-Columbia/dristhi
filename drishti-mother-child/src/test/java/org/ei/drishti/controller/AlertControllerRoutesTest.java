package org.ei.drishti.controller;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.AlertRouter;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.util.EventBuilder;
import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.WindowName;

import static org.ei.drishti.scheduler.DrishtiSchedules.SCHEDULE_ANC;
import static org.ei.drishti.scheduler.DrishtiSchedules.SCHEDULE_LAB;
import static org.mockito.Mockito.*;
import static org.motechproject.scheduletracking.api.domain.WindowName.max;

public class AlertControllerRoutesTest {
    @Test
    public void shouldSendMaxEventsOfANCNormalScheduleToForceFulfillAction() {
        Event.of(SCHEDULE_ANC, "ANC 1", max).shouldRouteToForceFulfillAction();
        Event.of(SCHEDULE_ANC, "ANC 3", max).shouldRouteToForceFulfillAction();
        Event.of("Some Other Schedule", "Some milestone", max).shouldRouteToGroupSMSAction();
    }

    @Test
    public void shouldSendMaxEventsOfLabRemindersScheduleToForceFulfillAction() {
        Event.of(SCHEDULE_LAB, "EDD", max).shouldRouteToForceFulfillAction();
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

        public void shouldRouteToForceFulfillAction() {
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
            new AlertController(router, groupSMSAction, ancMissedAction, null);
            MotechEvent event = org.ei.drishti.util.Event.create().withMilestone(milestone).withSchedule(schedule).withWindow(window).build();

            router.handle(event);

            return event;
        }
    }
}
