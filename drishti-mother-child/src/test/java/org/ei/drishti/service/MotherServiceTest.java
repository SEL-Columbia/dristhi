package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.MotherRegistrationInformation;
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

public class MotherServiceTest {
    @Mock
    private AllMothers mothers;
    @Mock
    private ScheduleTrackingService scheduleTrackingService;

    private MotherService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new MotherService(mothers, scheduleTrackingService);
    }

    @Test
    public void shouldSaveAMothersInformationDuringEnrollment() {
        String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        MotherRegistrationInformation motherInfo = new MotherRegistrationInformation("CASE-1", thaayiCardNumber, motherName);

        service.enroll(motherInfo);

        verify(mothers).register(new Mother("CASE-1", thaayiCardNumber, motherName));
    }

    @Test
    public void shouldEnrollAMotherIntoDefaultScheduleDuringEnrollment() {
        final String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        MotherRegistrationInformation motherInfo = new MotherRegistrationInformation("CASE-1", thaayiCardNumber, motherName);

        service.enroll(motherInfo);

        verify(scheduleTrackingService).enroll(enrollmentFor("CASE-1"));
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
