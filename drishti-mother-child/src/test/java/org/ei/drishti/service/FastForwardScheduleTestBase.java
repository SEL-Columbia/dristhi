package org.ei.drishti.service;

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
    private AlertService alertService;

    public FastForwardScheduleTestBase() {
        this.scheduleTrackingService = mock(ScheduleTrackingService.class);
        this.alertService = mock(AlertService.class);
        this.schedulesService = new ANCSchedulesService(scheduleTrackingService, alertService);
    }

    public FastForwardScheduleTestBase forANCSchedule() {
        this.scheduleName = SCHEDULE_ANC;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ancVisitHasHappened(caseId, visitNumber, visitDate);
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
                schedulesService.ifaVisitHasHappened(caseId, visitNumber, visitDate);
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

        LocalDate visitDate = DateUtil.today().minusDays(3);

        serviceCall.make("Case X", visitNumberToTryAndFulfill, visitDate);

        verify(scheduleTrackingService, times(expectedVisitCodes.length)).fulfillCurrentMilestone(eq("Case X"), eq(scheduleName), eq(visitDate), any(Time.class));

        verifyAllAlertActionInteractions(Arrays.asList(expectedVisitCodes));
    }

    private void verifyAllAlertActionInteractions(List<String> expectedVisitCodes) {
        if (expectedVisitCodes.isEmpty()) {
            verifyZeroInteractions(alertService);
        }
        for (String visitCode : expectedVisitCodes) {
            verify(alertService).deleteAlertForVisit("Case X", visitCode);
        }
        verifyNoMoreInteractions(alertService);
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
