package org.ei.drishti.web.controller;

import org.ei.drishti.domain.PNCVisit;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.dto.register.*;
import org.ei.drishti.mapper.*;
import org.ei.drishti.service.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegisterControllerTest {

    @Mock
    private ANCRegisterService ancRegisterService;
    @Mock
    private PNCRegisterService pncRegisterService;
    @Mock
    private ECRegisterService ecRegisterService;
    @Mock
    private ChildRegisterService childRegisterService;
    @Mock
    private FPRegisterService fpRegisterService;
    @Mock
    private ANCRegisterMapper ancRegisterMapper;
    @Mock
    private ECRegisterMapper ecRegisterMapper;
    @Mock
    private FPRegisterMapper fpRegisterMapper;
    @Mock
    private ChildRegisterMapper childRegisterMapper;
    @Mock
    private PNCRegisterMapper pncRegisterMapper;
    private RegisterController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new RegisterController(
                ancRegisterService, pncRegisterService, ecRegisterService, childRegisterService, fpRegisterService,
                ancRegisterMapper, ecRegisterMapper, childRegisterMapper, fpRegisterMapper, pncRegisterMapper, "http://dristhi_site_url");
    }

    @Test
    public void shouldGetECRegisterForGivenANM() throws Exception {
        ECRegister ecRegister = new ECRegister(asList(new ECRegisterEntry()
                        .withECNumber("123")
                        .withRegistrationDate("2013-01-01")
                        .withWifeName("wifeName")
                        .withHusbandName("husbandName")
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
                        .withNumberOfLivingMaleChildren("0")
                        .withNumberOfLivingFemaleChildren("1")
                        .withNumberOfStillBirths("0")
                        .withNumberOfAbortions("1")
                        .withYoungestChildAge("1")
                        .withCurrentFPMethod("ocp")
                        .withCurrentFPMethodStartDate("2012-01-01")
                        .withPregnancyStatus(false)
        ));
        ECRegisterDTO expectedECRegisterDTO = new ECRegisterDTO(asList(new ECRegisterEntryDTO()
                        .withECNumber("123")
                        .withRegistrationDate("2013-01-01")
                        .withWifeName("wifeName")
                        .withHusbandName("husbandName")
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
                        .withNumberOfLivingMaleChildren("0")
                        .withNumberOfLivingFemaleChildren("1")
                        .withNumberOfStillBirths("0")
                        .withNumberOfAbortions("1")
                        .withYoungestChildAge("1")
                        .withCurrentFPMethod("ocp")
                        .withCurrentFPMethodStartDate("2012-01-01")
                        .withPregnancyStatus("no")
        ));

        when(ecRegisterService.getRegisterForANM("anm1")).thenReturn(ecRegister);
        when(ecRegisterMapper.mapToDTO(ecRegister)).thenReturn(expectedECRegisterDTO);

        ResponseEntity<ECRegisterDTO> response = controller.getECRegister("anm1");

        assertEquals(expectedECRegisterDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldGetANCRegisterForGivenANM() throws Exception {
        ANCRegister ancRegister = new ANCRegister(asList(new ANCRegisterEntry()
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
        ANCRegisterDTO expectedANCRegisterDTO = new ANCRegisterDTO(asList(new ANCRegisterEntryDTO()
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
        when(ancRegisterService.getRegisterForANM("anm1")).thenReturn(ancRegister);
        when(ancRegisterMapper.mapToDTO(ancRegister)).thenReturn(expectedANCRegisterDTO);

        ResponseEntity<ANCRegisterDTO> response = controller.getANCRegister("anm1");

        assertEquals(expectedANCRegisterDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldGetPNCRegisterForGivenANM() throws Exception {
        PNCRegister pncRegister = new PNCRegister(asList(new PNCRegisterEntry()
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
                .withFPMethodName("female_sterilization")
                .withFPMethodDate("2014-01-01")
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

        PNCRegisterDTO expectedPNCRegisterDTO = new PNCRegisterDTO(asList(new PNCRegisterEntryDTO().withRegistrationDate("2014-01-01")
                        .withThayiCardNumber("thayi card number 1")
                        .withWifeName("name1")
                        .withHusbandName("name2")
                        .withWifeDOB("1989-01-01")
                        .withAddress("address1")
                        .withDateOfDelivery("2014-01-01")
                        .withPlaceOfDelivery("phc")
                        .withTypeOfDelivery("normal")
                        .withDischargeDate("2014-01-01")
                        .withFPMethodName("female_sterilization")
                        .withFPMethodDate("2014-01-01")
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
                        ).withPNCVisits(asList(new PNCVisitDTO()
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
                                ), new PNCVisitDTO()
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
                                )))
        ));
        when(pncRegisterService.getRegisterForANM("anm1")).thenReturn(pncRegister);
        when(pncRegisterMapper.mapToDTO(pncRegister)).thenReturn(expectedPNCRegisterDTO);

        ResponseEntity<PNCRegisterDTO> response = controller.getPNCRegister("anm1");

        assertEquals(expectedPNCRegisterDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldGetChildRegisterForGivenANM() throws Exception {
        Map<String, String> immunizations = create("bcg", "2013-01-01")
                .put("opv_0", "2013-01-01")
                .put("hepb_0", "2013-01-01")
                .put("opv_1", "2013-01-01")
                .put("pentavalent_1", "2013-01-01")
                .put("opv_2", "2013-01-01")
                .put("pentavalent_2", "2013-01-01")
                .put("opv_3", "2013-01-01")
                .put("pentavalent_3", "2013-01-01")
                .put("measles", "2013-01-01")
                .put("je", "2013-01-01")
                .put("mmr", "2013-01-01")
                .put("dptbooster_1", "2013-01-01")
                .put("dptbooster_2", "2013-01-01")
                .put("opvbooster", "2013-01-01")
                .put("measlesbooster", "2013-01-01")
                .put("je_2", "2013-01-01")
                .map();
        Map<String, String> vitaminADoses = create("1", "2013-01-01")
                .put("2", "2013-01-01")
                .map();
        ChildRegister childRegister = new ChildRegister(asList(new ChildRegisterEntry()
                .withThayiCardNumber("thayi card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withDOB("2013-01-01")
                .withVillage("village1")
                .withSubCenter("subCenter1")
                .withWifeDOB("1989-01-01")
                .withImmunizations(immunizations)
                .withVitaminADoses(vitaminADoses)));

        ChildRegisterDTO expectedChildRegisterDTO = new ChildRegisterDTO(asList(new ChildRegisterEntryDTO()
                .withThayiCardNumber("thayi card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withDOB("2013-01-01")
                .withVillage("village1")
                .withSubCenter("subCenter1")
                .withWifeDOB("1989-01-01")
                .withImmunizations(immunizations)
                .withVitaminADoses(vitaminADoses)));

        when(childRegisterService.getRegisterForANM("anm1")).thenReturn(childRegister);
        when(childRegisterMapper.mapToDTO(childRegister)).thenReturn(expectedChildRegisterDTO);

        ResponseEntity<ChildRegisterDTO> response = controller.getChildRegister("anm1");

        assertEquals(expectedChildRegisterDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldGetFPRegisterForGivenANM() throws Exception {
        IUDRegisterEntry iudRegisterEntry = new IUDRegisterEntry()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("38")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new IUDFPDetails("2014-02-23", "district hospital", "2014-05-12", "positive"));
        Map<String, String> refill1 = create("date", "2014-10-02")
                .put("pieces", "10")
                .map();
        Map<String, String> refill2 = create("date", "2014-10-12")
                .put("pieces", "10")
                .map();
        CondomRegisterEntry condomRegisterEntry = new CondomRegisterEntry()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new CondomFPDetails("2014-02-23", asList(refill1, refill2)));
        Map<String, String> refill3 = create("date", "2014-10-22")
                .put("pieces", "10")
                .map();
        Map<String, String> refill4 = create("date", "2014-10-24")
                .put("pieces", "10")
                .map();
        OCPRegisterEntry ocpRegisterEntry = new OCPRegisterEntry()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new OCPFPDetails("2014-02-23", asList(refill1, refill2, refill3, refill4)));
        MaleSterilizationRegisterEntry maleSterilizationRegisterEntry = new MaleSterilizationRegisterEntry()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("30")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new MaleSterilizationFPDetails("nsv", "2014-05-01", asList("2014-05-06",
                        "2014-05-10")));
        FemaleSterilizationRegisterEntry femaleSterilizationRegisterEntry = new FemaleSterilizationRegisterEntry()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("30")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new FemaleSterilizationFPDetails("minilap", "2014-06-02", asList("2014-06-06",
                        "2014-06-10",
                        "2014-07-10"
                )));
        FPRegister fpRegister = new FPRegister(asList(iudRegisterEntry), asList(condomRegisterEntry),
                asList(ocpRegisterEntry), asList(maleSterilizationRegisterEntry),
                asList(femaleSterilizationRegisterEntry), 2014);

        IUDRegisterEntryDTO iudRegisterEntryDTO = new IUDRegisterEntryDTO()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("38")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withLmpDate("2014-05-12")
                .withUptResult("+ve")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new IUDFPDetailsDTO("2014-02-23", "district hospital"));
        CondomRegisterEntryDTO condomRegisterEntryDTO = new CondomRegisterEntryDTO()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetailsDTO(new RefillableFPDetailsDTO("2014-02-23", asList(refill1, refill2)));
        OCPRegisterEntryDTO ocpRegisterEntryDTO = new OCPRegisterEntryDTO()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetailsDTO(new RefillableFPDetailsDTO("2014-02-23", asList(refill1, refill2, refill3, refill4)));
        MaleSterilizationRegisterEntryDTO maleSterilizationRegisterEntryDTO = new MaleSterilizationRegisterEntryDTO()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("30")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetailsDTO(new SterilizationFPDetailsDTO("nsv", "2014-05-01", asList("2014-05-06",
                        "2014-05-10")));
        FemaleSterilizationRegisterEntryDTO femaleSterilizationRegisterEntryDTO = new FemaleSterilizationRegisterEntryDTO()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("hosa_agrahara")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("30")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetailsDTO(new SterilizationFPDetailsDTO("minilap", "2014-06-02", asList("2014-06-06",
                        "2014-06-10",
                        "2014-07-10"
                )));
        FPRegisterDTO expectedFPRegisterDTO = new FPRegisterDTO(
                asList(iudRegisterEntryDTO),
                asList(condomRegisterEntryDTO),
                asList(ocpRegisterEntryDTO),
                asList(maleSterilizationRegisterEntryDTO),
                asList(femaleSterilizationRegisterEntryDTO), 2014);

        when(fpRegisterService.getRegisterForANM("anm1")).thenReturn(fpRegister);
        when(fpRegisterMapper.mapToDTO(fpRegister)).thenReturn(expectedFPRegisterDTO);

        ResponseEntity<FPRegisterDTO> response = controller.getFPRegister("anm1");

        assertEquals(expectedFPRegisterDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
