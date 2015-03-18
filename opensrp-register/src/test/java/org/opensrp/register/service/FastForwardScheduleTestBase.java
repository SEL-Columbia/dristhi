package org.opensrp.register.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import java.util.Arrays;
import java.util.List;

import static org.opensrp.common.util.DateUtil.today;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.*;
import static org.mockito.Mockito.*;

import org.opensrp.register.service.ActionService;
import org.opensrp.register.service.scheduling.ANCSchedulesService;
import org.opensrp.service.scheduling.ScheduleService;

public class FastForwardScheduleTestBase {
    private final ScheduleTrackingService scheduleTrackingService;
    private final ScheduleService scheduleService;
    private String scheduleName;
    private String expectedNextMilestone;
    private int visitNumberToTryAndFulfill;
    private ANCSchedulesService schedulesService;
    private Action serviceCall;
    private ActionService actionService;
    private String numberOfIFATabletsGiven;

    public FastForwardScheduleTestBase() {
        this.scheduleTrackingService = mock(ScheduleTrackingService.class);
        this.actionService = mock(ActionService.class);
        this.scheduleService = mock(ScheduleService.class);
        this.schedulesService = new ANCSchedulesService(scheduleTrackingService, actionService, scheduleService);
    }

    public FastForwardScheduleTestBase forANCSchedule() {
        this.scheduleName = SCHEDULE_ANC;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ancVisitHasHappened(caseId, "ANM 1", visitNumber, visitDate.toString());
            }
        };
        return this;
    }

    public FastForwardScheduleTestBase forTT1Schedule() {
        this.scheduleName = SCHEDULE_TT_1;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ttVisitHasHappened(caseId, "ANM 1", "tt1", visitDate.toString());
            }
        };
        return this;
    }

    public FastForwardScheduleTestBase forTT2Schedule() {
        this.scheduleName = SCHEDULE_TT_2;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ttVisitHasHappened(caseId, "ANM 1", "tt2", visitDate.toString());
            }
        };
        return this;
    }

    public FastForwardScheduleTestBase forIFA1Schedule() {
        return forIFASchedule(SCHEDULE_IFA_1);
    }

    public FastForwardScheduleTestBase forIFA2Schedule() {
        return forIFASchedule(SCHEDULE_IFA_2);
    }

    public FastForwardScheduleTestBase forIFA3Schedule() {
        return forIFASchedule(SCHEDULE_IFA_3);
    }

    private FastForwardScheduleTestBase forIFASchedule(String scheduleName) {
        this.scheduleName = scheduleName;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ifaTabletsGiven(caseId, "ANM 1", numberOfIFATabletsGiven, visitDate.toString());
            }
        };
        return this;
    }

    public FastForwardScheduleTestBase whenExpecting(String expectedNextMilestone) {
        this.expectedNextMilestone = expectedNextMilestone;
        return this;
    }

    public FastForwardScheduleTestBase providedWithVisitNumber(int visitNumberToTryAndFulfill) {
        this.visitNumberToTryAndFulfill = visitNumberToTryAndFulfill;
        return this;
    }

    public FastForwardScheduleTestBase providedWithNumberOfIFATablets(String numberOfIFATabletsGiven) {
        this.numberOfIFATabletsGiven = numberOfIFATabletsGiven;
        return this;
    }

    public void willFulfillFor(String... expectedVisitCodes) {
        EnrollmentRecord recordForNextMilestone = enrollmentRecord(scheduleName, expectedNextMilestone);

        when(scheduleTrackingService.getEnrollment("Case X", scheduleName)).thenReturn(recordForNextMilestone);

        LocalDate visitDate = today();

        serviceCall.make("Case X", visitNumberToTryAndFulfill, visitDate);

        verify(scheduleTrackingService, times(expectedVisitCodes.length)).fulfillCurrentMilestone(eq("Case X"), eq(scheduleName), eq(visitDate), any(Time.class));

        verifyAllActionInteractions(Arrays.asList(expectedVisitCodes));
    }

    public void willNotFulfillAnything() {
        willFulfillFor();
    }

    private void verifyAllActionInteractions(List<String> expectedVisitCodes) {
        if (expectedVisitCodes.isEmpty()) {
            verifyZeroInteractions(actionService);
        }
        for (String visitCode : expectedVisitCodes) {
            verify(actionService).markAlertAsClosed("Case X", "ANM 1", visitCode, today().toString());
        }
        verifyNoMoreInteractions(actionService);
    }

    private EnrollmentRecord enrollmentRecord(String scheduleName, String currentMilestone) {
        return new EnrollmentRecord("Case X", scheduleName, currentMilestone, null, null, null, null, null, null, null);
    }

    private class Action {
        public void make(String caseId, int visitNumber, LocalDate visitDate) {
            throw new RuntimeException("Unsupported.");
        }
    }
}
