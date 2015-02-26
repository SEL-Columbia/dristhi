package org.opensrp.service.scheduling.fpMethodStrategy;

import org.opensrp.domain.FPProductInformation;
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

import org.opensrp.service.ActionService;
import org.opensrp.service.scheduling.ScheduleService;
import org.opensrp.service.scheduling.fpMethodStrategy.IUDStrategy;

public class IUDStrategyTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private ActionService actionService;
    @Mock
    private ScheduleService scheduleService;

    private IUDStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new IUDStrategy(scheduleTrackingService, actionService, scheduleService);
    }

    @Test
    public void shouldEnrollInIUDFollowupScheduleOnECRegistration() throws Exception {
        strategy.registerEC(new FPProductInformation("entity id 1", null, null, null, null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduleService).enroll("entity id 1", "IUD Followup", "2012-02-01");
    }

    @Test
    public void shouldEnrollInIUDFollowupScheduleOnFPChange() throws Exception {
        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", null, "IUD Followup", "condom",
                null, null, null, null, "2012-03-01", "2012-02-01", null, null, null));

        verify(scheduleService).enroll("entity id 1", "IUD Followup", "2012-02-01");
    }

    @Test
    public void shouldUnEnrollFromIUDFollowupScheduleOnFPMethodChange() throws Exception {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(
                new FPProductInformation("entity id 1", "anm x", "condom", "iud", null, null, null, "20", "2012-03-01", "2012-03-10", null, null, null));

        verify(scheduleTrackingService).unenroll("entity id 1", asList("IUD Followup"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm x", "IUD Followup", "2012-03-10");
    }

    @Test
    public void shouldFastForwardIUDFollowupScheduleOnFPFollowup() throws Exception {
        when(scheduleTrackingService.getEnrollment("entity id 1", "IUD Followup")).thenReturn(new EnrollmentRecord(
                "entity id 1", "IUD Followup", "IUD Followup 2", null, null, null, null, null, null, null
        ));

        strategy.fpFollowup(new FPProductInformation("entity id 1", "anm x", "iud", null,
                null, null, null, null, "2012-03-01", null, "2012-02-01", null, null));

        InOrder inOrder = inOrder(scheduleTrackingService, actionService);
        inOrder.verify(scheduleTrackingService).getEnrollment("entity id 1", "IUD Followup");
        inOrder.verify(scheduleTrackingService).fulfillCurrentMilestone("entity id 1", "IUD Followup", LocalDate.parse("2012-02-01"));
        inOrder.verify(actionService).markAlertAsClosed("entity id 1", "anm x", "IUD Followup 2", "2012-02-01");
    }
}
