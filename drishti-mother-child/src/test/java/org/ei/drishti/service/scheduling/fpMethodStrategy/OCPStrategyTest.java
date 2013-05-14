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
import static org.ei.drishti.common.util.DateUtil.fakeIt;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class OCPStrategyTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private ActionService actionService;

    private OCPStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new OCPStrategy(scheduleTrackingService, actionService);
    }

    @Test
    public void shouldEnrollInOCPScheduleOnECRegistration() throws Exception {
        fakeIt(parse("2012-02-01"));
        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "1", "2012-01-01"
                , "20", "2012-03-01", null, null, null));
        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-01-15")));

        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "2", "2012-01-01"
                , "20", "2012-03-01", null, null, null));
        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-02-12")));

        strategy.registerEC(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "0", "2012-01-01"
                , "20", "2012-03-01", null, null, null));
        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-02-01")));
    }

    @Test
    public void shouldUnEnrollECFromPreviousRefillSchedule() {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(new FPProductInformation("entity id 1", "anm id 1", "condom", "ocp", null, null, null, null, null, "2012-01-01", null, null));

        verify(scheduleTrackingService).unenroll("entity id 1", asList("OCP Refill"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "OCP Refill", "2012-01-01");
    }

    @Test
    public void shouldEnrollECIntoOCPRefillScheduleWhenFPMethodIsChanged() {
        fakeIt(parse("2012-02-01"));

        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", "anm id 1", "ocp", "condom", null, "1", null, null, null, "2012-01-01", null, null));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-01-15")));
    }

    @Test
    public void shouldUpdateOCPRefillScheduleWhenOCPPillsAreResupplied() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "1", "2012-01-01", null, "2011-01-12", null, null, null));

        InOrder inOrder = inOrder(scheduleTrackingService, actionService);
        inOrder.verify(scheduleTrackingService).fulfillCurrentMilestone("entity id 1", "OCP Refill", parse("2011-01-12"));
        inOrder.verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "OCP Refill", "2012-01-01");
        inOrder.verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-01-15")));
    }

    @Test
    public void shouldDoNothingWhenZeroOCPPillsAreResupplied() {
        strategy.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, null, "0", "2012-01-02", null, "2011-01-12", null, null, null));

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
