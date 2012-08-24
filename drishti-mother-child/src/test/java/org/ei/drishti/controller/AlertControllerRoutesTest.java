package org.ei.drishti.controller;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.AlertRouter;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.junit.Test;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.HashMap;
import java.util.Map;

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
        Event.of(SCHEDULE_ANC, "ANC 1", WindowName.due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_ANC, "ANC 1", WindowName.late).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_LAB, "Reminder", WindowName.due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_LAB, "Reminder", WindowName.late).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_EDD, "Reminder", WindowName.due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_EDD, "Reminder", WindowName.late).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_IFA, "IFA 1", WindowName.due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_IFA, "IFA 2", WindowName.late).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_TT, "SomeMilestone", WindowName.due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_TT, "SomeOtherMilestone", WindowName.late).shouldRouteToAlertCreationActionForMother();
    }

    @Test
    public void shouldDoNothingForAllChildSchedules() throws Exception {
        Event.of(CHILD_SCHEDULE_BCG, "REMINDER", WindowName.due).shouldDoNothing();
        Event.of(CHILD_SCHEDULE_BCG, "REMINDER", WindowName.late).shouldDoNothing();
        Event.of(CHILD_SCHEDULE_DPT, "DPT 1", WindowName.due).shouldDoNothing();
        Event.of(CHILD_SCHEDULE_DPT, "DPT 1", WindowName.late).shouldDoNothing();
        Event.of(CHILD_SCHEDULE_HEPATITIS, "Hepatitis B3", WindowName.due).shouldDoNothing();
        Event.of(CHILD_SCHEDULE_HEPATITIS, "Hepatitis B3", WindowName.late).shouldDoNothing();
        Event.of(CHILD_SCHEDULE_MEASLES, "REMINDER", WindowName.due).shouldDoNothing();
        Event.of(CHILD_SCHEDULE_MEASLES, "REMINDER", WindowName.late).shouldDoNothing();
        Event.of(CHILD_SCHEDULE_OPV, "OPV 1", WindowName.due).shouldDoNothing();
        Event.of(CHILD_SCHEDULE_OPV, "OPV 1", WindowName.late).shouldDoNothing();
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
            expectCalls(Expectation.of(1), Expectation.of(0), Expectation.of(0));
        }

        public void shouldRouteToGroupSMSAction() {
            expectCalls(Expectation.of(0), Expectation.of(1), Expectation.of(0));
        }

        public void shouldRouteToAlertCreationActionForMother() {
            Map<String, String> extraData = new HashMap<>();
            extraData.put("beneficiaryType", "mother");
            expectCalls(Expectation.of(0), Expectation.of(0), Expectation.of(1, extraData));
        }

        public void shouldDoNothing() {
            expectCalls(Expectation.of(0), Expectation.of(0), Expectation.of(0));
        }

        public void shouldRouteToAlertCreationActionForChild() {
            Map<String, String> extraData = new HashMap<>();
            extraData.put("beneficiaryType", "child");
            expectCalls(Expectation.of(0), Expectation.of(0), Expectation.of(1, extraData));
        }

        private void expectCalls(Expectation fulfillActionCallsExpected, Expectation groupSMSActionCallsExpected, Expectation captureReminderActionCallsExpected) {
            Action groupSMSAction = mock(Action.class);
            Action forceFulfillAction = mock(Action.class);
            Action captureANCReminderAction = mock(Action.class);

            MotechEvent event = routeEvent(groupSMSAction, forceFulfillAction, captureANCReminderAction);

            verify(forceFulfillAction, times(fulfillActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), fulfillActionCallsExpected.extraDataExpected);
            verify(groupSMSAction, times(groupSMSActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), groupSMSActionCallsExpected.extraDataExpected);
            verify(captureANCReminderAction, times(captureReminderActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), captureReminderActionCallsExpected.extraDataExpected);
        }

        private MotechEvent routeEvent(Action groupSMSAction, Action ancMissedAction, Action captureANCReminderAction) {
            AlertRouter router = new AlertRouter();
            new AlertController(router, groupSMSAction, ancMissedAction, captureANCReminderAction);
            MotechEvent event = org.ei.drishti.util.Event.create().withMilestone(milestone).withSchedule(schedule).withWindow(window).build();

            router.handle(event);

            return event;
        }

        private static class Expectation {
            private final int numberOfCallsExpected;
            private final Map<String, String> extraDataExpected;

            public static Expectation of(int numberOfCallsExpected) {
                return new Expectation(numberOfCallsExpected, new HashMap<String, String>());
            }

            public static Expectation of(int numberOfCallsExpected, Map<String, String> extraDataExpected) {
                return new Expectation(numberOfCallsExpected, extraDataExpected);
            }

            public Expectation(int numberOfCallsExpected, Map<String, String> extraDataExpected) {
                this.numberOfCallsExpected = numberOfCallsExpected;
                this.extraDataExpected = extraDataExpected;
            }
        }
    }
}
