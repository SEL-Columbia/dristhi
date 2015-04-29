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

public class MaleSterilizationStrategyTest {
	@Mock
    private HealthSchedulerService scheduler;

    private MaleSterilizationStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new MaleSterilizationStrategy(scheduler);
    }

    @Test
    public void shouldEnrollInMaleSterilizationFollowupScheduleOnECRegistration() throws Exception {
        strategy.registerEC(new FPProductInformation("entity id 1", null, null, null, null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "Male sterilization Followup", "2012-02-01");
    }

    @Test
    public void shouldEnrollInMaleSterilizationFollowupScheduleOnFPChange() throws Exception {
        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", null, "Male sterilization Followup", "condom",
                null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "Male sterilization Followup", "2012-02-01");
    }

    @Test
    public void shouldUnEnrollFromMaleSterilizationFollowupScheduleOnFPMethodChange() throws Exception {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(
                new FPProductInformation("entity id 1", "anm x", "condom", "male_sterilization", null, null, null, "20", "2012-03-01", "2012-03-10", null, null, null));

        verify(scheduler).unEnrollAndCloseSchedule("entity id 1", "anm x", "Male sterilization Followup", LocalDate.parse("2012-03-10"));
    }

    @Test
    public void shouldFastForwardMaleSterilizationFollowupScheduleOnFPFollowup() throws Exception {
        when(scheduler.getEnrollment("entity id 1", "Male sterilization Followup")).thenReturn(new EnrollmentRecord(
                "entity id 1", "Male sterilization Followup", "Male sterilization Followup 2", null, null, null, null, null, null, null
        ));

        strategy.fpFollowup(new FPProductInformation("entity id 1", "anm x", "male_sterilization", null,
                null, null, null, null, "2012-03-01", null, "2012-02-01", null, null));

        InOrder inOrder = inOrder(scheduler);
        inOrder.verify(scheduler).getEnrollment("entity id 1", "Male sterilization Followup");
        inOrder.verify(scheduler).fullfillMilestoneAndCloseAlert("entity id 1", "anm x", "Male sterilization Followup", "Male sterilization Followup 2", LocalDate.parse("2012-02-01"));
    }
}
