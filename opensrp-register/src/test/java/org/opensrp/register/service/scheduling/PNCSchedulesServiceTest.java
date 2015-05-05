package org.opensrp.register.service.scheduling;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.register.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_AUTO_CLOSE_PNC;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.testing.utils.BaseUnitTest;
import org.opensrp.register.service.scheduling.PNCSchedulesService;
import org.opensrp.scheduler.HealthSchedulerService;

public class PNCSchedulesServiceTest extends BaseUnitTest {
    @Mock
    private HealthSchedulerService scheduler;

    private PNCSchedulesService schedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        schedulesService = new PNCSchedulesService(scheduler);
    }

    @Test
    public void shouldEnrollMotherIntoSchedulesWhileDeliveryOutcome() {
        schedulesService.deliveryOutcome("mother id 1", "2012-01-01");

        verify(scheduler).enrollIntoSchedule("mother id 1", SCHEDULE_AUTO_CLOSE_PNC, "2012-01-01");
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void shouldUnenrollAMotherFromAllOpenSchedulesAndRaiseDeleteAllAlertActionDuringClose() {
        EnrollmentRecord record1 = new EnrollmentRecord("Case X", "Schedule 1", null, null, null, null, null, null, null, null);
        EnrollmentRecord record2 = new EnrollmentRecord("Case X", "Schedule 2", null, null, null, null, null, null, null, null);
        List<EnrollmentRecord> records = Arrays.asList(record1, record2);

        when(scheduler.findActiveEnrollments("Case X")).thenReturn(records);

        schedulesService.unEnrollFromSchedules("Case X");
    }

    /*private EnrollmentsQuery queryFor(final String externalId) {
        return argThat(new ArgumentMatcher<EnrollmentsQuery>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentsQuery expectedQuery = new EnrollmentsQuery().havingExternalId(externalId).havingState(ACTIVE);
                return EqualsBuilder.reflectionEquals(expectedQuery.getCriteria(), ((EnrollmentsQuery) o).getCriteria());
            }
        });
    }*/
}
