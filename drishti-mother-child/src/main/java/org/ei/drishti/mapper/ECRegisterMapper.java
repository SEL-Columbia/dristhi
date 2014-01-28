package org.ei.drishti.mapper;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.domain.register.ECRegister;
import org.ei.drishti.domain.register.ECRegisterEntry;
import org.ei.drishti.dto.register.ECRegisterDTO;
import org.ei.drishti.dto.register.ECRegisterEntryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

@Component
public class ECRegisterMapper {
    private static Logger logger = LoggerFactory.getLogger(ECRegisterMapper.class.toString());

    public ECRegisterDTO mapToDTO(ECRegister ecRegister) {
        List<ECRegisterEntryDTO> ecRegisterEntryDTOs =
                with(ecRegister.ecRegisterEntries())
                        .convert(new Converter<ECRegisterEntry, ECRegisterEntryDTO>() {
                            @Override
                            public ECRegisterEntryDTO convert(ECRegisterEntry entry) {
                                return new ECRegisterEntryDTO()
                                        .withECNumber(entry.ecNumber())
                                        .withRegistrationDate(entry.registrationDate())
                                        .withWifeName(entry.wifeName())
                                        .withHusbandName(entry.husbandName())
                                        .withVillage(entry.village())
                                        .withSubCenter(entry.subCenter())
                                        .withPHC(entry.phc())
                                        .withWifeAge(entry.wifeAge())
                                        .withHusbandAge(entry.husbandAge())
                                        .withHouseholdNumber(entry.householdNumber())
                                        .withHouseholdAddress(entry.househouldAddress())
                                        .withHeadOfHousehold(entry.headOfHousehold())
                                        .withReligion(entry.religion())
                                        .withCaste(entry.caste())
                                        .withEconomicStatus(entry.economicStatus())
                                        .withWifeEducationLevel(entry.wifeEducationLevel())
                                        .withHusbandEducationLevel(entry.husbandEducationLevel())
                                        .withGravida(entry.gravida())
                                        .withParity(entry.parity())
                                        .withNumberOfLivingChildren(entry.numberOfLivingChildren())
                                        .withNumberOfLivingMaleChildren(entry.numberOfLivingMaleChildren())
                                        .withNumberOfLivingFemaleChildren(entry.numberOfLivingFemaleChildren())
                                        .withNumberOfStillBirths(entry.numberOfStillBirths())
                                        .withNumberOfAbortions(entry.numberOfAbortions())
                                        .withYoungestChildAge(entry.youngestChildAge())
                                        .withCurrentFPMethod(entry.currentFPMethod())
                                        .withCurrentFPMethodStartDate(entry.currentFPMethodStartDate())
                                        .withPregnancyStatus(entry.isPregnant());
                            }
                        });
        return new ECRegisterDTO(ecRegisterEntryDTOs);
    }
}
