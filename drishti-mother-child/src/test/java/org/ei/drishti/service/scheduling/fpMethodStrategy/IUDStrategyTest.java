package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.ei.drishti.service.scheduling.ScheduleService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
