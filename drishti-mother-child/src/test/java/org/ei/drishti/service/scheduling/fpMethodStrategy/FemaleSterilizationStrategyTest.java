package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.ei.drishti.service.scheduling.ScheduleService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FemaleSterilizationStrategyTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private ActionService actionService;
    @Mock
    private ScheduleService scheduleService;

    private FemaleSterilizationStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new FemaleSterilizationStrategy(scheduleTrackingService, actionService, scheduleService);
    }

    @Test
    public void shouldEnrollInFemaleSterilizationFollowupScheduleOnECRegistration() throws Exception {
        strategy.registerEC(new FPProductInformation("entity id 1", null, null, null, null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduleService).enroll("entity id 1", "Female sterilization Followup", "2012-02-01");
    }

    @Test
    public void shouldEnrollInFemaleSterilizationFollowupScheduleOnFPChange() throws Exception {
        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", null, "Female sterilization Followup", "condom",
                null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduleService).enroll("entity id 1", "Female sterilization Followup", "2012-02-01");
    }

    @Test
    public void shouldUnEnrollFromFemaleSterilizationFollowupScheduleOnFPMethodChange() throws Exception {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(
                new FPProductInformation("entity id 1", "anm x", "condom", "female_sterilization", null, null, null, "20", "2012-03-01", "2012-03-10", null, null, null));

        verify(scheduleTrackingService).unenroll("entity id 1", asList("Female sterilization Followup"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm x", "Female sterilization Followup", "2012-03-10");
    }

    @Test
    public void shouldFastForwardFemaleSterilizationFollowupScheduleOnFPFollowup() throws Exception {
        when(scheduleTrackingService.getEnrollment("entity id 1", "Female sterilization Followup")).thenReturn(new EnrollmentRecord(
                "entity id 1", "Female sterilization Followup", "Female sterilization Followup 2", null, null, null, null, null, null, null
        ));

        strategy.fpFollowup(new FPProductInformation("entity id 1", "anm x", "female_sterilization", null,
                null, null, null, null, "2012-03-01", null, "2012-02-01", null, null));

        InOrder inOrder = inOrder(scheduleTrackingService, actionService);
        inOrder.verify(scheduleTrackingService).getEnrollment("entity id 1", "Female sterilization Followup");
        inOrder.verify(scheduleTrackingService).fulfillCurrentMilestone("entity id 1", "Female sterilization Followup", LocalDate.parse("2012-02-01"));
        inOrder.verify(actionService).markAlertAsClosed("entity id 1", "anm x", "Female sterilization Followup 2", "2012-02-01");
    }
}
