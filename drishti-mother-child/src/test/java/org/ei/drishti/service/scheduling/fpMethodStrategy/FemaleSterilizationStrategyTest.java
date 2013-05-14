package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FemaleSterilizationStrategyTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private ActionService actionService;

    private FemaleSterilizationStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new FemaleSterilizationStrategy(scheduleTrackingService, actionService);
    }

    @Test
    public void shouldEnrollInFemaleSterilizationFollowupScheduleOnECRegistration() throws Exception {
        strategy.registerEC(new FPProductInformation("entity id 1", null, null, null, null, null, null, null, "2012-03-01", "2012-02-01", null, null));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "Female sterilization followup", LocalDate.parse("2012-02-01")));
    }

    @Test
    public void shouldEnrollInFemaleSterilizationFollowupScheduleOnFPChange() throws Exception {
        strategy.enrollToNewScheduleForNewFPMethod(new FPProductInformation("entity id 1", null, "Female sterilization followup", "condom",
                null, null, null, null, "2012-03-01", "2012-02-01", null, null));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "Female sterilization followup", LocalDate.parse("2012-02-01")));
    }

    @Test
    public void shouldUnEnrollFromFemaleSterilizationFollowupScheduleOnFPMethodChange() throws Exception {
        strategy.unEnrollFromPreviousScheduleAsFPMethodChanged(
                new FPProductInformation("entity id 1", "anm x", "condom", "female_sterilization", null, null, null, "20", "2012-03-01", "2012-03-10", null, null));

        verify(scheduleTrackingService).unenroll("entity id 1", asList("Female sterilization followup"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm x", "Female sterilization followup", "2012-03-10");
    }

    @Test
    public void shouldFastForwardFemaleSterilizationFollowupScheduleOnFPFollowup() throws Exception {
        when(scheduleTrackingService.getEnrollment("entity id 1", "Female sterilization followup")).thenReturn(new EnrollmentRecord(
                "entity id 1", "Female sterilization followup", "Female sterilization followup 2", null, null, null, null, null, null, null
        ));
        strategy.fpFollowup(new FPProductInformation("entity id 1", "anm x", "female_sterilization", null,
                null, null, null, null, "2012-03-01", null, "2012-02-01", null));

        verify(scheduleTrackingService).fulfillCurrentMilestone("entity id 1", "Female sterilization followup", LocalDate.parse("2012-02-01"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm x", "Female sterilization followup 2", "2012-02-01");
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
