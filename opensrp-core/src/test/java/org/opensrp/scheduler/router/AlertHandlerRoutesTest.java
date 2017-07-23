package org.opensrp.scheduler.router;

import org.junit.Test;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.opensrp.scheduler.*;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.motechproject.scheduletracking.api.domain.WindowName.max;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.*;

public class AlertHandlerRoutesTest {

    public static final String SCHEDULE_LAB = "EC EVENT";


    @Test
    public void shouldRouteToProperEcHandeler() {
        Event.of(SCHEDULE_LAB, "Milestone", max).shouldRouteToECAlertCreation();
    }


  /*
      @Test
    public void shouldSendMaxEventsOfANCNormalScheduleToForceFulfillAction() {
        Event.of(SCHEDULE_ANC, "ANC 1", max).shouldRouteToForceFulfillAction();
        Event.of(SCHEDULE_ANC, "ANC 3", max).shouldRouteToForceFulfillAction();
    }

    @Test
    public void shouldSendMaxEventsOfLabRemindersScheduleToForceFulfillAction() {
        Event.of(SCHEDULE_LAB, "EDD", max).shouldRouteToForceFulfillAction();
    }
    @Test
    public void shouldSendDueRemindersOfAllMotherSchedulesToCaptureRemindersAction() throws Exception {
        Event.of(SCHEDULE_ANC, "ANC 1", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_ANC, "ANC 1", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_LAB, "Reminder", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_LAB, "Reminder", late).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_EDD, "Reminder", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_EDD, "Reminder", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_IFA_1, "IFA 1", earliest).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_IFA_1, "IFA 1", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_IFA_1, "IFA 1", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_IFA_1, "IFA 2", earliest).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_IFA_1, "IFA 2", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_IFA_1, "IFA 2", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_IFA_1, "IFA 3", earliest).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_IFA_1, "IFA 3", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_IFA_1, "IFA 3", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_TT_1, "TT 1", earliest).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_TT_1, "TT 1", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_TT_1, "TT 1", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_TT_2, "TT 2", earliest).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_TT_2, "TT 2", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_TT_2, "TT 2", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_HB_TEST_1, "Hb Test 1", earliest).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_HB_TEST_1, "Hb Test 1", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_HB_TEST_1, "Hb Test 1", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_HB_TEST_2, "Hb Test 2", earliest).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_HB_TEST_2, "Hb Test 2", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_HB_TEST_2, "Hb Test 2", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_HB_FOLLOWUP_TEST, "Hb Followup Test", earliest).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_HB_FOLLOWUP_TEST, "Hb Followup Test", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_HB_FOLLOWUP_TEST, "Hb Followup Test", late).shouldRouteToAlertCreationActionForMother();

        Event.of(SCHEDULE_DELIVERY_PLAN, "Delivery Plan", earliest).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_DELIVERY_PLAN, "Delivery Plan", due).shouldRouteToAlertCreationActionForMother();
        Event.of(SCHEDULE_DELIVERY_PLAN, "Delivery Plan", late).shouldRouteToAlertCreationActionForMother();
    }
*/
   /* @Test
    public void shouldSendReminderForAllChildSchedulesToCaptureRemindersAction() throws Exception {
        Event.of(CHILD_SCHEDULE_BCG, "BCG", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_BCG, "BCG", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_BCG, "BCG", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_MEASLES, "Measles", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_MEASLES, "Measles", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_MEASLES, "Measles", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_MEASLES_BOOSTER, "Measles Booster", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_MEASLES_BOOSTER, "Measles Booster", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_MEASLES_BOOSTER, "Measles Booster", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_DPT_BOOSTER1, "dptbooster_1", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT_BOOSTER1, "dptbooster_1", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT_BOOSTER1, "dptbooster_1", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_DPT_BOOSTER2, "dptbooster_2", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT_BOOSTER2, "dptbooster_2", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_DPT_BOOSTER2, "dptbooster_2", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_OPV_0_AND_1, "opv_0", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_0_AND_1, "opv_0", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_0_AND_1, "opv_0", late).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_0_AND_1, "opv_0", max).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_OPV_0_AND_1, "opv_1", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_0_AND_1, "opv_1", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_0_AND_1, "opv_1", late).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_0_AND_1, "opv_1", max).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_OPV_2, "opv_2", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_2, "opv_2", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_2, "opv_2", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_OPV_3, "opv_3", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_3, "opv_3", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_3, "opv_3", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_OPV_BOOSTER, "opvbooster", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_BOOSTER, "opvbooster", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_OPV_BOOSTER, "opvbooster", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_PENTAVALENT_1, "pentavalent_1", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_PENTAVALENT_1, "pentavalent_1", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_PENTAVALENT_1, "pentavalent_1", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_PENTAVALENT_2, "pentavalent_2", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_PENTAVALENT_2, "pentavalent_2", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_PENTAVALENT_2, "pentavalent_2", late).shouldRouteToAlertCreationActionForChild();

        Event.of(CHILD_SCHEDULE_PENTAVALENT_3, "pentavalent_3", earliest).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_PENTAVALENT_3, "pentavalent_3", due).shouldRouteToAlertCreationActionForChild();
        Event.of(CHILD_SCHEDULE_PENTAVALENT_3, "pentavalent_3", late).shouldRouteToAlertCreationActionForChild();
    }

    @Test
    public void shouldSendDueRemindersOfAllEcsToCaptureRemindersAction() throws Exception {
        Event.of(EC_SCHEDULE_DMPA_INJECTABLE_REFILL, "DMPA Injectable Refill", earliest).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_DMPA_INJECTABLE_REFILL, "DMPA Injectable Refill", due).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_DMPA_INJECTABLE_REFILL, "DMPA Injectable Refill", late).shouldRouteToAlertCreationActionForEC();

        Event.of(EC_SCHEDULE_OCP_REFILL, "OCP Refill", earliest).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_OCP_REFILL, "OCP Refill", due).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_OCP_REFILL, "OCP Refill", late).shouldRouteToAlertCreationActionForEC();

        Event.of(EC_SCHEDULE_CONDOM_REFILL, "Condom Refill", earliest).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_CONDOM_REFILL, "Condom Refill", due).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_CONDOM_REFILL, "Condom Refill", late).shouldRouteToAlertCreationActionForEC();

        Event.of(EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP, "Female sterilization followup", earliest).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP, "Female sterilization followup", due).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP, "Female sterilization followup", late).shouldRouteToAlertCreationActionForEC();

        Event.of(EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP, "Male sterilization followup", earliest).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP, "Male sterilization followup", due).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP, "Male sterilization followup", late).shouldRouteToAlertCreationActionForEC();

        Event.of(EC_SCHEDULE_IUD_FOLLOWUP, "IUD followup", earliest).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_IUD_FOLLOWUP, "IUD followup", due).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_IUD_FOLLOWUP, "IUD followup", late).shouldRouteToAlertCreationActionForEC();

        Event.of(EC_SCHEDULE_FP_FOLLOWUP, "FP Referral Followup", earliest).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_FP_FOLLOWUP, "FP Referral Followup", due).shouldRouteToAlertCreationActionForEC();
        Event.of(EC_SCHEDULE_FP_FOLLOWUP, "FP Referral Followup", late).shouldRouteToAlertCreationActionForEC();
    }

    @Test
    public void shouldSendDueRemindersOfPNCCloseToAutoClosePNCAction() throws Exception {
        Event.of(SCHEDULE_AUTO_CLOSE_PNC, "PNC Close", due).shouldRouteToAutoClosePNCAction();
    }*/

