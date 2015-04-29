package org.opensrp.service;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.opensrp.common.util.DateUtil.today;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_ANC;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_IFA_1;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_IFA_2;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_IFA_3;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_TT_1;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_TT_2;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.service.scheduling.ANCSchedulesService;

public class FastForwardScheduleTestBase {
    //private final ScheduleTrackingService scheduleTrackingService;
    //private final ScheduleService scheduleService;
	private HealthSchedulerService sf;
    private String scheduleName;
    private String expectedNextMilestone;
    private int visitNumberToTryAndFulfill;
    private ANCSchedulesService schedulesService;
    private Action serviceCall;
    //private ActionService actionService;
    private String numberOfIFATabletsGiven;

    public FastForwardScheduleTestBase() {
        //this.scheduleTrackingService = mock(ScheduleTrackingService.class);
        //this.actionService = mock(ActionService.class);
       // this.scheduleService = mock(ScheduleService.class);
        this.sf = mock(HealthSchedulerService.class);
        this.schedulesService = new ANCSchedulesService(sf);
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

    private FastForwardScheduleTestBase forIFASchedule(final String scheduleName) {
        this.scheduleName = scheduleName;
        this.serviceCall = new Action() {
            @Override
            public void make(String caseId, int visitNumber, LocalDate visitDate) {
            	if(scheduleName.equalsIgnoreCase("IFA 2") || scheduleName.equalsIgnoreCase("IFA 3")) {
            		when(sf.isNotEnrolled("Case X", "IFA 1")).thenReturn(true);
                    when(sf.getEnrollment("Case X", "IFA 1")).thenReturn(null);
                }
            	if(scheduleName.equalsIgnoreCase("IFA 3")) {
            		when(sf.isNotEnrolled("Case X", "IFA 2")).thenReturn(true);
                    when(sf.getEnrollment("Case X", "IFA 2")).thenReturn(null);
            	}
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

        when(sf.getEnrollment("Case X", scheduleName)).thenReturn(recordForNextMilestone);

        LocalDate visitDate = today();

        serviceCall.make("Case X", visitNumberToTryAndFulfill, visitDate);

       // verify(sf, times(expectedVisitCodes.length)).fullfillMilestoneAndCloseAlert(eq("Case X"), any(String.class), eq(scheduleName), eq(expectedNextMilestone), eq(visitDate));

       verifyAllActionInteractions(Arrays.asList(expectedVisitCodes));
    }

    public void willNotFulfillAnything() {
        willFulfillFor();
    }

    private void verifyAllActionInteractions(List<String> expectedVisitCodes) {
        if (expectedVisitCodes.isEmpty()) {
        	verify(sf, atMost(1)).isNotEnrolled("Case X", scheduleName);
        	verify(sf, atMost(1)).getEnrollment("Case X", scheduleName);
            verifyZeroInteractions(sf);
        }
        for (String visitCode : expectedVisitCodes) {
        	verify(sf, atMost(expectedVisitCodes.size()*2)).isNotEnrolled("Case X", scheduleName);
            verify(sf).fullfillMilestoneAndCloseAlert("Case X", "ANM 1", scheduleName, visitCode, today());
        }
        //verifyNoMoreInteractions(sf);
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
