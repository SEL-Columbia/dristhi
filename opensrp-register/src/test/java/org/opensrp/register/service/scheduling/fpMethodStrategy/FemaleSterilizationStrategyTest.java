package org.opensrp.register.service.scheduling.fpMethodStrategy;

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
import org.opensrp.register.domain.FPProductInformation;
import org.opensrp.register.service.scheduling.fpMethodStrategy.FemaleSterilizationStrategy;
import org.opensrp.scheduler.HealthSchedulerService;

public class FemaleSterilizationStrategyTest {
	@Mock
    private HealthSchedulerService scheduler;
	
    private FemaleSterilizationStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new FemaleSterilizationStrategy(scheduler);
    }

    @Test
    public void shouldEnrollInFemaleSterilizationFollowupScheduleOnECRegistration() throws Exception {
        strategy.registerEC(new FPProductInformation("entity id 1", null, null, null, null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "Female sterilization Followup", "2012-02-01");
    }

    @Test
    public void shouldEnrollInFemaleSterilizationFollowupScheduleOnFPChange() throws Exception {
        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", null, "Female sterilization Followup", "condom",
                null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "Female sterilization Followup", "2012-02-01");
    }

    @Test
    public void shouldUnEnrollFromFemaleSterilizationFollowupScheduleOnFPMethodChange() throws Exception {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(
                new FPProductInformation("entity id 1", "anm x", "condom", "female_sterilization", null, null, null, "20", "2012-03-01", "2012-03-10", null, null, null));

        verify(scheduler).unEnrollAndCloseSchedule("entity id 1", "anm x", "Female sterilization Followup", LocalDate.parse("2012-03-10"));
    }

    @Test
    public void shouldFastForwardFemaleSterilizationFollowupScheduleOnFPFollowup() throws Exception {
        when(scheduler.getEnrollment("entity id 1", "Female sterilization Followup")).thenReturn(new EnrollmentRecord(
                "entity id 1", "Female sterilization Followup", "Female sterilization Followup 2", null, null, null, null, null, null, null
        ));

        strategy.fpFollowup(new FPProductInformation("entity id 1", "anm x", "female_sterilization", null,
                null, null, null, null, "2012-03-01", null, "2012-02-01", null, null));

        InOrder inOrder = inOrder(scheduler);
        inOrder.verify(scheduler).getEnrollment("entity id 1", "Female sterilization Followup");
        inOrder.verify(scheduler).fullfillMilestoneAndCloseAlert("entity id 1", "anm x", "Female sterilization Followup", "Female sterilization Followup 2", LocalDate.parse("2012-02-01"));
    }
}
