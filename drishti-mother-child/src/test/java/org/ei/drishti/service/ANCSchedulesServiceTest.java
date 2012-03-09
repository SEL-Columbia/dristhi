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
    public void shouldFulfillANCScheduleWhenANCVisitHasHappened() {
        LocalDate visitDate = DateUtil.today().minusDays(3);

        schedulesService.ancVisitHasHappened("Case X", visitDate);

        verify(scheduleTrackingService).fulfillCurrentMilestone("Case X", SCHEDULE_ANC, visitDate);
    }

    @Test
    public void shouldUnEnrollAMotherFromAllOpenSchedulesDuringClose() {
        EnrollmentRecord record1 = new EnrollmentRecord("Case X", "Schedule 1", null, null, null, null, null, null, null);
        EnrollmentRecord record2 = new EnrollmentRecord("Case X", "Schedule 2", null, null, null, null, null, null, null);
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

}
