package org.opensrp.service;

import org.opensrp.domain.EligibleCouple;
import org.opensrp.domain.Mother;
import org.opensrp.domain.PNCVisit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.domain.register.PNCRegister;
import org.opensrp.domain.register.PNCRegisterEntry;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllMothers;
import org.opensrp.service.PNCRegisterService;

public class PNCRegisterServiceTest {

    private PNCRegisterService registerService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        registerService = new PNCRegisterService(allMothers, allEligibleCouples);
    }

    @Test
    public void shouldGetPNCRegisterForAGivenANM() throws Exception {
        Mother mother = new Mother("caseId", "ecCaseId", "thayi card number 1")
                .withDetails(
                        create("deliveryPlace", "phc")
                                .put("registrationDate", "2014-01-01")
                                .put("dischargeDate", "2014-01-01")
                                .put("deliveryType", "normal")
                                .put("deliveryComplications", "difficulty breathing")
                                .map()
                )
                .withChildrenDetails(asList(create("id", "123")
                                .put("sex", "male")
                                .put("birthWeight", "2")
                                .put("breastFeedingInOneHour", "yes")
                                .map(),
                        create("id", "234")
                                .put("sex", "male")
                                .put("birthWeight", "2")
                                .put("breastFeedingInOneHour", "yes")
                                .map()
                ))
                .withPNCVisits(asList(new PNCVisit()
                                .withDate("2014-01-01")
                                .withPerson("ASHA")
                                .withPlace("phc")
                                .withDifficulties("difficulty 1")
                                .withAbdominalProblems("abdominal problem")
                                .withVaginalProblems("vaginal problem")
                                .withUrinalProblems("urinal problem")
                                .withBreastProblems("breast problem")
                                .withChildrenDetails(asList(create("id", "123")
                                                        .put("complications 1", "complication 1 value")
                                                        .put("complications 2", "complications 2 value")
                                                        .map(),
                                                create("id", "234")
                                                        .put("complications 1", "complication 1 value")
                                                        .put("complications 2", "complications 2 value")
                                                        .map()
                                        )
                                ),
                        new PNCVisit()
                                .withDate("2014-01-02")
                                .withPerson("ASHA")
                                .withPlace("phc")
                                .withDifficulties("difficulty 1")
                                .withAbdominalProblems("abdominal problem")
                                .withVaginalProblems("vaginal problem")
                                .withUrinalProblems("urinal problem")
                                .withBreastProblems("breast problem")
                                .withChildrenDetails(asList(create("id", "123")
                                                        .put("complications 1", "complication 1 value")
                                                        .put("complications 2", "complications 2 value")
                                                        .map(),
                                                create("id", "234")
                                                        .put("complications 1", "complication 1 value")
                                                        .put("complications 2", "complications 2 value")
                                                        .map()
                                        )
                                )
                ))
                .withDeliveryOutCome("2014-01-01");
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
                                .put("currentMethod", "condom")
                                .put("familyPlanningMethodChangeDate", "2014-01-02")
                                .map()
                )
                .withCouple("name1", "name2");
        when(allMothers.findAllOpenPNCsForANM("anm1")).thenReturn(asList(mother));
        when(allEligibleCouples.findAll(asList("ecCaseId"))).thenReturn(asList(eligibleCouple));
        PNCRegister expectedRegister = new PNCRegister(asList(new PNCRegisterEntry()
                .withRegistrationDate("2014-01-01")
                .withThayiCardNumber("thayi card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withWifeDOB("1989-01-01")
                .withAddress("address1")
                .withDateOfDelivery("2014-01-01")
                .withPlaceOfDelivery("phc")
                .withTypeOfDelivery("normal")
                .withDischargeDate("2014-01-01")
                .withFPMethodName("condom")
                .withFPMethodDate("2014-01-02")
                .withDeliveryComplications("difficulty breathing")
                .withChildrenDetails(asList(create("id", "123")
                                        .put("sex", "male")
                                        .put("birthWeight", "2")
                                        .put("breastFeedingInOneHour", "yes")
                                        .map(),
                                create("id", "234")
                                        .put("sex", "male")
                                        .put("birthWeight", "2")
                                        .put("breastFeedingInOneHour", "yes")
                                        .map()
                        )
                ).withPNCVisits(asList(new PNCVisit()
                                .withDate("2014-01-01")
                                .withPerson("ASHA")
                                .withPlace("phc")
                                .withDifficulties("difficulty 1")
                                .withAbdominalProblems("abdominal problem")
                                .withVaginalProblems("vaginal problem")
                                .withUrinalProblems("urinal problem")
                                .withBreastProblems("breast problem")
                                .withChildrenDetails(asList(create("id", "123")
                                                        .put("complications 1", "complication 1 value")
                                                        .put("complications 2", "complications 2 value")
                                                        .map(),
                                                create("id", "234")
                                                        .put("complications 1", "complication 1 value")
                                                        .put("complications 2", "complications 2 value")
                                                        .map()
                                        )
                                ),
                        new PNCVisit()
                                .withDate("2014-01-02")
                                .withPerson("ASHA")
                                .withPlace("phc")
                                .withDifficulties("difficulty 1")
                                .withAbdominalProblems("abdominal problem")
                                .withVaginalProblems("vaginal problem")
                                .withUrinalProblems("urinal problem")
                                .withBreastProblems("breast problem")
                                .withChildrenDetails(asList(create("id", "123")
                                                        .put("complications 1", "complication 1 value")
                                                        .put("complications 2", "complications 2 value")
                                                        .map(),
                                                create("id", "234")
                                                        .put("complications 1", "complication 1 value")
                                                        .put("complications 2", "complications 2 value")
                                                        .map()
                                        )
                                )
                ))));

        PNCRegister register = registerService.getRegisterForANM("anm1");

        assertEquals(expectedRegister, register);
    }
}
