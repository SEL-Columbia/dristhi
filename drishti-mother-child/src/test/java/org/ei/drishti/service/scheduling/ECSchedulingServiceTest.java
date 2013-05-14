package org.ei.drishti.service.scheduling;

import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.scheduling.fpMethodStrategy.FPMethodStrategyFactory;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECSchedulingServiceTest {
    @Mock
    private FPMethodStrategyFactory fpMethodStrategyFactory;
    @Mock
    private ScheduleTrackingService scheduleTrackingService;

    private ECSchedulingService ecSchedulingService;

    public ECSchedulingServiceTest() {
        initMocks(this);
        ecSchedulingService = new ECSchedulingService(fpMethodStrategyFactory, scheduleTrackingService);
    }

    @Test
    public void shouldDoNothingWhenComplicationIsReportedAndECDoesNotNeedFollowup() {
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, null, null, "2011-01-012", "no"));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, null, null, "2011-01-012", null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, null, null, "2011-01-012", ""));

        verifyZeroInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollECToFollowupScheduleWhenComplicationIsReportedAndTheyNeedFollowup() {
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, null, null, "2011-01-12", "yes"));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "FP Followup", parse("2011-01-12")));
    }

    @Test
    public void shouldDoNothingWhenReferralFollowupIsReportedAndECDoesNotNeedFollowup() {
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, null, null, "2011-01-012", "no"));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, null, null, "2011-01-012", null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, null, null, "2011-01-012", ""));

        verifyZeroInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollECToFollowupScheduleWhenReferralFollowupIsReportedAndTheyNeedFollowup() {
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, null, null, "2011-01-12", "yes"));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "FP Followup", parse("2011-01-12")));
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
