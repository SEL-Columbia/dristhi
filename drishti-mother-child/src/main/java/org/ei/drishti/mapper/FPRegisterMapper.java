package org.ei.drishti.mapper;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.dto.register.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.ei.drishti.common.AllConstants.ECRegistrationFields.*;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.LMP_DATE;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.UPT_RESULT;
import static org.ei.drishti.common.util.IntegerUtil.parseValidIntegersAndDefaultInvalidOnesToEmptyString;

@Component
public class FPRegisterMapper {
    public FPRegisterDTO mapToDTO(FPRegister fpRegister) {
        List<IUDRegisterEntryDTO> iudRegisterEntryDTOs =
                with(fpRegister.iudRegisterEntries())
                        .convert(new Converter<IUDRegisterEntry, IUDRegisterEntryDTO>() {
                            @Override
                            public IUDRegisterEntryDTO convert(IUDRegisterEntry entry) {
                                return new IUDRegisterEntryDTO()
                                        .withEcNumber(entry.ecNumber())
                                        .withWifeName(entry.wifeName())
                                        .withHusbandName(entry.husbandName())
                                        .withVillage(entry.village())
                                        .withSubCenter(entry.subCenter())
                                        .withWifeAge(entry.wifeAge())
                                        .withHusbandAge(entry.husbandAge())
                                        .withCaste(entry.caste())
                                        .withReligion(entry.religion())
                                        .withNumberOfLivingMaleChildren(entry.numberOfLivingMaleChildren())
                                        .withNumberOfLivingFemaleChildren(entry.numberOfLivingFemaleChildren())
                                        .withLmpDate(entry.lmpDate())
                                        .withUptResult(entry.uptResult())
                                        .withWifeEducationLevel(entry.wifeEducationLevel())
                                        .withHusbandEducationLevel(entry.husbandEducationLevel())
                                        .withFpDetails(new IUDFPDetailsDTO(
                                                entry.fpDetails().fpAcceptanceDate(),
                                                entry.fpDetails().iudPlace(),
                                                entry.fpDetails().lmpDate(),
                                                entry.fpDetails().uptResult()
                                        ));
                            }
                        });
        List<CondomRegisterEntryDTO> condomRegisterEntryDTOs = with(fpRegister.condomRegisterEntries())
                .convert(new Converter<CondomRegisterEntry, CondomRegisterEntryDTO>() {
                    @Override
                    public CondomRegisterEntryDTO convert(CondomRegisterEntry entry) {
                        return new CondomRegisterEntryDTO()
                                .withEcNumber(entry.ecNumber())
                                .withWifeName(entry.wifeName())
                                .withHusbandName(entry.husbandName())
                                .withVillage(entry.village())
                                .withSubCenter(entry.subCenter())
                                .withWifeAge(entry.wifeAge())
                                .withCaste(entry.caste())
                                .withReligion(entry.religion())
                                .withNumberOfLivingMaleChildren(entry.numberOfLivingMaleChildren())
                                .withNumberOfLivingFemaleChildren(entry.numberOfLivingFemaleChildren())
                                .withWifeEducationLevel(entry.wifeEducationLevel())
                                .withHusbandEducationLevel(entry.husbandEducationLevel())
                                .withFpDetails(new CondomFPDetailsDTO(
                                        entry.fpDetails().fpAcceptanceDate(),
                                        entry.fpDetails().refills())
                                );
                    }
                });

        List<OCPRegisterEntryDTO> ocpRegisterEntryDTOs = with(fpRegister.ocpRegisterEntries())
                .convert(new Converter<OCPRegisterEntry, OCPRegisterEntryDTO>() {
                    @Override
                    public OCPRegisterEntryDTO convert(OCPRegisterEntry entry) {
                        return new OCPRegisterEntryDTO()
                                .withEcNumber(entry.ecNumber())
                                .withWifeName(entry.wifeName())
                                .withHusbandName(entry.husbandName())
                                .withVillage(entry.village())
                                .withSubCenter(entry.subCenter())
                                .withWifeAge(entry.wifeAge())
                                .withCaste(entry.caste())
                                .withReligion(entry.religion())
                                .withNumberOfLivingMaleChildren(entry.numberOfLivingMaleChildren())
                                .withNumberOfLivingFemaleChildren(entry.numberOfLivingFemaleChildren())
                                .withWifeEducationLevel(entry.wifeEducationLevel())
                                .withHusbandEducationLevel(entry.husbandEducationLevel())
                                .withFpDetails(new OCPFPDetailsDTO(
                                        entry.fpDetails().fpAcceptanceDate(),
                                        entry.fpDetails().refills(),
                                        entry.fpDetails().lmpDate(),
                                        entry.fpDetails().uptResult())
                                );
                    }
                });

        List<MaleSterilizationRegisterEntryDTO> maleSterilizationRegisterEntryDTOs = with(fpRegister.maleSterilizationRegisterEntries())
                .convert(new Converter<MaleSterilizationRegisterEntry, MaleSterilizationRegisterEntryDTO>() {
                    @Override
                    public MaleSterilizationRegisterEntryDTO convert(MaleSterilizationRegisterEntry entry) {
                        return new MaleSterilizationRegisterEntryDTO()
                                .withEcNumber(entry.ecNumber())
                                .withWifeName(entry.wifeName())
                                .withHusbandName(entry.husbandName())
                                .withVillage(entry.village())
                                .withSubCenter(entry.subCenter())
                                .withWifeAge(entry.wifeAge())
                                .withHusbandAge(entry.husbandAge())
                                .withCaste(entry.caste())
                                .withReligion(entry.religion())
                                .withNumberOfLivingMaleChildren(entry.numberOfLivingMaleChildren())
                                .withNumberOfLivingFemaleChildren(entry.numberOfLivingFemaleChildren())
                                .withWifeEducationLevel(entry.wifeEducationLevel())
                                .withHusbandEducationLevel(entry.husbandEducationLevel())
                                .withFpDetails(new MaleSterilizationFPDetailsDTO(
                                        entry.fpDetails().typeOfSterilization(),
                                        entry.fpDetails().sterilizationDate(),
                                        entry.fpDetails().followupVisitDates()));
                    }
                });

        List<FemaleSterilizationRegisterEntryDTO> femaleSterilizationRegisterEntryDTOs = with(fpRegister.femaleSterilizationRegisterEntries())
                .convert(new Converter<FemaleSterilizationRegisterEntry, FemaleSterilizationRegisterEntryDTO>() {
                    @Override
                    public FemaleSterilizationRegisterEntryDTO convert(FemaleSterilizationRegisterEntry entry) {
                        return new FemaleSterilizationRegisterEntryDTO()
                                .withEcNumber(entry.ecNumber())
                                .withWifeName(entry.wifeName())
                                .withHusbandName(entry.husbandName())
                                .withVillage(entry.village())
                                .withSubCenter(entry.subCenter())
                                .withWifeAge(entry.wifeAge())
                                .withHusbandAge(entry.husbandAge())
                                .withCaste(entry.caste())
                                .withReligion(entry.religion())
                                .withNumberOfLivingMaleChildren(entry.numberOfLivingMaleChildren())
                                .withNumberOfLivingFemaleChildren(entry.numberOfLivingFemaleChildren())
                                .withWifeEducationLevel(entry.wifeEducationLevel())
                                .withHusbandEducationLevel(entry.husbandEducationLevel())
                                .withFpDetails(new FemaleSterilizationFPDetailsDTO(
                                        entry.fpDetails().typeOfSterilization(),
                                        entry.fpDetails().sterilizationDate(),
                                        entry.fpDetails().followupVisitDates()
                                )
                                );

                    }
                });

        return new FPRegisterDTO(iudRegisterEntryDTOs, condomRegisterEntryDTOs, ocpRegisterEntryDTOs,
                maleSterilizationRegisterEntryDTOs, femaleSterilizationRegisterEntryDTOs, 2014);
    }

