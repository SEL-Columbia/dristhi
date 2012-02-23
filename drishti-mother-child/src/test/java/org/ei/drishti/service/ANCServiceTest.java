package org.ei.drishti.service;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
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
        LocalDate lmp = DateUtil.today();

        String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", thaayiCardNumber, motherName, "12345", lmp.toDate());

        service.registerANCCase(enrollmentInfo);

        verify(mothers).register(sameFieldsAs(new Mother("CASE-1", thaayiCardNumber, motherName).withAnmPhoneNumber("12345").withLMP(lmp)));
    }

    @Test
    public void shouldEnrollAMotherIntoDefaultScheduleDuringEnrollmentBasedOnLMP() {
        Date lmp = DateUtil.today().minusDays(2).toDate();

        final String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", thaayiCardNumber, motherName, "12345", lmp);

        service.registerANCCase(enrollmentInfo);

        verify(scheduleTrackingService).enroll(enrollmentFor("CASE-1", lmp));
    }

    @Test
    public void shouldEnrollAMotherUsingCurrentDateIfLMPDateIsNotFound() {
        final String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", thaayiCardNumber, motherName, "12345", null);

        service.registerANCCase(enrollmentInfo);

        verify(scheduleTrackingService).enroll(enrollmentFor("CASE-1", DateUtil.today().toDate()));
    }

    @Test
    public void shouldFulfillMilestoneWhenANCCareHasBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X"));

        verify(scheduleTrackingService).fulfillCurrentMilestone("CASE-X", "Ante Natal Care - Normal");
    }

    @Test
    public void shouldNotTryAndFulfillMilestoneWhenANCCareIsProvidedToAMotherWhoIsNotRegisteredInTheSystem() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-UNKNOWN-MOM"));

        verifyZeroInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenANCCaseIsClosed() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X"));

        verify(scheduleTrackingService).unenroll("CASE-X", "Ante Natal Care - Normal");
    }

    @Test
    public void shouldNotUnEnrollAMotherFromScheduleWhenSheIsNotRegistered() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-UNKNOWN-MOM"));

        verifyZeroInteractions(scheduleTrackingService);
    }

    private EnrollmentRequest enrollmentFor(final String caseId, final Date lmp) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return request.getExternalId().equals(caseId) && request.getReferenceDate().toDate().equals(lmp);
            }
        });
    }

    private Mother sameFieldsAs(final Mother mother) {
        return argThat(new ArgumentMatcher<Mother>() {
            @Override
            public boolean matches(Object o) {
                return EqualsBuilder.reflectionEquals(mother, o);
            }
        });
    }
}
