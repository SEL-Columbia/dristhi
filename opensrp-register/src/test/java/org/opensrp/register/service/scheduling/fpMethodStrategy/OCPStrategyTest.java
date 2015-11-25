package org.opensrp.register.service.scheduling.fpMethodStrategy;

import static org.joda.time.LocalDate.parse;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.util.DateUtil.fakeIt;
import static org.opensrp.dto.AlertStatus.upcoming;
import static org.opensrp.dto.BeneficiaryType.ec;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.opensrp.register.domain.FPProductInformation;
import org.opensrp.register.service.scheduling.fpMethodStrategy.OCPStrategy;
import org.opensrp.scheduler.HealthSchedulerService;

public class OCPStrategyTest {
    @Mock
    private HealthSchedulerService scheduler;
    
    private OCPStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new OCPStrategy(14, scheduler);
    }

    @Test
    public void shouldEnrollInOCPScheduleAndGenerateUpcomingAlertOnECRegistration() throws Exception {
        fakeIt(parse("2012-02-01"));
        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "1", "2012-01-01"
                , "20", "2012-03-01", null, null, null, null));
        verify(scheduler).alertFor(ec, "entity id 1", "anm id 1", "OCP Refill", "OCP Refill", upcoming, LocalDate.parse("2012-01-15").toDateTime(new LocalTime(14, 0)),
                LocalDate.parse("2012-01-15").plusWeeks(1).toDateTime(new LocalTime(14, 0)));
        verify(scheduler).enrollIntoSchedule("entity id 1", "OCP Refill", "2012-01-15");

        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "2", "2012-01-01"
                , "20", "2012-03-01", null, null, null, null));
        verify(scheduler).alertFor(ec, "entity id 1", "anm id 1", "OCP Refill", "OCP Refill", upcoming, LocalDate.parse("2012-02-12").toDateTime(new LocalTime(14, 0)),
                LocalDate.parse("2012-02-12").plusWeeks(1).toDateTime(new LocalTime(14, 0)));
        verify(scheduler).enrollIntoSchedule("entity id 1", "OCP Refill", "2012-02-12");

        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "0", "2012-01-01"
                , "20", "2012-03-01", null, null, null, null));
        verify(scheduler).alertFor(ec, "entity id 1", "anm id 1", "OCP Refill", "OCP Refill", upcoming, LocalDate.parse("2012-02-01").toDateTime(new LocalTime(14, 0)),
                LocalDate.parse("2012-02-01").plusWeeks(1).toDateTime(new LocalTime(14, 0)));
        verify(scheduler).enrollIntoSchedule("entity id 1", "OCP Refill", "2012-02-01");
    }

    @Test
    public void shouldUnEnrollECFromPreviousRefillSchedule() {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(new FPProductInformation("entity id 1", "anm id 1", "condom", "ocp", null, null, null, null, null, "2012-01-01", null, null, null));

        verify(scheduler).unEnrollAndCloseSchedule("entity id 1", "anm id 1", "OCP Refill", parse("2012-01-01"));
    }

    @Test
    public void shouldEnrollECIntoOCPRefillScheduleWhenFPMethodIsChanged() {
        fakeIt(parse("2012-02-01"));

        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", "anm id 1", "ocp", "condom", null, "1", null, null, null, "2012-01-01", null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "OCP Refill", "2012-01-15");
        verify(scheduler).alertFor(ec, "entity id 1", "anm id 1", "OCP Refill", "OCP Refill", upcoming, LocalDate.parse("2012-01-15").toDateTime(new LocalTime(14, 0)),
                LocalDate.parse("2012-01-15").plusWeeks(1).toDateTime(new LocalTime(14, 0)));
    }

    @Test
    public void shouldUpdateOCPRefillScheduleWhenOCPPillsAreResupplied() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "1", "2012-01-01", null, "2011-01-12", null, null, null, null));

        InOrder inOrder = inOrder(scheduler);
        inOrder.verify(scheduler).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "OCP Refill", parse("2011-01-12"));
        inOrder.verify(scheduler).enrollIntoSchedule("entity id 1", "OCP Refill", "2012-01-15");
        inOrder.verify(scheduler).alertFor(ec, "entity id 1", "anm id 1", "OCP Refill", "OCP Refill", upcoming, LocalDate.parse("2012-01-15").toDateTime(new LocalTime(14, 0)),
                LocalDate.parse("2012-01-15").plusWeeks(1).toDateTime(new LocalTime(14, 0)));
    }

    @Test
    public void shouldDoNothingWhenZeroOCPPillsAreResupplied() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "0", "2012-01-02", null, "2011-01-12", null, null, null, null));

        verifyZeroInteractions(scheduler);
    }
}
