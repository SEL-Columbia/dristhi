package org.ei.drishti.service;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static org.ei.drishti.service.ANCSchedulesService.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.powermock.api.mockito.PowerMockito.when;

public class ANCSchedulesServiceTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    private ANCSchedulesService schedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        schedulesService = new ANCSchedulesService(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollMotherIntoANCSchedulesBasedOnLMP() {
        LocalDate lmp = DateUtil.today().minusDays(3);

        schedulesService.enrollMother("Case X", lmp, new Time(14, 0));

        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", SCHEDULE_ANC, lmp));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", SCHEDULE_EDD, lmp));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", SCHEDULE_IFA, lmp));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", SCHEDULE_LAB, lmp));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", SCHEDULE_TT, lmp));
        verifyNoMoreInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldFulfillANCScheduleWhenTheExpectedANCVisitHappens() {
        EnrollmentRecord nextExpectedMilestone = ancEnrollmentRecord("ANC 1");
        int visitNumberToTryAndFulfill = 1;

        LocalDate visitDate = DateUtil.today().minusDays(3);
        when(scheduleTrackingService.getEnrollment("Case X", SCHEDULE_ANC)).thenReturn(nextExpectedMilestone);

        schedulesService.ancVisitHasHappened("Case X", visitNumberToTryAndFulfill, visitDate);

        verify(scheduleTrackingService).fulfillCurrentMilestone("Case X", SCHEDULE_ANC, visitDate);
    }

    @Test
    public void shouldNotFulfillANCMilestoneWhichHasAlreadyBeenFulfilled() {
        EnrollmentRecord nextExpectedMilestone = ancEnrollmentRecord("ANC 3");
        int visitNumberToTryAndFulfill = 1;

        when(scheduleTrackingService.getEnrollment("Case X", SCHEDULE_ANC)).thenReturn(nextExpectedMilestone);

        schedulesService.ancVisitHasHappened("Case X", visitNumberToTryAndFulfill, DateUtil.today().minusDays(3));

        verify(scheduleTrackingService).getEnrollment("Case X", SCHEDULE_ANC);
        verifyNoMoreInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldFulfillAllMilestonesBetweenTheCurrentOneAndTheOneCorrespondingToTheVisitNumber() {
        EnrollmentRecord nextExpectedMilestone = ancEnrollmentRecord("ANC 1");
        int visitNumberToTryAndFulfill = 3;

        when(scheduleTrackingService.getEnrollment("Case X", SCHEDULE_ANC)).thenReturn(nextExpectedMilestone);

        LocalDate visitDate = DateUtil.today().minusDays(3);
        schedulesService.ancVisitHasHappened("Case X", visitNumberToTryAndFulfill, visitDate);

        verify(scheduleTrackingService).getEnrollment("Case X", SCHEDULE_ANC);
        verify(scheduleTrackingService, times(3)).fulfillCurrentMilestone("Case X", SCHEDULE_ANC, visitDate);
        verifyNoMoreInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldUnEnrollAMotherFromAllOpenSchedulesDuringClose() {
        EnrollmentRecord record1 = new EnrollmentRecord("Case X", "Schedule 1", null, null, null, null, null, null, null, null);
        EnrollmentRecord record2 = new EnrollmentRecord("Case X", "Schedule 2", null, null, null, null, null, null, null, null);
        List<EnrollmentRecord> records = Arrays.asList(record1, record2);

        when(scheduleTrackingService.search(queryFor("Case X"))).thenReturn(records);

        schedulesService.closeCase("Case X");

        verify(scheduleTrackingService).unenroll("Case X", Arrays.asList("Schedule 1"));
        verify(scheduleTrackingService).unenroll("Case X", Arrays.asList("Schedule 2"));
    }

    private EnrollmentRequest enrollmentFor(final String caseId, final String scheduleName, final LocalDate lmp) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return request.getExternalId().equals(caseId) && request.getReferenceDate().equals(lmp)
                    && request.getScheduleName().equals(scheduleName);
            }
        });
    }

    private EnrollmentsQuery queryFor(final String externalId) {
        return argThat(new ArgumentMatcher<EnrollmentsQuery>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentsQuery expectedQuery = new EnrollmentsQuery().havingExternalId(externalId).havingState(ACTIVE.toString());
                return EqualsBuilder.reflectionEquals(expectedQuery.getCriteria(), ((EnrollmentsQuery) o).getCriteria());
            }
        });
    }

    private EnrollmentRecord ancEnrollmentRecord(String currentMilestone) {
        return new EnrollmentRecord("Case X", SCHEDULE_ANC, currentMilestone, null, null, null, null, null, null, null);
    }

}
