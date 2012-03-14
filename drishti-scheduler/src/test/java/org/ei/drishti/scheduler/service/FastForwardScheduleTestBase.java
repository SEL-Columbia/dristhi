package org.ei.drishti.scheduler.service;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.*;

public class FastForwardScheduleTestBase {
    private final ScheduleTrackingService scheduleTrackingService;
    private String scheduleName;
    private String expectedNextMilestone;
    private int visitNumberToTryAndFulfill;
    private ANCSchedulesService schedulesService;
    private Action serviceCall;

    public FastForwardScheduleTestBase(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.schedulesService = new ANCSchedulesService(scheduleTrackingService);
    }

    public FastForwardScheduleTestBase forSchedule(String scheduleName) {
        this.scheduleName = scheduleName;
        return this;
    }

    public FastForwardScheduleTestBase forANCSchedule() {
        this.scheduleName = ANCSchedulesService.SCHEDULE_ANC;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ancVisitHasHappened(caseId, visitNumber, visitDate);
            }
        };
        return this;
    }

    public FastForwardScheduleTestBase forTTSchedule() {
        this.scheduleName = ANCSchedulesService.SCHEDULE_TT;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
                schedulesService.ttVisitHasHappened(caseId, visitNumber, visitDate);
            }
        };
        return this;
    }

    public FastForwardScheduleTestBase forIFASchedule() {
        this.scheduleName = ANCSchedulesService.SCHEDULE_IFA;
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

    public void willFulFillTimes(int numberOfTimesFulfillmentIsExpected) {
        EnrollmentRecord nextExpectedMilestone = enrollmentRecord(scheduleName, expectedNextMilestone);

        when(scheduleTrackingService.getEnrollment("Case X", scheduleName)).thenReturn(nextExpectedMilestone);

        LocalDate visitDate = DateUtil.today().minusDays(3);

        serviceCall.make("Case X", visitNumberToTryAndFulfill, visitDate);

        verify(scheduleTrackingService, times(numberOfTimesFulfillmentIsExpected)).fulfillCurrentMilestone("Case X", scheduleName, visitDate);
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
