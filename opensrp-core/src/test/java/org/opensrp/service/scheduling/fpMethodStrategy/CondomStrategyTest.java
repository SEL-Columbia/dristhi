package org.opensrp.service.scheduling.fpMethodStrategy;

import static org.joda.time.LocalDate.parse;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.util.DateUtil.fakeIt;
import static org.opensrp.dto.AlertStatus.upcoming;
import static org.opensrp.dto.BeneficiaryType.ec;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.opensrp.domain.FPProductInformation;
import org.opensrp.scheduler.HealthSchedulerService;

public class CondomStrategyTest {
	@Mock
    private HealthSchedulerService scheduler;

    private CondomStrategy strategy;
    private static final int preferredTime = 14;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new CondomStrategy(scheduler, preferredTime);
    }

    @Test
    public void shouldEnrollInCondomScheduleOnECRegistration() throws Exception {
        fakeIt(parse("2012-01-15"));
        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "condom", null, "2012-01-15", null, null
                , "20", "2012-03-01", null, null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "Condom Refill", parse("2012-02-01"));
        verify(scheduler).alertFor(ec, "entity id 1", "anm id 1", "Condom Refill", "Condom Refill", upcoming, dateTime("2012-02-01"), dateTime("2012-02-08"));

        fakeIt(parse("2012-12-01"));
        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "condom", null, "2012-12-01", null, null
                , "20", "2012-03-01", null, null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "Condom Refill", parse("2013-01-01"));
        verify(scheduler).alertFor(ec, "entity id 1", "anm id 1", "Condom Refill", "Condom Refill", upcoming, dateTime("2013-01-01"), dateTime("2013-01-08"));
    }

    @Test
    public void shouldUnEnrollECFromPreviousRefillSchedule() {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(new FPProductInformation("entity id 1", "anm id 1", "ocp", "condom", null, "1", null, null, null, "2012-01-01", null, null, null));

        verify(scheduler).unEnrollAndCloseSchedule("entity id 1", "anm id 1", "Condom Refill", parse("2012-01-01"));
    }

    @Test
    public void shouldEnrollECIntoCondomRefillScheduleWhenFPMethodIsChanged() {
        fakeIt(parse("2012-01-15"));

        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", "anm id 1", "condom", "ocp", null, null, null, null, null, "2012-01-01", null, null, null));

        verify(scheduler).enrollIntoSchedule("entity id 1", "Condom Refill", parse("2012-02-01"));
        verify(scheduler).alertFor(ec, "entity id 1", "anm id 1", "Condom Refill", "Condom Refill", upcoming, dateTime("2012-02-01"), dateTime("2012-02-08"));
    }

    @Test
    public void shouldUpdateECFromCondomRefillScheduleWhenCondomsAreResupplied() {
        fakeIt(parse("2011-01-15"));

        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "condom", null, null, null, null, "20", "2011-01-12", "", null, null, null));

        InOrder inOrder = inOrder(scheduler);
        inOrder.verify(scheduler).fullfillMilestoneAndCloseAlert("entity id 1", "anm id 1", "Condom Refill", parse("2011-01-12"));
        inOrder.verify(scheduler).enrollIntoSchedule("entity id 1", "Condom Refill", parse("2011-02-01"));
        inOrder.verify(scheduler).alertFor(ec, "entity id 1", "anm id 1", "Condom Refill", "Condom Refill", upcoming, dateTime("2011-02-01"), dateTime("2011-02-08"));
    }

    @Test
    public void shouldDoNothingWhenCondomsAreNotResupplied() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "condom", null, null, null, null, "", "2011-01-012", "", null, null, null));

        verifyZeroInteractions(scheduler);
    }

    private DateTime dateTime(String date) {
        return parse(date).toDateTime(new LocalTime(preferredTime, 0));
    }
}
