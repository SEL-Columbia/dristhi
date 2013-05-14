package org.ei.drishti.service.scheduling;

import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.scheduling.fpMethodStrategy.FPMethodStrategy;
import org.ei.drishti.service.scheduling.fpMethodStrategy.FPMethodStrategyFactory;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECSchedulingServiceTest {
    @Mock
    private FPMethodStrategyFactory fpMethodStrategyFactory;
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private FPMethodStrategy fpMethodStrategy;

    private ECSchedulingService ecSchedulingService;

    public ECSchedulingServiceTest() {
        initMocks(this);
        ecSchedulingService = new ECSchedulingService(fpMethodStrategyFactory, scheduleTrackingService);
    }

    @Test
    public void shouldFulfillFollowupScheduleWhenComplicationIsReportedAndECDoesNotNeedFollowup() {
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, "", null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "no", null));

        verify(scheduleTrackingService, times(3)).fulfillCurrentMilestone("entity id 1", "FP Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToFollowupScheduleWhenComplicationIsReportedAndTheyNeedFollowup() {
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-13", null, "2011-01-12", "yes", null));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "FP Followup", parse("2011-01-12")));
    }

    @Test
    public void shouldFulfillReferralFollowupScheduleWhenComplicationIsReportedAndECDoesNotNeedReferralFollowup() {
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, ""));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, "no"));

        verify(scheduleTrackingService, times(3)).fulfillCurrentMilestone("entity id 1", "FP Referral Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToReferralFollowupScheduleWhenComplicationIsReportedAndTheyNeedReferralFollowup() {
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-13", null, "2011-01-12", null, "yes"));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "FP Referral Followup", parse("2011-01-12")));
    }

    @Test
    public void shouldFulfillFollowupScheduleWhenReferralFollowupIsReportedAndECDoesNotNeedFollowup() {
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, "", null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "no", null));

        verify(scheduleTrackingService, times(3)).fulfillCurrentMilestone("entity id 1", "FP Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToFollowupScheduleWhenReferralFollowupIsReportedAndTheyNeedFollowup() {
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-13", null, "2011-01-12", "yes", null));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "FP Followup", parse("2011-01-12")));
    }

    @Test
    public void shouldFulfillReferralFollowupScheduleWhenReferralFollowupIsReportedAndECDoesNotNeedReferralFollowup() {
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, ""));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, "no"));

        verify(scheduleTrackingService, times(3)).fulfillCurrentMilestone("entity id 1", "FP Referral Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToReferralFollowupScheduleWhenReferralFollowupIsReportedAndTheyNeedReferralFollowup() {
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-13", null, "2011-01-12", null, "yes"));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "FP Referral Followup", parse("2011-01-12")));
    }

    @Test
    public void shouldFulfillFollowupScheduleWhenFollowupIsReportedAndECDoesNotNeedFollowup() {
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "", null));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "no", null));

        verify(scheduleTrackingService, times(3)).fulfillCurrentMilestone("entity id 1", "FP Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToFollowupScheduleWhenFollowupIsReportedAndTheyNeedFollowup() {
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-13", null, "2011-01-12", "yes", null));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "FP Followup", parse("2011-01-12")));
    }

    @Test
    public void shouldFulfillReferralFollowupScheduleWhenFollowupIsReportedAndECDoesNotNeedReferralFollowup() {
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, ""));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, "no"));

        verify(scheduleTrackingService, times(3)).fulfillCurrentMilestone("entity id 1", "FP Referral Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToReferralFollowupScheduleWhenFollowupIsReportedAndTheyNeedReferralFollowup() {
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-13", null, "2011-01-12", null, "yes"));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "FP Referral Followup", parse("2011-01-12")));
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