    public static class Event {
        private final String schedule;
        private final String milestone;
        private final WindowName window;
        private String externalId;

        private Event(String schedule, String milestone, WindowName window) {
            this.schedule = schedule;
            this.milestone = milestone;
            this.window = window;
        }

        public static Event of(String schedule, String milestone, WindowName window) {
            return new Event(schedule, milestone, window);
        }

        public static Event of(String externalId, String schedule, String milestone, WindowName window) {
            Event event = new Event(schedule, milestone, window);
            event.externalId  = externalId;
            return event;
        }

        public void shouldRouteToECAlertCreation() {
            expectCallsToECAlertCreation(Expectation.of(1));
        }

        public void expectCallsToECAlertCreation(Expectation fulfillActionCallsExpected) {
            ECAlertCreationAction ecAlertCreationAction = mock(ECAlertCreationAction.class);

            MotechEvent event = routeEvent(ecAlertCreationAction, null, null);

            verify(ecAlertCreationAction, times(fulfillActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), fulfillActionCallsExpected.extraDataExpected);
        }

        /*
        public void shouldRouteToForceFulfillAction() {
            expectCalls(Expectation.of(1), Expectation.of(0), Expectation.of(0));
        }

        public void shouldRouteToAlertCreationActionForMother() {
            Map<String, String> extraData = new HashMap<>();
            extraData.put("beneficiaryType", "mother");
            expectCalls(Expectation.of(0), Expectation.of(1, extraData), Expectation.of(0));
        }

        public void shouldRouteToAlertCreationActionForChild() {
            Map<String, String> extraData = new HashMap<>();
            extraData.put("beneficiaryType", "child");
            expectCalls(Expectation.of(0), Expectation.of(1, extraData), Expectation.of(0));
        }

        public void shouldRouteToAlertCreationActionForEC() {
            Map<String, String> extraData = new HashMap<>();
            extraData.put("beneficiaryType", "ec");
            expectCalls(Expectation.of(0), Expectation.of(1, extraData), Expectation.of(0));
        }

        public void shouldRouteToAutoClosePNCAction() {
            expectCalls(Expectation.of(0), Expectation.of(0), Expectation.of(1));
        }

        private void expectCalls(Expectation fulfillActionCallsExpected, Expectation captureReminderActionCallsExpected, Expectation autoClosePNCActionCallsExpected) {
            HookedEvent forceFulfillAction = mock(ECAlertCreationAction.class);
            HookedEvent captureANCReminderAction = mock(HookedEvent.class);
            HookedEvent autoClosePNCAction = mock(HookedEvent.class);

            MotechEvent event = routeEvent(forceFulfillAction, captureANCReminderAction, autoClosePNCAction);

            verify(forceFulfillAction, times(fulfillActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), fulfillActionCallsExpected.extraDataExpected);
            verify(captureANCReminderAction, times(captureReminderActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), captureReminderActionCallsExpected.extraDataExpected);
            verify(autoClosePNCAction, times(autoClosePNCActionCallsExpected.numberOfCallsExpected)).invoke(new MilestoneEvent(event), autoClosePNCActionCallsExpected.extraDataExpected);
        }*/

        public MotechEvent routeEvent(HookedEvent ancMissedAction, HookedEvent captureANCReminderAction, HookedEvent autoClosePNCAction) {
            AlertRouter router = new AlertRouter();
            TaskSchedulerService sf = new TaskSchedulerService(null, null, router);
            new AlertHandler(sf, ancMissedAction);
            MotechEvent event = createMotechEvent();

            router.handleAlerts(event);

            return event;
        }

        public MotechEvent createMotechEvent() {
            MilestoneAlert alert = mock(MilestoneAlert.class);
            when(alert.getMilestoneName()).thenReturn(milestone);


            Map<String, Object> parameters = new HashMap<>();

            parameters.put(EXTERNAL_ID, externalId);
            parameters.put(SCHEDULE_NAME, schedule);
            parameters.put(MILESTONE_NAME, alert);
            parameters.put(WINDOW_NAME, window.toString());


            return new MotechEvent("Subject", parameters);
        }

        public static class Expectation {
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
