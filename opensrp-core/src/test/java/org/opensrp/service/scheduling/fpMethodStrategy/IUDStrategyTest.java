package org.opensrp.service.scheduling.fpMethodStrategy;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.opensrp.domain.FPProductInformation;
import org.opensrp.scheduler.HealthSchedulerService;

public class IUDStrategyTest {
	@Mock
    private HealthSchedulerService scheduler;

    private IUDStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new IUDStrategy(scheduler);
    }

    @Test
    public void shouldEnrollInIUDFollowupScheduleOnECRegistration() throws Exception {
        strategy.registerEC(new FPProductInformation("entity id 1", null, null, null, null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "IUD Followup", "2012-02-01");
    }

    @Test
    public void shouldEnrollInIUDFollowupScheduleOnFPChange() throws Exception {
        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", null, "IUD Followup", "condom",
                null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "IUD Followup", "2012-02-01");
    }

    @Test
    public void shouldUnEnrollFromIUDFollowupScheduleOnFPMethodChange() throws Exception {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(
                new FPProductInformation("entity id 1", "anm x", "condom", "iud", null, null, null, "20", "2012-03-01", "2012-03-10", null, null, null));

        verify(scheduler).unEnrollAndCloseSchedule("entity id 1", "anm x", "IUD Followup", LocalDate.parse("2012-03-10"));
    }

    @Test
    public void shouldFastForwardIUDFollowupScheduleOnFPFollowup() throws Exception {
        when(scheduler.getEnrollment("entity id 1", "IUD Followup")).thenReturn(new EnrollmentRecord(
                "entity id 1", "IUD Followup", "IUD Followup 2", null, null, null, null, null, null, null
        ));

        strategy.fpFollowup(new FPProductInformation("entity id 1", "anm x", "iud", null,
                null, null, null, null, "2012-03-01", null, "2012-02-01", null, null));

        InOrder inOrder = inOrder(scheduler);
        inOrder.verify(scheduler).getEnrollment("entity id 1", "IUD Followup");
        inOrder.verify(scheduler).fullfillMilestoneAndCloseAlert("entity id 1", "anm x", "IUD Followup", "IUD Followup 2", LocalDate.parse("2012-02-01"));
    }
}
