package org.ei.drishti.controller;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.AlertRouter;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.junit.Test;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.*;
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
    public void shouldSendReminderForAllChildSchedulesToCaptureRemindersAction() throws Exception {
        Event.of(CHILD_SCHEDULE_BCG, "BCG", WindowName.due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_BCG, "BCG", WindowName.late).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT1, "DPT 1", WindowName.due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT1, "DPT 1", WindowName.late).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT2, "DPT 2", WindowName.due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT2, "DPT 2", WindowName.late).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT2, "DPT 3", WindowName.due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT2, "DPT 3", WindowName.late).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_HEPATITIS, "Hepatitis B3", WindowName.due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_HEPATITIS, "Hepatitis B3", WindowName.late).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_MEASLES, "Measles", WindowName.due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_MEASLES, "Measles", WindowName.late).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_MEASLES_BOOSTER, "Measles Booster", WindowName.due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_MEASLES_BOOSTER, "Measles Booster", WindowName.late).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV, "OPV 1", WindowName.due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV, "OPV 1", WindowName.late).shouldRouteToAlertCreationActionForChild();
    }

    @Test
    public void shouldSendDueRemindersOfAllEcsToCaptureRemindersAction() throws Exception {
        Event.of(EC_SCHEDULE_FP_COMPLICATION, "FP Complications", WindowName.late).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_DMPA_INJECTABLE_REFILL, "DMPA Injectable Refill", WindowName.due).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_DMPA_INJECTABLE_REFILL, "DMPA Injectable Refill", WindowName.late).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_OCP_REFILL, "OCP Refill", WindowName.due).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_OCP_REFILL, "OCP Refill", WindowName.late).shouldRouteToAlertCreationActionForEC();
    }

    @Test
    public void shouldSendDueRemindersOfPNCCloseToAutoClosePNCAction() throws Exception {
        Event.of(SCHEDULE_AUTO_CLOSE_PNC, "PNC Close", WindowName.due).shouldRouteToAutoClosePNCAction();
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
            expectCalls(Expectation.of(1), Expectation.of(0), Expectation.of(0), Expectation.of(0));
        }

        public void shouldRouteToGroupSMSAction() {
            expectCalls(Expectation.of(0), Expectation.of(1), Expectation.of(0), Expectation.of(0));
        }

        public void shouldRouteToAlertCreationActionForMother() {
            Map<String, String> extraData = new HashMap<>();
            extraData.put("beneficiaryType", "mother");
            expectCalls(Expectation.of(0), Expectation.of(0), Expectation.of(1, extraData), Expectation.of(0));
        }

        public void shouldRouteToAlertCreationActionForChild() {
            Map<String, String> extraData = new HashMap<>();
            extraData.put("beneficiaryType", "child");
            expectCalls(Expectation.of(0), Expectation.of(0), Expectation.of(1, extraData), Expectation.of(0));
        }

        public void shouldRouteToAlertCreationActionForEC() {
            Map<String, String> extraData = new HashMap<>();
            extraData.put("beneficiaryType", "ec");
            expectCalls(Expectation.of(0), Expectation.of(0), Expectation.of(1, extraData), Expectation.of(0));
        }

        public void shouldRouteToAutoClosePNCAction() {
            expectCalls(Expectation.of(0), Expectation.of(0), Expectation.of(0), Expectation.of(1));
        }

        private void expectCalls(Expectation fulfillActionCallsExpected, Expectation groupSMSActionCallsExpected, Expectation captureReminderActionCallsExpected, Expectation autoClosePNCActionCallsExpected) {
            Action groupSMSAction = mock(Action.class);
            Action forceFulfillAction = mock(Action.class);
            Action captureANCReminderAction = mock(Action.class);
            Action autoClosePNCAction = mock(Action.class);

            MotechEvent event = routeEvent(groupSMSAction, forceFulfillAction, captureANCReminderAction, autoClosePNCAction);

            verify(forceFulfillAction, times(fulfillActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), fulfillActionCallsExpected.extraDataExpected);
            verify(groupSMSAction, times(groupSMSActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), groupSMSActionCallsExpected.extraDataExpected);
            verify(captureANCReminderAction, times(captureReminderActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), captureReminderActionCallsExpected.extraDataExpected);
            verify(autoClosePNCAction, times(autoClosePNCActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), autoClosePNCActionCallsExpected.extraDataExpected);
        }

        private MotechEvent routeEvent(Action groupSMSAction, Action ancMissedAction, Action captureANCReminderAction, Action autoClosePNCAction) {
            AlertRouter router = new AlertRouter();
            new AlertController(router, groupSMSAction, ancMissedAction, captureANCReminderAction, autoClosePNCAction);
            MotechEvent event = org.ei.drishti.util.Event
                    .create()
                    .withMilestone(milestone)
                    .withSchedule(schedule)
                    .withWindow(window)
                    .build();

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