    public List<FemaleSterilizationRegisterEntry> mapToFemaleSterilizationRegistryEntries(EligibleCouple ec) {
        List<FemaleSterilizationRegisterEntry> femaleSterilizationEntries = new ArrayList<>();
        for (FemaleSterilizationFPDetails femaleSterilizationFPDetails : ec.femaleSterilizationFPDetails()) {
            FemaleSterilizationRegisterEntry femaleSterilizationRegisterEntry = new FemaleSterilizationRegisterEntry()
                    .withEcNumber(ec.ecNumber())
                    .withWifeName(ec.wifeName())
                    .withHusbandName(ec.husbandName())
                    .withVillage(ec.village())
                    .withSubCenter(ec.subCenter())
                    .withWifeAge(ec.getDetail(WIFE_AGE))
                    .withHusbandAge(ec.getDetail(HUSBAND_AGE))
                    .withCaste(ec.getDetail(CASTE))
                    .withReligion(ec.getDetail(RELIGION))
                    .withNumberOfLivingMaleChildren(
                            parseValidIntegersAndDefaultInvalidOnesToEmptyString(
                                    ec.getDetail(NUMBER_OF_LIVING_MALE_CHILDREN)))
                    .withNumberOfLivingFemaleChildren(
                            parseValidIntegersAndDefaultInvalidOnesToEmptyString(
                                    ec.getDetail(NUMBER_OF_LIVING_FEMALE_CHILDREN)))
                    .withWifeEducationLevel(ec.getDetail(WIFE_EDUCATIONAL_LEVEL))
                    .withHusbandEducationLevel(ec.getDetail(HUSBAND_EDUCATION_LEVEL))
                    .withFpDetails(femaleSterilizationFPDetails);
            femaleSterilizationEntries.add(femaleSterilizationRegisterEntry);
        }
        return femaleSterilizationEntries;
    }

