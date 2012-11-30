package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECSchedulingServiceTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;

    private ECSchedulingService ecSchedulingService;

    public ECSchedulingServiceTest() {
        initMocks(this);
        ecSchedulingService = new ECSchedulingService(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollHighPriorityECIntoFPComplicationsSchedulesIfFPMethodIsNone() {
        EligibleCoupleRegistrationRequest request = new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "none", "Yes");

        ecSchedulingService.enrollToFPComplications(request);

        verify(scheduleTrackingService).enroll(enrollmentFor(request.caseId(),EC_SCHEDULE_FP_COMPLICATION,LocalDate.now()));
    }

    @Test
    public void shouldEnrollHighPriorityECIntoFPComplicationsSchedulesIfFPMethodIsEmpty() {
        EligibleCoupleRegistrationRequest request = new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "", "Yes");

        ecSchedulingService.enrollToFPComplications(request);

        verify(scheduleTrackingService).enroll(enrollmentFor(request.caseId(),EC_SCHEDULE_FP_COMPLICATION,LocalDate.now()));
    }

    @Test
    public void shouldNotEnrollHighPriorityECIntoFPComplicationsSchedulesIfFPMethodIsNeitherNoneNorEmpty() {
        EligibleCoupleRegistrationRequest request = new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "some method", "Yes");

        ecSchedulingService.enrollToFPComplications(request);

        verifyZeroInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldNotEnrollNormalPriorityECIntoFPComplicationsSchedules() {
        EligibleCoupleRegistrationRequest request = new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "some method", "no");

        ecSchedulingService.enrollToFPComplications(request);

        verifyZeroInteractions(scheduleTrackingService);
    }

    private EnrollmentRequest enrollmentFor(final String caseId, final String scheduleName, final LocalDate lmp) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return caseId.equals(request.getExternalId()) && lmp.equals(request.getReferenceDate())
                        && scheduleName.equals(request.getScheduleName());
            }
        });
    }
}
