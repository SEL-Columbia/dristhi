package org.ei.drishti.service;

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.register.ANCRegister;
import org.ei.drishti.domain.register.ANCRegisterEntry;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANCRegisterServiceTest {

    private ANCRegisterService registerService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        registerService = new ANCRegisterService(allMothers, allEligibleCouples);
    }

    @Test
    public void shouldGetANCRegisterForAGivenANM() throws Exception {
        Mother mother = new Mother("caseId", "ecCaseId", "thayi card number 1")
                .withLMP(LocalDate.parse("2013-12-30"))
                .withANCVisits(asList(
                        create("ancVisitDate", "2013-01-01")
                                .put("weight", "55")
                                .put("bpSystolic", "120")
                                .put("bpDiastolic", "80")
                                .map()))
                .withIFATablets(asList(
                        create("ifaTabletsDate", "2013-05-24")
                                .put("numberOfIFATabletsGiven", "30")
                                .map()))
                .withTTDoses(asList(create("ttDate", "2012-12-24")
                        .put("ttDose", "tt1")
                        .map()))
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
                .withWifeDOB(LocalDate.parse("1989-01-01"))
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
                .withIsHRP("yes")
                .withANCVisits(asList(
                        create("ancVisitDate", "2013-01-01")
                                .put("weight", "55")
                                .put("bpSystolic", "120")
                                .put("bpDiastolic", "80")
                                .map()))
                .withIFATablets(asList(
                        create("ifaTabletsDate", "2013-05-24")
                                .put("numberOfIFATabletsGiven", "30")
                                .map()))
                .withTTDoses(asList(create("ttDate", "2012-12-24")
                        .put("ttDose", "tt1")
                        .map())))
        );

        ANCRegister register = registerService.getRegisterForANM("anm1");

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
                .withWifeDOB(LocalDate.parse("1988-01-01"))
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

        ANCRegister register = registerService.getRegisterForANM("anm1");

        assertEquals(expectedRegister, register);
    }
}
