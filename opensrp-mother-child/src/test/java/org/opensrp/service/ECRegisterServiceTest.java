package org.opensrp.service;

import org.opensrp.domain.EligibleCouple;
import org.opensrp.domain.Mother;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.domain.register.ECRegister;
import org.opensrp.domain.register.ECRegisterEntry;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllMothers;
import org.opensrp.service.ECRegisterService;

public class ECRegisterServiceTest {

    private ECRegisterService registerService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        registerService = new ECRegisterService(allMothers, allEligibleCouples);
    }

    @Test
    public void shouldGetECRegisterForAGivenANM() throws Exception {
        Mother mother = new Mother("caseId", "ecCaseId", "thayi card number 1")
                .withLMP(LocalDate.parse("2013-12-30"))
                .withDetails(
                        create("ancNumber", "OA899")
                                .put("registrationDate", "2014-01-01")
                                .put("isJSYBeneficiary", "yes")
                                .put("edd", "2014-09-01")
                                .put("height", "150")
                                .put("bloodGroup", "o -ve")
                                .put("isHighRisk", "yes")
                                .map()
                );
        EligibleCouple eligibleCouple = new EligibleCouple("ecCaseId", "123")
                .withANMIdentifier("anm1")
                .withLocation("village", "sc", "phc")
                .withCouple("wife Name", "husband Name")
                .withDetails(
                        create("registrationDate", "2013-01-01")
                                .put("wifeName", "wife Name")
                                .put("husbandName", "husband Name")
                                .put("wifeAge", "23")
                                .put("husbandAge", "32")
                                .put("householdNumber", "22")
                                .put("householdAddress", "HH Address")
                                .put("headOfHousehold", "Head of HH")
                                .put("religion", "religion")
                                .put("caste", "caste")
                                .put("economicStatus", "bpl")
                                .put("educationalLevel", "wife education level")
                                .put("husbandEducationLevel", "husband education level")
                                .put("parity", "1")
                                .put("numberOfLivingChildren", "1")
                                .put("numberOfLivingMaleChildren", "1")
                                .put("numberOfLivingFemaleChild", "0")
                                .put("numberOfAbortions", "0")
                                .put("numberOfStillBirths", "0")
                                .put("numberOfPregnancies", "2")
                                .put("youngestChildAge", "1")
                                .put("currentMethod", "ocp")
                                .put("familyPlanningMethodChangeDate", "2012-01-01")
                                .map()
                );
        when(allEligibleCouples.allOpenECsForANM("anm1")).thenReturn(asList(eligibleCouple));
        when(allMothers.findAllOpenANCByECCaseId("ecCaseId")).thenReturn(asList(mother));
        ECRegister expectedRegister = new ECRegister(asList(new ECRegisterEntry()
                .withECNumber("123")
                .withRegistrationDate("2013-01-01")
                .withWifeName("wife Name")
                .withHusbandName("husband Name")
                .withVillage("village")
                .withSubCenter("sc")
                .withPHC("phc")
                .withWifeAge("23")
                .withHusbandAge("32")
                .withHouseholdNumber("22")
                .withHouseholdAddress("HH Address")
                .withHeadOfHousehold("Head of HH")
                .withReligion("religion")
                .withCaste("caste")
                .withEconomicStatus("bpl")
                .withWifeEducationLevel("wife education level")
                .withHusbandEducationLevel("husband education level")
                .withGravida("2")
                .withParity("1")
                .withNumberOfLivingChildren("1")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("0")
                .withNumberOfStillBirths("0")
                .withNumberOfAbortions("0")
                .withYoungestChildAge("1")
                .withCurrentFPMethod("ocp")
                .withCurrentFPMethodStartDate("2012-01-01")
                .withPregnancyStatus(true)
        ));

        ECRegister register = registerService.getRegisterForANM("anm1");

        assertEquals(expectedRegister, register);
    }

    @Test
    public void shouldDefaultIntegerFieldsToZeroWhenValueIsNotInteger() throws Exception {
        Mother mother = new Mother("caseId", "ecCaseId", "thayi card number 1")
                .withLMP(LocalDate.parse("2013-12-30"))
                .withDetails(
                        create("ancNumber", "OA899")
                                .put("registrationDate", "2014-01-01")
                                .put("isJSYBeneficiary", "yes")
                                .put("edd", "2014-09-01")
                                .put("height", "150")
                                .put("bloodGroup", "o -ve")
                                .put("isHighRisk", "yes")
                                .map()
                );
        EligibleCouple eligibleCouple = new EligibleCouple("ecCaseId", "123")
                .withANMIdentifier("anm1")
                .withLocation("village", "sc", "phc")
                .withCouple("wife Name", "husband Name")
                .withDetails(
                        create("registrationDate", "2013-01-01")
                                .put("wifeName", "wife Name")
                                .put("husbandName", "husband Name")
                                .put("wifeAge", "23")
                                .put("husbandAge", "32")
                                .put("householdNumber", "22")
                                .put("householdAddress", "HH Address")
                                .put("headOfHousehold", "Head of HH")
                                .put("religion", "religion")
                                .put("caste", "caste")
                                .put("economicStatus", "bpl")
                                .put("educationalLevel", "wife education level")
                                .put("husbandEducationLevel", "husband education level")
                                .put("parity", "NaN")
                                .put("numberOfLivingChildren", "str")
                                .put("numberOfLivingMaleChildren", null)
                                .put("numberOfLivingFemaleChild", "NaN")
                                .put("numberOfAbortions", "inp")
                                .put("numberOfStillBirths", null)
                                .put("youngestChildAge", "")
                                .put("currentMethod", "ocp")
                                .put("familyPlanningMethodChangeDate", "2012-01-01")
                                .map()
                );
        when(allEligibleCouples.allOpenECsForANM("anm1")).thenReturn(asList(eligibleCouple));
        when(allMothers.findAllOpenANCByECCaseId("ecCaseId")).thenReturn(asList(mother));
        ECRegister expectedRegister = new ECRegister(asList(new ECRegisterEntry()
                .withECNumber("123")
                .withRegistrationDate("2013-01-01")
                .withWifeName("wife Name")
                .withHusbandName("husband Name")
                .withVillage("village")
                .withSubCenter("sc")
                .withPHC("phc")
                .withWifeAge("23")
                .withHusbandAge("32")
                .withHouseholdNumber("22")
                .withHouseholdAddress("HH Address")
                .withHeadOfHousehold("Head of HH")
                .withReligion("religion")
                .withCaste("caste")
                .withEconomicStatus("bpl")
                .withWifeEducationLevel("wife education level")
                .withHusbandEducationLevel("husband education level")
                .withGravida("")
                .withParity("")
                .withNumberOfLivingChildren("")
                .withNumberOfLivingMaleChildren("")
                .withNumberOfLivingFemaleChildren("")
                .withNumberOfStillBirths("")
                .withNumberOfAbortions("")
                .withYoungestChildAge("")
                .withCurrentFPMethod("ocp")
                .withCurrentFPMethodStartDate("2012-01-01")
                .withPregnancyStatus(true)
        ));

        ECRegister register = registerService.getRegisterForANM("anm1");

        assertEquals(expectedRegister, register);
    }
}
