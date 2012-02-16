package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANCServiceTest {
    @Mock
    private AllMothers mothers;
    @Mock
    private ScheduleTrackingService scheduleTrackingService;

    private ANCService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ANCService(mothers, scheduleTrackingService);
    }

    @Test
    public void shouldSaveAMothersInformationDuringEnrollment() {
        String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", thaayiCardNumber, motherName, "12345");

        service.registerANCCase(enrollmentInfo);

        verify(mothers).register(new Mother("CASE-1", thaayiCardNumber, motherName).withAnmPhoneNumber("12345"));
    }

    @Test
    public void shouldEnrollAMotherIntoDefaultScheduleDuringEnrollment() {
        final String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", thaayiCardNumber, motherName, "12345");

        service.registerANCCase(enrollmentInfo);

        verify(scheduleTrackingService).enroll(enrollmentFor("CASE-1"));
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenANCCaseIsClosed() {
        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X"));

        verify(scheduleTrackingService).unenroll("CASE-X", "Ante Natal Care - Normal");
    }

    @Test
    public void shouldFulfillMilestoneWhenANCCareHasBeenProvided() {
        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X"));

        verify(scheduleTrackingService).fulfillCurrentMilestone("CASE-X", "Ante Natal Care - Normal");
    }

    private EnrollmentRequest enrollmentFor(final String caseId) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                return ((EnrollmentRequest) o).getExternalId().equals(caseId);
            }
        });
    }
}
