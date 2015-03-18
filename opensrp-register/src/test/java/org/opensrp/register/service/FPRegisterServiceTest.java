package org.opensrp.register.service;

import org.opensrp.common.domain.ReportMonth;
import org.opensrp.register.domain.EligibleCouple;
import org.opensrp.register.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.opensrp.common.util.EasyMap.create;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.register.CondomFPDetails;
import org.opensrp.register.CondomRegisterEntry;
import org.opensrp.register.FPRegister;
import org.opensrp.register.FemaleSterilizationFPDetails;
import org.opensrp.register.FemaleSterilizationRegisterEntry;
import org.opensrp.register.IUDFPDetails;
import org.opensrp.register.IUDRegisterEntry;
import org.opensrp.register.MaleSterilizationFPDetails;
import org.opensrp.register.MaleSterilizationRegisterEntry;
import org.opensrp.register.OCPFPDetails;
import org.opensrp.register.OCPRegisterEntry;
import org.opensrp.register.mapper.FPRegisterMapper;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.register.service.FPRegisterService;

public class FPRegisterServiceTest {

    private FPRegisterService registerService;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private ReportMonth reportMonth;
    @Mock
    private FPRegisterMapper fpRegisterMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        registerService = new FPRegisterService(allEligibleCouples, fpRegisterMapper, reportMonth);
    }

    @Test
    public void shouldGetFPRegisterForAGivenANM() throws Exception {
        Map<String, String> details = create("wifeAge", "27")
                .put("husbandAge", "30")
                .put("caste", "c_others").put("religion", "Hindu")
                .put("numberOfLivingMaleChildren", "1")
                .put("numberOfLivingFemaleChild", "1")
                .put("educationalLevel", "illiterate")
                .put("husbandEducationLevel", "illiterate")
                .put("lmpDate", "2014-05-12")
                .put("uptResult", "+ve")
                .map();
        Map<String, String> refill1 = create("date", "2014-10-02")
                .put("pieces", "10")
                .map();
        Map<String, String> refill2 = create("date", "2014-10-12")
                .put("pieces", "10")
                .map();
        Map<String, String> refill3 = create("date", "2014-10-22")
                .put("pieces", "10")
                .map();
        Map<String, String> refill4 = create("date", "2014-10-24")
                .put("pieces", "10")
                .map();
        details.put("currentMethod", "iud");
        EligibleCouple firstEligibleCouple = new EligibleCouple("ec id1", "90")
                .withCouple("Saranya", "Manjunatha")
                .withLocation("boregowdanakoppalu", "hosa_agrahara", "phc")
                .withDetails(details)
                .withIUDFPDetails(asList(new IUDFPDetails(
                        "2014-02-23", "district hospital", "2014-04-12", "negative"),
                        new IUDFPDetails("2014-06-23", "district hospital", "2014-05-12", "negative")))
                .withOCPFPDetails(asList(new OCPFPDetails("2014-02-23", asList(refill1, refill2, refill3, refill4), "2014-06-12", "negative"),
                        new OCPFPDetails("2014-06-23", asList(refill1, refill2, refill3, refill4), "2014-07-12", "negative")));
        EligibleCouple secondEligibleCouple = new EligibleCouple("ec id2", "91")
                .withCouple("Saranya", "Manjunatha")
                .withLocation("boregowdanakoppalu", "hosa_agrahara", "phc")
                .withDetails(details)
                .withCondomFPDetails(asList(new CondomFPDetails("2014-02-23", asList(refill1, refill2)),
                        new CondomFPDetails("2014-06-23", asList(refill1, refill2))));
        EligibleCouple thirdEligibleCouple = new EligibleCouple("ec id3", "92")
                .withCouple("Saranya", "Manjunatha")
                .withLocation("boregowdanakoppalu", "hosa_agrahara", "phc")
                .withDetails(details)
                .withOCPFPDetails(asList(new OCPFPDetails("2014-02-23", asList(refill1, refill2, refill3, refill4), "2014-08-12", "negative")));
        EligibleCouple fourthEligibleCouple = new EligibleCouple("ec id4", "93")
                .withCouple("Saranya", "Manjunatha")
                .withLocation("boregowdanakoppalu", "hosa_agrahara", "phc")
                .withDetails(details)
                .withMaleSterilizationFPDetails(asList(new MaleSterilizationFPDetails("nsv", "2014-05-01",
                        asList("2014-05-06", "2014-05-10")),
                        new MaleSterilizationFPDetails("conventional_vasectomy", "2014-05-01",
                                asList("2014-05-06", "2014-05-10"))));
        EligibleCouple fifthEligibleCouple = new EligibleCouple("ec id5", "94")
                .withCouple("Saranya", "Manjunatha")
                .withLocation("boregowdanakoppalu", "hosa_agrahara", "phc")
                .withDetails(details)
                .withFemaleSterilizationFPDetails(asList(
                        new FemaleSterilizationFPDetails("minilap", "2014-06-02",
                                asList("2014-06-06", "2014-06-10", "2014-07-10")),
                        new FemaleSterilizationFPDetails("laparoscopic", "2014-06-02",
                                asList("2014-06-06", "2014-06-10", "2014-07-10"))));
        IUDRegisterEntry iudRegisterEntry = new IUDRegisterEntry()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("30")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withLmpDate("2014-05-12")
                .withUptResult("+ve")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new IUDFPDetails("2014-02-23",
                        "district hospital",
                        "2014-04-12", "negative"));
        IUDRegisterEntry anotherIUDRegistry = new IUDRegisterEntry()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("30")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withLmpDate("2014-05-12")
                .withUptResult("+ve")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new IUDFPDetails("2014-06-23",
                        "district hospital",
                        "2014-05-12", "negative"));
        CondomRegisterEntry condomRegisterEntry = new CondomRegisterEntry()
                .withEcNumber("91")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new CondomFPDetails("2014-02-23", asList(refill1, refill2)));
        CondomRegisterEntry anotherCondomRegisterEntry = new CondomRegisterEntry()
                .withEcNumber("91")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new CondomFPDetails("2014-06-23", asList(refill1, refill2)));
        OCPRegisterEntry ocpRegisterEntry = new OCPRegisterEntry()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new OCPFPDetails("2014-02-23", asList(refill1, refill2, refill3, refill4), "2014-06-12", "negative"));
        OCPRegisterEntry oneMoreOCPPRegisterEntry = new OCPRegisterEntry()
                .withEcNumber("90")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new OCPFPDetails("2014-06-23", asList(refill1, refill2, refill3, refill4), "2014-07-12", "negative"));
        OCPRegisterEntry anotherOCPRegisterEntry = new OCPRegisterEntry()
                .withEcNumber("92")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new OCPFPDetails("2014-02-23", asList(refill1, refill2, refill3, refill4), "2014-08-12", "negative"));
        MaleSterilizationRegisterEntry maleSterilizationRegisterEntry = new MaleSterilizationRegisterEntry()
                .withEcNumber("93")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
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
        MaleSterilizationRegisterEntry anotherMaleSterilizationRegisterEntry = new MaleSterilizationRegisterEntry()
                .withEcNumber("93")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("30")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new MaleSterilizationFPDetails("conventional_vasectomy", "2014-05-01", asList("2014-05-06",
                        "2014-05-10")));
        FemaleSterilizationRegisterEntry femaleSterilizationRegisterEntry = new FemaleSterilizationRegisterEntry()
                .withEcNumber("94")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
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
        FemaleSterilizationRegisterEntry anotherFemaleSterilizationRegisterEntry = new FemaleSterilizationRegisterEntry()
                .withEcNumber("94")
                .withWifeName("Saranya")
                .withHusbandName("Manjunatha")
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withWifeAge("27")
                .withHusbandAge("30")
                .withCaste("c_others")
                .withReligion("Hindu")
                .withNumberOfLivingMaleChildren("1")
                .withNumberOfLivingFemaleChildren("1")
                .withWifeEducationLevel("illiterate")
                .withHusbandEducationLevel("illiterate")
                .withFpDetails(new FemaleSterilizationFPDetails("laparoscopic", "2014-06-02", asList("2014-06-06",
                        "2014-06-10",
                        "2014-07-10"
                )));
        FPRegister expectedFPRegister = new FPRegister(asList(iudRegisterEntry, anotherIUDRegistry),
                asList(condomRegisterEntry, anotherCondomRegisterEntry),
                asList(ocpRegisterEntry, oneMoreOCPPRegisterEntry, anotherOCPRegisterEntry),
                asList(maleSterilizationRegisterEntry, anotherMaleSterilizationRegisterEntry),
                asList(femaleSterilizationRegisterEntry, anotherFemaleSterilizationRegisterEntry), 2014);
        when(allEligibleCouples.allOpenECsForANM("anm1")).thenReturn(
                asList(firstEligibleCouple,
                        secondEligibleCouple,
                        thirdEligibleCouple,
                        fourthEligibleCouple,
                        fifthEligibleCouple));
        when(fpRegisterMapper.mapToIUDRegisterEntries(firstEligibleCouple)).
                thenReturn(asList(iudRegisterEntry, anotherIUDRegistry));
        when(fpRegisterMapper.mapToCondomRegisterEntries(secondEligibleCouple)).
                thenReturn(asList(condomRegisterEntry, anotherCondomRegisterEntry));
        when(fpRegisterMapper.mapToOCPRegisterEntries(firstEligibleCouple)).
                thenReturn(asList(ocpRegisterEntry, oneMoreOCPPRegisterEntry));
        when(fpRegisterMapper.mapToOCPRegisterEntries(thirdEligibleCouple)).
                thenReturn(asList(anotherOCPRegisterEntry));
        when(fpRegisterMapper.mapToMaleSterilizationRegistryEntries(fourthEligibleCouple)).
                thenReturn(asList(maleSterilizationRegisterEntry, anotherMaleSterilizationRegisterEntry));
        when(fpRegisterMapper.mapToFemaleSterilizationRegistryEntries(fifthEligibleCouple)).
                thenReturn(asList(femaleSterilizationRegisterEntry, anotherFemaleSterilizationRegisterEntry));
        when(reportMonth.reportingYear()).thenReturn(2014);

        FPRegister fpRegister = registerService.getRegisterForANM("anm1");

        assertEquals(expectedFPRegister, fpRegister);
    }
}
