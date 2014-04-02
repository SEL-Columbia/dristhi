package org.ei.drishti.service;

import org.ei.drishti.common.domain.ReportMonth;
import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.repository.AllEligibleCouples;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.ei.drishti.common.AllConstants.ECRegistrationFields.*;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.LMP_DATE;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.UPT_RESULT;
import static org.ei.drishti.common.util.IntegerUtil.isInteger;

@Service
public class FPRegisterService {
    private final AllEligibleCouples allEligibleCouples;
    private ReportMonth reportMonth;

    @Autowired
    public FPRegisterService(AllEligibleCouples allEligibleCouples, ReportMonth reportMonth) {
        this.allEligibleCouples = allEligibleCouples;
        this.reportMonth = reportMonth;
    }

    public FPRegister getRegisterForANM(String anmIdentifier) {
        ArrayList<IUDRegisterEntry> iudRegisterEntries = new ArrayList<>();
        ArrayList<CondomRegisterEntry> condomRegisterEntries = new ArrayList<>();
        ArrayList<OCPRegisterEntry> ocpRegisterEntries = new ArrayList<>();
        ArrayList<MaleSterilizationRegisterEntry> maleSterilizationRegisterEntries = new ArrayList<>();
        ArrayList<FemaleSterilizationRegisterEntry> femaleSterilizationRegisterEntries = new ArrayList<>();
        List<EligibleCouple> ecs = allEligibleCouples.allOpenECsForANM(anmIdentifier);
        for (EligibleCouple ec : ecs) {
            if (ec.iudFPDetails() != null) {
                iudRegisterEntries.add(getIudRegisterEntry(ec));
            }
            if (ec.condomFPDetails() != null) {
                condomRegisterEntries.add(getCondomRegisterEntry(ec));
            }
            if (ec.ocpFPDetails() != null) {
                ocpRegisterEntries.add(getOcpRegisterEntry(ec));
            }
            if (ec.maleSterilizationFPDetails() != null) {
                maleSterilizationRegisterEntries.add(getMaleSterilizationRegisterEntry(ec));
            }
            if (ec.femaleSterilizationFPDetails() != null) {
                femaleSterilizationRegisterEntries.add(getFemaleSterilizationRegisterEntry(ec));
            }
        }
        return new FPRegister(iudRegisterEntries, condomRegisterEntries, ocpRegisterEntries,
                maleSterilizationRegisterEntries, femaleSterilizationRegisterEntries, reportMonth.reportingYear());
    }

