package org.ei.drishti.mapper;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.dto.register.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

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
                                                entry.fpDetails().remarks()));
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
                                .withFpDetailsDTO(new RefillableFPDetailsDTO(
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
                                .withFpDetailsDTO(new RefillableFPDetailsDTO(
                                        entry.fpDetails().fpAcceptanceDate(),
                                        entry.fpDetails().refills())
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
                                .withFpDetailsDTO(new SterilizationFPDetailsDTO(
                                        entry.fpDetails().typeOfSterilization(),
                                        entry.fpDetails().sterilizationDate(),
                                        entry.fpDetails().followupVisitDates(),
                                        entry.fpDetails().remarks())
                                );

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
                                .withFpDetailsDTO(new SterilizationFPDetailsDTO(
                                        entry.fpDetails().typeOfSterilization(),
                                        entry.fpDetails().sterilizationDate(),
                                        entry.fpDetails().followupVisitDates(),
                                        entry.fpDetails().remarks())
                                );

                    }
                });

        return new FPRegisterDTO(iudRegisterEntryDTOs, condomRegisterEntryDTOs, ocpRegisterEntryDTOs,
                maleSterilizationRegisterEntryDTOs, femaleSterilizationRegisterEntryDTOs, 2014);
    }
}
