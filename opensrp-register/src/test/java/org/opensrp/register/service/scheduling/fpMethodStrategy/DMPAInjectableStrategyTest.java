package org.opensrp.register.service.scheduling.fpMethodStrategy;

import static org.joda.time.LocalDate.parse;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.opensrp.register.domain.FPProductInformation;
import org.opensrp.register.service.scheduling.fpMethodStrategy.DMPAInjectableStrategy;
import org.opensrp.scheduler.HealthSchedulerService;

public class DMPAInjectableStrategyTest {
	@Mock
    private HealthSchedulerService scheduler;

    private DMPAInjectableStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new DMPAInjectableStrategy(scheduler);
    }

    @Test
    public void shouldEnrollInDMPAInjectableScheduleOnECRegistration() throws Exception {
        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, "2012-01-01", null, null
                , "20", "2012-03-01", null, null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "DMPA Injectable Refill", "2012-01-01");
    }

    @Test
    public void shouldUnEnrollECFromPreviousRefillSchedule() {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(new FPProductInformation("entity id 1", "anm id 1", "condom", "dmpa_injectable", null, null, null, null, null, "2012-01-01", null, null, null));

        verify(scheduler).unEnrollAndCloseSchedule("entity id 1", "anm id 1", "DMPA Injectable Refill", LocalDate.parse("2012-01-01"));
    }

    @Test
    public void shouldEnrollECIntoDMPAInjectableRefillScheduleWhenFPMethodIsChanged() {
        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", "condom", null, null, null, null, null, "2012-01-01", null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "DMPA Injectable Refill", "2012-01-01");
    }

    @Test
    public void shouldUpdateDMPAInjectableRefillScheduleWhenDMPAIsReinjected() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, "2012-01-01", null, null, null, "2011-01-12", "", null, null, null));

        InOrder inOrder = inOrder(scheduler);
        inOrder.verify(scheduler).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "DMPA Injectable Refill", parse("2011-01-12"));
        inOrder.verify(scheduler).enrollIntoSchedule("entity id 1", "DMPA Injectable Refill", "2012-01-01");
    }

    @Test
    public void shouldDoNothingWhenDMPANotInjected() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, "", null, null, null, "2011-01-12", "", null, null, null));

        verifyZeroInteractions(scheduler);

        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, null, null, null, null, "2011-01-12", "", null, null, null));

        verifyZeroInteractions(scheduler);
    }
}