    private FemaleSterilizationRegisterEntry getFemaleSterilizationRegisterEntry(EligibleCouple ec) {
        return new FemaleSterilizationRegisterEntry()
                .withEcNumber(ec.ecNumber())
                .withWifeName(ec.wifeName())
                .withHusbandName(ec.husbandName())
                .withVillage(ec.village())
                .withSubCenter(ec.subCenter())
                .withWifeAge(ec.getDetail(WIFE_AGE))
                .withHusbandAge(ec.getDetail(HUSBAND_AGE))
                .withCaste(ec.getDetail(CASTE))
                .withReligion(ec.getDetail(RELIGION))
                .withNumberOfLivingMaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_MALE_CHILDREN)))
                .withNumberOfLivingFemaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_FEMALE_CHILDREN)))
                .withWifeEducationLevel(ec.getDetail(WIFE_EDUCATIONAL_LEVEL))
                .withHusbandEducationLevel(ec.getDetail(HUSBAND_EDUCATION_LEVEL))
                .withFpDetails(ec.femaleSterilizationFPDetails());
    }

    private MaleSterilizationRegisterEntry getMaleSterilizationRegisterEntry(EligibleCouple ec) {
        return new MaleSterilizationRegisterEntry()
                .withEcNumber(ec.ecNumber())
                .withWifeName(ec.wifeName())
                .withHusbandName(ec.husbandName())
                .withVillage(ec.village())
                .withSubCenter(ec.subCenter())
                .withWifeAge(ec.getDetail(WIFE_AGE))
                .withHusbandAge(ec.getDetail(HUSBAND_AGE))
                .withCaste(ec.getDetail(CASTE))
                .withReligion(ec.getDetail(RELIGION))
                .withNumberOfLivingMaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_MALE_CHILDREN)))
                .withNumberOfLivingFemaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_FEMALE_CHILDREN)))
                .withWifeEducationLevel(ec.getDetail(WIFE_EDUCATIONAL_LEVEL))
                .withHusbandEducationLevel(ec.getDetail(HUSBAND_EDUCATION_LEVEL))
                .withFpDetails(ec.maleSterilizationFPDetails());
    }

    private OCPRegisterEntry getOcpRegisterEntry(EligibleCouple ec) {
        return new OCPRegisterEntry()
                .withEcNumber(ec.ecNumber())
                .withWifeName(ec.wifeName())
                .withHusbandName(ec.husbandName())
                .withVillage(ec.village())
                .withSubCenter(ec.subCenter())
                .withWifeAge(ec.getDetail(WIFE_AGE))
                .withCaste(ec.getDetail(CASTE))
                .withReligion(ec.getDetail(RELIGION))
                .withNumberOfLivingMaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_MALE_CHILDREN)))
                .withNumberOfLivingFemaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_FEMALE_CHILDREN)))
                .withWifeEducationLevel(ec.getDetail(WIFE_EDUCATIONAL_LEVEL))
                .withHusbandEducationLevel(ec.getDetail(HUSBAND_EDUCATION_LEVEL))
                .withFpDetails(ec.ocpFPDetails());
    }

    private CondomRegisterEntry getCondomRegisterEntry(EligibleCouple ec) {
        return new CondomRegisterEntry()
                .withEcNumber(ec.ecNumber())
                .withWifeName(ec.wifeName())
                .withHusbandName(ec.husbandName())
                .withVillage(ec.village())
                .withSubCenter(ec.subCenter())
                .withWifeAge(ec.getDetail(WIFE_AGE))
                .withCaste(ec.getDetail(CASTE))
                .withReligion(ec.getDetail(RELIGION))
                .withNumberOfLivingMaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_MALE_CHILDREN)))
                .withNumberOfLivingFemaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_FEMALE_CHILDREN)))
                .withWifeEducationLevel(ec.getDetail(WIFE_EDUCATIONAL_LEVEL))
                .withHusbandEducationLevel(ec.getDetail(HUSBAND_EDUCATION_LEVEL))
                .withFpDetails(ec.condomFPDetails());
    }

    private IUDRegisterEntry getIudRegisterEntry(EligibleCouple ec) {
        return new IUDRegisterEntry()
                .withEcNumber(ec.ecNumber())
                .withWifeName(ec.wifeName())
                .withHusbandName(ec.husbandName())
                .withVillage(ec.village())
                .withSubCenter(ec.subCenter())
                .withWifeAge(ec.getDetail(WIFE_AGE))
                .withHusbandAge(ec.getDetail(HUSBAND_AGE))
                .withCaste(ec.getDetail(CASTE))
                .withReligion(ec.getDetail(RELIGION))
                .withNumberOfLivingMaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_MALE_CHILDREN)))
                .withNumberOfLivingFemaleChildren(parseValidIntegersAndDefaultInvalidOnesToEmptyString(ec.getDetail(NUMBER_OF_LIVING_FEMALE_CHILDREN)))
                .withLmpDate(ec.getDetail(LMP_DATE))
                .withUptResult(ec.getDetail(UPT_RESULT))
                .withWifeEducationLevel(ec.getDetail(WIFE_EDUCATIONAL_LEVEL))
                .withHusbandEducationLevel(ec.getDetail(HUSBAND_EDUCATION_LEVEL))
                .withFpDetails(ec.iudFPDetails());
    }

    private String parseValidIntegersAndDefaultInvalidOnesToEmptyString(String value) {
        return !isInteger(value) ? "" : String.valueOf(IntegerUtil.tryParse(value, 0));
    }
}
