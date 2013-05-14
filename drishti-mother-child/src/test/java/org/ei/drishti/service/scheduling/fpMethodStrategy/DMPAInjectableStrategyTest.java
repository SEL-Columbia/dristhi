package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class DMPAInjectableStrategyTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private ActionService actionService;

    private DMPAInjectableStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new DMPAInjectableStrategy(scheduleTrackingService, actionService);
    }

    @Test
    public void shouldEnrollInDMPAInjectableScheduleOnECRegistration() throws Exception {
        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, "2012-01-01", null, null
                , "20", "2012-03-01", null, null, null));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "DMPA Injectable Refill", parse("2012-01-01")));
    }

    @Test
    public void shouldUnEnrollECFromPreviousRefillSchedule() {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(new FPProductInformation("entity id 1", "anm id 1", "condom", "dmpa_injectable", null, null, null, null, null, "2012-01-01", null, null));

        verify(scheduleTrackingService).unenroll("entity id 1", asList("DMPA Injectable Refill"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "DMPA Injectable Refill", "2012-01-01");
    }

    @Test
    public void shouldEnrollECIntoDMPAInjectableRefillScheduleWhenFPMethodIsChanged() {
        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", "condom", null, null, null, null, null, "2012-01-01", null, null));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "DMPA Injectable Refill", parse("2012-01-01")));
    }

    @Test
    public void shouldUpdateDMPAInjectableRefillScheduleWhenDMPAIsReinjected() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, "2012-01-01", null, null, null, "2011-01-12", "", null, null));

        InOrder inOrder = inOrder(scheduleTrackingService, actionService);
        inOrder.verify(scheduleTrackingService).fulfillCurrentMilestone("entity id 1", "DMPA Injectable Refill", parse("2011-01-12"));
        inOrder.verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "DMPA Injectable Refill", "2012-01-01");
        inOrder.verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "DMPA Injectable Refill", parse("2012-01-01")));
    }

    @Test
    public void shouldDoNothingWhenDMPANotInjected() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, "", null, null, null, "2011-01-12", "", null, null));

        verifyZeroInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);

        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, null, null, null, null, "2011-01-12", "", null, null));

        verifyZeroInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);
    }

    private EnrollmentRequest enrollmentFor(final String caseId, final String scheduleName, final LocalDate referenceDate) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return caseId.equals(request.getExternalId()) && referenceDate.equals(request.getReferenceDate())
                        && scheduleName.equals(request.getScheduleName());
            }
        });
    }
}
