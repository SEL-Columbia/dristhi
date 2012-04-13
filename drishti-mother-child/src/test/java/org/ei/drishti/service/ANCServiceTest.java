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
import org.motechproject.model.Time;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.today;
import static org.motechproject.util.DateUtil.tomorrow;

public class ANCServiceTest {
    @Mock
    private AlertService alertService;
    @Mock
    private AllMothers mothers;
    @Mock
    private ANCSchedulesService ancSchedulesService;

    private ANCService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ANCService(mothers, alertService, ancSchedulesService);
    }

    @Test
    public void shouldSaveAMothersInformationDuringEnrollment() {
        LocalDate lmp = today();

        String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", thaayiCardNumber, motherName, "12345", lmp.toDate());

        service.registerANCCase(enrollmentInfo);

        verify(mothers).register(sameFieldsAs(new Mother("CASE-1", thaayiCardNumber, motherName).withAnmPhoneNumber("12345").withLMP(lmp)));
    }

    @Test
    public void shouldEnrollAMotherIntoDefaultScheduleDuringEnrollmentBasedOnLMP() {
        LocalDate lmp = today().minusDays(2);

        final String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", thaayiCardNumber, motherName, "12345", lmp.toDate());

        service.registerANCCase(enrollmentInfo);

        verify(ancSchedulesService).enrollMother(eq("CASE-1"), eq(lmp), any(Time.class), any(Time.class));
    }

    @Test
    public void shouldEnrollAMotherUsingCurrentDateIfLMPDateIsNotFound() {
        final String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", thaayiCardNumber, motherName, "12345", null);

        service.registerANCCase(enrollmentInfo);

        verify(ancSchedulesService).enrollMother(eq("CASE-1"), eq(today()), any(Time.class), any(Time.class));
    }

    @Test
    public void shouldTellANCSchedulesServiceWhenANC1CareHasBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X").withAnc1Date(yesterday()));

        verify(ancSchedulesService).ancVisitHasHappened("CASE-X", 1, yesterday());
    }

    @Test
    public void shouldTellANCSchedulesServiceWhenANC3CareHasBeenProvidedWhenItIsTheOnlyDateFilled() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X").withAnc3Date(yesterday()));

        verify(ancSchedulesService).ancVisitHasHappened("CASE-X", 3, yesterday());
    }

    @Test
    public void shouldTellANCSchedulesServiceWhenMultipleANCVisitDatesHaveBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X").withAnc1Date(yesterday()).withAnc3Date(today()).withAnc4Date(tomorrow()));

        verify(ancSchedulesService).ancVisitHasHappened("CASE-X", 1, yesterday());
        verify(ancSchedulesService).ancVisitHasHappened("CASE-X", 3, today());
        verify(ancSchedulesService).ancVisitHasHappened("CASE-X", 4, tomorrow());
    }

    @Test
    public void shouldTellANCSchedulesServiceThatTT1IsProvidedWhenOnlyTT1DateHasBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X").withTT1Date(yesterday()));

        verify(ancSchedulesService).ttVisitHasHappened("CASE-X", 1, yesterday());
    }

    @Test
    public void shouldTellANCSchedulesServiceThatTT2IsProvidedWhenOnlyTT2DateHasBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X").withTT2Date(yesterday()));

        verify(ancSchedulesService).ttVisitHasHappened("CASE-X", 2, yesterday());
    }

    @Test
    public void shouldTellANCSchedulesServiceThatTT1AndTT2AreProvidedWhenBothTTDatesHaveBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X").withTT1Date(yesterday()).withTT2Date(yesterday()));

        verify(ancSchedulesService).ttVisitHasHappened("CASE-X", 1, yesterday());
        verify(ancSchedulesService).ttVisitHasHappened("CASE-X", 2, yesterday());
    }

    @Test
    public void shouldTellANCSchedulesServiceThatIFA1IsProvidedWhenOnlyIFA1DateHasBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X").withIFA1Date(yesterday()));

        verify(ancSchedulesService).ifaVisitHasHappened("CASE-X", 1, yesterday());
    }

    @Test
    public void shouldTellANCSchedulesServiceThatIFA2IsProvidedWhenOnlyIFA2DateHasBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X").withIFA2Date(yesterday()));

        verify(ancSchedulesService).ifaVisitHasHappened("CASE-X", 2, yesterday());
    }

    @Test
    public void shouldTellANCSchedulesServiceThatIFA1AndIFA2AreProvidedWhenBothIFADatesHaveBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X").withIFA1Date(yesterday()).withIFA2Date(yesterday()));

        verify(ancSchedulesService).ifaVisitHasHappened("CASE-X", 1, yesterday());
        verify(ancSchedulesService).ifaVisitHasHappened("CASE-X", 2, yesterday());
    }

    @Test
    public void shouldNotTryAndFulfillMilestoneWhenANCCareIsProvidedToAMotherWhoIsNotRegisteredInTheSystem() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-UNKNOWN-MOM"));

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenANCCaseIsClosed() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X"));

        verify(ancSchedulesService).closeCase("CASE-X");
    }

    @Test
    public void shouldNotUnEnrollAMotherFromScheduleWhenSheIsNotRegistered() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-UNKNOWN-MOM"));

        verifyZeroInteractions(ancSchedulesService);
    }

    private Mother sameFieldsAs(final Mother mother) {
        return argThat(new ArgumentMatcher<Mother>() {
            @Override
            public boolean matches(Object o) {
                return EqualsBuilder.reflectionEquals(mother, o);
            }
        });
    }

    private LocalDate yesterday() {
        return today().minusDays(1);
    }
}
