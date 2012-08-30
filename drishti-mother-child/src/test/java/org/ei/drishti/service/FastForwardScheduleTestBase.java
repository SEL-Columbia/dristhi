package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareInformation;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static org.ei.drishti.scheduler.DrishtiSchedules.*;
import static org.mockito.Mockito.*;

public class FastForwardScheduleTestBase {
    private final ScheduleTrackingService scheduleTrackingService;
    private String scheduleName;
    private String expectedNextMilestone;
    private int visitNumberToTryAndFulfill;
    private ANCSchedulesService schedulesService;
    private Action serviceCall;
    private ActionService actionService;

    public FastForwardScheduleTestBase() {
        this.scheduleTrackingService = mock(ScheduleTrackingService.class);
        this.actionService = mock(ActionService.class);
        this.schedulesService = new ANCSchedulesService(scheduleTrackingService, actionService);
    }

    public FastForwardScheduleTestBase forANCSchedule() {
        this.scheduleName = SCHEDULE_ANC;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ancVisitHasHappened(new AnteNatalCareInformation(caseId, "ANM 1", visitNumber));
            }
        };
        return this;
    }

    public FastForwardScheduleTestBase forTTSchedule() {
        this.scheduleName = SCHEDULE_TT;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ttVisitHasHappened(caseId, visitNumber, visitDate);
            }
        };
        return this;
    }

    public FastForwardScheduleTestBase forIFASchedule() {
        this.scheduleName = SCHEDULE_IFA;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ifaVisitHasHappened(new AnteNatalCareInformation(caseId, "ANM 1", 0));
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

    public void willFulfillFor(String... expectedVisitCodes) {
        EnrollmentRecord recordForNextMilestone = enrollmentRecord(scheduleName, expectedNextMilestone);

        when(scheduleTrackingService.getEnrollment("Case X", scheduleName)).thenReturn(recordForNextMilestone);

        LocalDate visitDate = DateUtil.today();

        serviceCall.make("Case X", visitNumberToTryAndFulfill, visitDate);

        verify(scheduleTrackingService, times(expectedVisitCodes.length)).fulfillCurrentMilestone(eq("Case X"), eq(scheduleName), eq(visitDate), any(Time.class));

        verifyAllActionInteractions(Arrays.asList(expectedVisitCodes));
    }

    private void verifyAllActionInteractions(List<String> expectedVisitCodes) {
        if (expectedVisitCodes.isEmpty()) {
            verifyZeroInteractions(actionService);
        }
        for (String visitCode : expectedVisitCodes) {
            verify(actionService).markAlertAsClosedForVisitForMother("Case X", "ANM 1", visitCode);
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
