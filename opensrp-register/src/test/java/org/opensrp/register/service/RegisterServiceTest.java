package org.opensrp.register.service;

import org.opensrp.common.util.DateUtil;
import org.opensrp.register.domain.EligibleCouple;
import org.opensrp.register.domain.Mother;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.register.ANCRegister;
import org.opensrp.register.ANCRegisterEntry;
import org.opensrp.register.ECRegister;
import org.opensrp.register.ECRegisterEntry;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.register.repository.AllMothers;
import org.opensrp.register.service.RegisterService;

public class RegisterServiceTest {

    private RegisterService registerService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        registerService = new RegisterService(allMothers, allEligibleCouples);
    }

    @Test
    public void shouldGetANCRegisterForAGivenANM() throws Exception {
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
                .withDetails(
                        create("aadharNumber", "aadhar card number 1")
                                .put("householdAddress", "address1")
                                .put("phoneNumber", "phone 1")
                                .put("educationalLevel", "wife education level")
                                .put("husbandEducationLevel", "husband education level")
                                .put("caste", "sc")
                                .put("religion", "hindu")
                                .put("economicStatus", "bpl")
                                .put("bplCardNumber", "bpl card number 1")
                                .put("numberOfPregnancies", "1")
                                .put("parity", "2")
                                .put("numberOfLivingChildren", "3")
                                .put("numberOfStillBirths", "4")
                                .put("numberOfAbortions", "5")
                                .put("youngestChildDOB", "2012-01-01")
                                .put("womanDOB", "1989-01-01")
                                .map())
                .withCouple("name1", "name2");
        when(allMothers.findAllOpenMothersForANM("anm1")).thenReturn(asList(mother));
        when(allEligibleCouples.findAll(asList("ecCaseId"))).thenReturn(asList(eligibleCouple));
        ANCRegister expectedRegister = new ANCRegister(asList(new ANCRegisterEntry()
                .withANCNumber("OA899")
                .withRegistrationDate("2014-01-01")
                .withECNumber("123")
                .withThayiCardNumber("thayi card number 1")
                .withAadharCardNumber("aadhar card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withAddress("address1")
                .withWifeDOB("1989-01-01")
                .withPhoneNumber("phone 1")
                .withWifeEducationLevel("wife education level")
                .withHusbandEducationLevel("husband education level")
                .withCaste("sc")
                .withReligion("hindu")
                .withEconomicStatus("bpl")
                .withBPLCardNumber("bpl card number 1")
                .withJSYBeneficiary("yes")
                .withGravida("1")
                .withParity("2")
                .withNumberOfLivingChildren("3")
                .withNumberOfStillBirths("4")
                .withNumberOfAbortions("5")
                .withYoungestChildDOB("2012-01-01")
                .withLMP("2013-12-30")
                .withEDD("2014-09-01")
                .withHeight("150")
                .withBloodGroup("o -ve")
                .withIsHRP("yes")));

        ANCRegister register = registerService.getANCRegister("anm1");

        assertEquals(expectedRegister, register);
    }

    @Test
    public void shouldGetANCRegisterForANCOutOfAreaWithoutDOB() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2014-01-01"));
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
                .withDetails(
                        create("aadharNumber", "aadhar card number 1")
                                .put("householdAddress", "address1")
                                .put("phoneNumber", "phone 1")
                                .put("educationalLevel", "wife education level")
                                .put("husbandEducationLevel", "husband education level")
                                .put("caste", "sc")
                                .put("religion", "hindu")
                                .put("economicStatus", "bpl")
                                .put("bplCardNumber", "bpl card number 1")
                                .put("numberOfPregnancies", "1")
                                .put("parity", "2")
                                .put("numberOfLivingChildren", "3")
                                .put("numberOfStillBirths", "4")
                                .put("numberOfAbortions", "5")
                                .put("youngestChildDOB", "2012-01-01")
                                .put("wifeAge", "26")
                                .map())
                .withCouple("name1", "name2");
        when(allMothers.findAllOpenMothersForANM("anm1")).thenReturn(asList(mother));
        when(allEligibleCouples.findAll(asList("ecCaseId"))).thenReturn(asList(eligibleCouple));
        ANCRegister expectedRegister = new ANCRegister(asList(new ANCRegisterEntry()
                .withANCNumber("OA899")
                .withRegistrationDate("2014-01-01")
                .withECNumber("123")
                .withThayiCardNumber("thayi card number 1")
                .withAadharCardNumber("aadhar card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withAddress("address1")
                .withWifeDOB("1988-01-01")
                .withPhoneNumber("phone 1")
                .withWifeEducationLevel("wife education level")
                .withHusbandEducationLevel("husband education level")
                .withCaste("sc")
                .withReligion("hindu")
                .withEconomicStatus("bpl")
                .withBPLCardNumber("bpl card number 1")
                .withJSYBeneficiary("yes")
                .withGravida("1")
                .withParity("2")
                .withNumberOfLivingChildren("3")
                .withNumberOfStillBirths("4")
                .withNumberOfAbortions("5")
                .withYoungestChildDOB("2012-01-01")
                .withLMP("2013-12-30")
                .withEDD("2014-09-01")
                .withHeight("150")
                .withBloodGroup("o -ve")
                .withIsHRP("yes")));

        ANCRegister register = registerService.getANCRegister("anm1");

        assertEquals(expectedRegister, register);
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
                                .put("youngestChildAge", "1")
                                .put("currentMethod", "ocp")
                                .put("familyPlanningMethodChangeDate", "2012-01-01")
                                .map()
                );
        when(allEligibleCouples.allOpenECsForANM("anm1")).thenReturn(asList(eligibleCouple));
        when(allMothers.findAllOpenMothersByECCaseId(asList("ecCaseId"))).thenReturn(asList(mother));
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
                .withGravida("1")
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

        ECRegister register = registerService.getECRegister("anm1");

        assertEquals(expectedRegister, register);
    }
}
