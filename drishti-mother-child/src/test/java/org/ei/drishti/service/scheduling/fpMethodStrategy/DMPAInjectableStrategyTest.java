package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.ei.drishti.service.scheduling.ScheduleService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DMPAInjectableStrategyTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private ActionService actionService;
    @Mock
    private ScheduleService scheduleService;

    private DMPAInjectableStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new DMPAInjectableStrategy(scheduleTrackingService, actionService, scheduleService);
    }

    @Test
    public void shouldEnrollInDMPAInjectableScheduleOnECRegistration() throws Exception {
        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, "2012-01-01", null, null
                , "20", "2012-03-01", null, null, null, null));

        verify(scheduleService).enroll("entity id 1", "DMPA Injectable Refill", "2012-01-01");
    }

    @Test
    public void shouldUnEnrollECFromPreviousRefillSchedule() {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(new FPProductInformation("entity id 1", "anm id 1", "condom", "dmpa_injectable", null, null, null, null, null, "2012-01-01", null, null, null));

        verify(scheduleTrackingService).unenroll("entity id 1", asList("DMPA Injectable Refill"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "DMPA Injectable Refill", "2012-01-01");
    }

    @Test
    public void shouldEnrollECIntoDMPAInjectableRefillScheduleWhenFPMethodIsChanged() {
        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", "condom", null, null, null, null, null, "2012-01-01", null, null, null));

        verify(scheduleService).enroll("entity id 1", "DMPA Injectable Refill", "2012-01-01");
    }

    @Test
    public void shouldUpdateDMPAInjectableRefillScheduleWhenDMPAIsReinjected() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, "2012-01-01", null, null, null, "2011-01-12", "", null, null, null));

        InOrder inOrder = inOrder(scheduleTrackingService, actionService, scheduleService);
        inOrder.verify(scheduleTrackingService).fulfillCurrentMilestone("entity id 1", "DMPA Injectable Refill", parse("2011-01-12"));
        inOrder.verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "DMPA Injectable Refill", "2012-01-01");
        inOrder.verify(scheduleService).enroll("entity id 1", "DMPA Injectable Refill", "2012-01-01");
    }

    @Test
    public void shouldDoNothingWhenDMPANotInjected() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, "", null, null, null, "2011-01-12", "", null, null, null));

        verifyZeroInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);

        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, null, null, null, null, "2011-01-12", "", null, null, null));

        verifyZeroInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);
    }
}
