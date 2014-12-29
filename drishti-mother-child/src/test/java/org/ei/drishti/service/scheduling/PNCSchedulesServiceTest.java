package org.ei.drishti.service.scheduling;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.ei.drishti.service.ActionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.Arrays;
import java.util.List;

import static org.ei.drishti.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_AUTO_CLOSE_PNC;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.powermock.api.mockito.PowerMockito.when;

public class PNCSchedulesServiceTest extends BaseUnitTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private ActionService actionService;
    @Mock
    private ScheduleService scheduleService;

    private PNCSchedulesService schedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        schedulesService = new PNCSchedulesService(scheduleTrackingService, scheduleService, actionService);
    }

    @Test
    public void shouldEnrollMotherIntoSchedulesWhileDeliveryOutcome() {
        schedulesService.deliveryOutcome("mother id 1", "2012-01-01");

        verify(scheduleService).enroll("mother id 1", SCHEDULE_AUTO_CLOSE_PNC, "2012-01-01");
        verifyNoMoreInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldUnenrollAMotherFromAllOpenSchedulesAndRaiseDeleteAllAlertActionDuringClose() {
        EnrollmentRecord record1 = new EnrollmentRecord("Case X", "Schedule 1", null, null, null, null, null, null, null, null);
        EnrollmentRecord record2 = new EnrollmentRecord("Case X", "Schedule 2", null, null, null, null, null, null, null, null);
        List<EnrollmentRecord> records = Arrays.asList(record1, record2);

        when(scheduleTrackingService.search(queryFor("Case X"))).thenReturn(records);

        schedulesService.unEnrollFromSchedules("Case X");

        verify(scheduleTrackingService).unenroll("Case X", Arrays.asList("Schedule 1"));
        verify(scheduleTrackingService).unenroll("Case X", Arrays.asList("Schedule 2"));
    }

    private EnrollmentsQuery queryFor(final String externalId) {
        return argThat(new ArgumentMatcher<EnrollmentsQuery>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentsQuery expectedQuery = new EnrollmentsQuery().havingExternalId(externalId).havingState(ACTIVE);
                return EqualsBuilder.reflectionEquals(expectedQuery.getCriteria(), ((EnrollmentsQuery) o).getCriteria());
            }
        });
    }
}
