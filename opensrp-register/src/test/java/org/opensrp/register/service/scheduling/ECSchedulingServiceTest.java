package org.opensrp.register.service.scheduling;

import static org.joda.time.LocalDate.parse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.register.domain.FPProductInformation;
import org.opensrp.register.service.scheduling.ECSchedulingService;
import org.opensrp.register.service.scheduling.fpMethodStrategy.FPMethodStrategy;
import org.opensrp.register.service.scheduling.fpMethodStrategy.FPMethodStrategyFactory;
import org.opensrp.scheduler.HealthSchedulerService;

public class ECSchedulingServiceTest {
    @Mock
    private FPMethodStrategyFactory fpMethodStrategyFactory;
    @Mock
    private HealthSchedulerService scheduler;
    @Mock
    private FPMethodStrategy fpMethodStrategy;

    private ECSchedulingService ecSchedulingService;

    public ECSchedulingServiceTest() {
        initMocks(this);
        ecSchedulingService = new ECSchedulingService(fpMethodStrategyFactory, scheduler);
    }

    @Test
    public void shouldFulfillFollowupScheduleWhenComplicationIsReportedAndECDoesNotNeedFollowup() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Followup")).thenReturn(false);

        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, "", null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "no", null));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Followup");
        verify(scheduler, times(3)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldNotFulfillFollowupScheduleWhenComplicationIsReportedAndECDoesNotEnrolledIntoSchedule() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Followup")).thenReturn(true);

        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, "", null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "no", null));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Followup");
        verify(scheduler, times(0)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToFollowupScheduleWhenComplicationIsReportedAndTheyNeedFollowup() {
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-13", null, "2011-01-12", "yes", null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "FP Followup", "2011-01-12");
    }

    @Test
    public void shouldFulfillReferralFollowupScheduleWhenComplicationIsReportedAndECDoesNotNeedReferralFollowup() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Referral Followup")).thenReturn(false);

        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, ""));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, "no"));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Referral Followup");
        verify(scheduler, times(3)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Referral Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldNotFulfillReferralFollowupScheduleWhenComplicationIsReportedAndECIsNotEnrolledToSchedule() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Referral Followup")).thenReturn(true);

        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, ""));
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, "no"));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Referral Followup");
        verify(scheduler, times(0)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Referral Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToReferralFollowupScheduleWhenComplicationIsReportedAndTheyNeedReferralFollowup() {
        ecSchedulingService.reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-13", null, "2011-01-12", null, "yes"));

        verify(scheduler).enrollIntoSchedule("entity id 1", "FP Referral Followup", "2011-01-12");
    }

    @Test
    public void shouldFulfillFollowupScheduleWhenReferralFollowupIsReportedAndECDoesNotNeedFollowup() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Followup")).thenReturn(false);

        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, "", null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "no", null));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Followup");
        verify(scheduler, times(3)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldNotFulfillFollowupScheduleWhenReferralFollowupIsReportedAndECIsNotEnrolledToSchedule() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Followup")).thenReturn(true);

        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, "", null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "no", null));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Followup");
        verify(scheduler, times(0)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToFollowupScheduleWhenReferralFollowupIsReportedAndTheyNeedFollowup() {
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-13", null, "2011-01-12", "yes", null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "FP Followup", "2011-01-12");
    }

    @Test
    public void shouldFulfillReferralFollowupScheduleWhenReferralFollowupIsReportedAndECDoesNotNeedReferralFollowup() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Referral Followup")).thenReturn(false);

        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, ""));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, "no"));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Referral Followup");
        verify(scheduler, times(3)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Referral Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldNotFulfillReferralFollowupScheduleWhenReferralFollowupIsReportedAndECIsNotEnrolledIntoScheduled() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Referral Followup")).thenReturn(true);

        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-12", null, null, null, ""));
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, "no"));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Referral Followup");
        verify(scheduler, times(0)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Referral Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToReferralFollowupScheduleWhenReferralFollowupIsReportedAndTheyNeedReferralFollowup() {
        ecSchedulingService.reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2011-01-13", null, "2011-01-12", null, "yes"));

        verify(scheduler).enrollIntoSchedule("entity id 1", "FP Referral Followup", "2011-01-12");
    }

    @Test
    public void shouldFulfillFollowupScheduleWhenFollowupIsReportedAndECDoesNotNeedFollowup() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Followup")).thenReturn(false);
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "", null));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "no", null));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Followup");
        verify(scheduler, times(3)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldNotFulfillFollowupScheduleWhenFollowupIsReportedAndECIsNotEnrolledIntoSchedule() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Followup")).thenReturn(true);
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "", null));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, "no", null));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Followup");
        verify(scheduler, times(0)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToFollowupScheduleWhenFollowupIsReportedAndTheyNeedFollowup() {
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-13", null, "2011-01-12", "yes", null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "FP Followup", "2011-01-12");
    }

    @Test
    public void shouldFulfillReferralFollowupScheduleWhenFollowupIsReportedAndECDoesNotNeedReferralFollowup() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Referral Followup")).thenReturn(false);
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, ""));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, "no"));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Referral Followup");
        verify(scheduler, times(3)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Referral Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldNotFulfillReferralFollowupScheduleWhenFollowupIsReportedAndECIsNotEnrolledIntoSchedule() {
        when(scheduler.isNotEnrolled("entity id 1", "FP Referral Followup")).thenReturn(true);
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, null));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, ""));
        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-12", null, null, null, "no"));

        verify(scheduler, times(3)).isNotEnrolled("entity id 1", "FP Referral Followup");
        verify(scheduler, times(0)).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "FP Referral Followup", parse("2011-01-12"));
    }

    @Test
    public void shouldEnrollECToReferralFollowupScheduleWhenFollowupIsReportedAndTheyNeedReferralFollowup() {
        when(fpMethodStrategyFactory.getStrategyFor("fp_method")).thenReturn(fpMethodStrategy);

        ecSchedulingService.fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp_method", null, null, null, null, null, "2011-01-13", null, "2011-01-12", null, "yes"));

        verify(scheduler).enrollIntoSchedule("entity id 1", "FP Referral Followup", "2011-01-12");
    }
}
