package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;

import static org.ei.drishti.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_AUTO_CLOSE_PNC;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCSchedulesServiceTest extends BaseUnitTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private ActionService actionService;
    private PNCSchedulesService schedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        schedulesService = new PNCSchedulesService(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollMotherIntoSchedules() {
        AnteNatalCareOutcomeInformation outcomeInformation = new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0");

        schedulesService.enrollMother(outcomeInformation);

        verify(scheduleTrackingService).enroll(enrollmentFor("MOTHER-CASE-1", SCHEDULE_AUTO_CLOSE_PNC, parse("2012-01-01")));
        verifyNoMoreInteractions(scheduleTrackingService);
    }

    private EnrollmentRequest enrollmentFor(final String caseId, final String scheduleName, final LocalDate referenceDate) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return caseId.equals(request.getExternalId()) && referenceDate.equals(request.getReferenceDate())
                        && scheduleName.equals(request.getScheduleName());
            }
        });
    }
}