    public List<MaleSterilizationRegisterEntry> mapToMaleSterilizationRegistryEntries(EligibleCouple ec) {
        List<MaleSterilizationRegisterEntry> maleSterilizationEntries = new ArrayList<>();
        for (MaleSterilizationFPDetails maleSterilizationFPDetails : ec.maleSterilizationFPDetails()) {
            MaleSterilizationRegisterEntry maleSterilizationRegisterEntry = new MaleSterilizationRegisterEntry()
                    .withEcNumber(ec.ecNumber())
                    .withWifeName(ec.wifeName())
                    .withHusbandName(ec.husbandName())
                    .withVillage(ec.village())
                    .withSubCenter(ec.subCenter())
                    .withWifeAge(ec.getDetail(WIFE_AGE))
                    .withHusbandAge(ec.getDetail(HUSBAND_AGE))
                    .withCaste(ec.getDetail(CASTE))
                    .withReligion(ec.getDetail(RELIGION))
                    .withNumberOfLivingMaleChildren(
                            parseValidIntegersAndDefaultInvalidOnesToEmptyString(
                                    ec.getDetail(NUMBER_OF_LIVING_MALE_CHILDREN)))
                    .withNumberOfLivingFemaleChildren(
                            parseValidIntegersAndDefaultInvalidOnesToEmptyString(
                                    ec.getDetail(NUMBER_OF_LIVING_FEMALE_CHILDREN)))
                    .withWifeEducationLevel(ec.getDetail(WIFE_EDUCATIONAL_LEVEL))
                    .withHusbandEducationLevel(ec.getDetail(HUSBAND_EDUCATION_LEVEL))
                    .withFpDetails(maleSterilizationFPDetails);
            maleSterilizationEntries.add(maleSterilizationRegisterEntry);
        }
        return maleSterilizationEntries;
    }

    public List<OCPRegisterEntry> mapToOCPRegisterEntries(EligibleCouple ec) {
        List<OCPRegisterEntry> ocpRegisterEntries = new ArrayList<>();
        for (OCPFPDetails ocpFPDetails : ec.ocpFPDetails()) {
            OCPRegisterEntry ocpRegisterEntry = new OCPRegisterEntry()
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
                    .withFpDetails(ocpFPDetails);
            ocpRegisterEntries.add(ocpRegisterEntry);
        }
        return ocpRegisterEntries;
    }

    public List<CondomRegisterEntry> mapToCondomRegisterEntries(EligibleCouple ec) {
        List<CondomRegisterEntry> condomRegisterEntries = new ArrayList<>();
        for (CondomFPDetails condomFPDetails : ec.condomFPDetails()) {
            CondomRegisterEntry condomRegisterEntry = new CondomRegisterEntry()
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
                    .withFpDetails(condomFPDetails);
            condomRegisterEntries.add(condomRegisterEntry);
        }
        return condomRegisterEntries;
    }

    public List<IUDRegisterEntry> mapToIUDRegisterEntries(EligibleCouple ec) {
        List<IUDRegisterEntry> iudRegisterEntries = new ArrayList<>();
        for (IUDFPDetails iudFPDetails : ec.iudFPDetails()) {
            IUDRegisterEntry iudRegisterEntry = new IUDRegisterEntry()
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
                    .withFpDetails(iudFPDetails);
            iudRegisterEntries.add(iudRegisterEntry);
        }
        return iudRegisterEntries;
    }
}
