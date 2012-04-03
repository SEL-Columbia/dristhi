package org.ei.drishti.controller;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.AlertRouter;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.WindowName;

import static org.ei.drishti.scheduler.DrishtiSchedules.*;
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

    @Test
    public void shouldSendDueRemindersOfAllMotherSchedulesToCaptureRemindersAction() throws Exception {
        Event.of(SCHEDULE_ANC, "ANC 1", WindowName.due).shouldRouteToCaptureRemindersAction();
        Event.of(SCHEDULE_ANC, "ANC 1", WindowName.late).shouldRouteToCaptureRemindersAction();
        Event.of(SCHEDULE_LAB, "Reminder", WindowName.due).shouldRouteToCaptureRemindersAction();
        Event.of(SCHEDULE_LAB, "Reminder", WindowName.late).shouldRouteToCaptureRemindersAction();
        Event.of(SCHEDULE_EDD, "Reminder", WindowName.due).shouldRouteToCaptureRemindersAction();
        Event.of(SCHEDULE_EDD, "Reminder", WindowName.late).shouldRouteToCaptureRemindersAction();
        Event.of(SCHEDULE_IFA, "IFA 1", WindowName.due).shouldRouteToCaptureRemindersAction();
        Event.of(SCHEDULE_IFA, "IFA 2", WindowName.late).shouldRouteToCaptureRemindersAction();
        Event.of(SCHEDULE_TT, "SomeMilestone", WindowName.due).shouldRouteToCaptureRemindersAction();
        Event.of(SCHEDULE_TT, "SomeOtherMilestone", WindowName.late).shouldRouteToCaptureRemindersAction();
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
            expectCalls(1, 0, 0);
        }

        public void shouldRouteToGroupSMSAction() {
            expectCalls(0, 1, 0);
        }

        public void shouldRouteToCaptureRemindersAction() {
            expectCalls(0, 0, 1);
        }

        private void expectCalls(int numberOfForceFulfillActionCallsExpected, int numberOfGroupSMSActionCallsExpected, int numberOfCaptureReminderActionCallsExpected) {
            Action groupSMSAction = mock(Action.class);
            Action forceFulfillAction = mock(Action.class);
            Action captureANMReminderAction = mock(Action.class);

            MotechEvent event = routeEvent(groupSMSAction, forceFulfillAction, captureANMReminderAction);

            verify(forceFulfillAction, times(numberOfForceFulfillActionCallsExpected)).invoke(new MilestoneEvent(event));
            verify(groupSMSAction, times(numberOfGroupSMSActionCallsExpected)).invoke(new MilestoneEvent(event));
            verify(captureANMReminderAction, times(numberOfCaptureReminderActionCallsExpected)).invoke(new MilestoneEvent(event));
        }

        private MotechEvent routeEvent(Action groupSMSAction, Action ancMissedAction, Action captureANMReminderAction) {
            AlertRouter router = new AlertRouter();
            new AlertController(router, groupSMSAction, ancMissedAction, captureANMReminderAction);
            MotechEvent event = org.ei.drishti.util.Event.create().withMilestone(milestone).withSchedule(schedule).withWindow(window).build();

            router.handle(event);

            return event;
        }
    }
}
