package org.ei.drishti.mapper;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.domain.register.ANCRegister;
import org.ei.drishti.domain.register.ANCRegisterEntry;
import org.ei.drishti.dto.register.ANCRegisterDTO;
import org.ei.drishti.dto.register.ANCRegisterEntryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

@Component
public class ANCRegisterMapper {
    private static Logger logger = LoggerFactory.getLogger(ANCRegisterMapper.class.toString());

    public ANCRegisterDTO mapToDTO(ANCRegister ancRegister) {
        List<ANCRegisterEntryDTO> ancRegisterEntryDTOs =
                with(ancRegister.ancRegisterEntries())
                        .convert(new Converter<ANCRegisterEntry, ANCRegisterEntryDTO>() {
                            @Override
                            public ANCRegisterEntryDTO convert(ANCRegisterEntry entry) {
                                return new ANCRegisterEntryDTO()
                                        .withANCNumber(entry.ancNumber())
                                        .withRegistrationDate(entry.registrationDate())
                                        .withECNumber(entry.ecNumber())
                                        .withThayiCardNumber(entry.thayiCardNumber())
                                        .withAadharCardNumber(entry.aadharCardNumber())
                                        .withWifeName(entry.wifeName())
                                        .withHusbandName(entry.husbandName())
                                        .withAddress(entry.address())
                                        .withWifeDOB(entry.wifeDOB())
                                        .withPhoneNumber(entry.phoneNumber())
                                        .withWifeEducationLevel(entry.wifeEducationLevel())
                                        .withHusbandEducationLevel(entry.husbandEducationLevel())
                                        .withCaste(entry.caste())
                                        .withReligion(entry.religion())
                                        .withEconomicStatus(entry.economicStatus())
                                        .withBPLCardNumber(entry.bplCardNumber())
                                        .withJSYBeneficiary(entry.jsyBeneficiary())
                                        .withGravida(entry.gravida())
                                        .withParity(entry.parity())
                                        .withNumberOfLivingChildren(entry.numberOfLivingChildren())
                                        .withNumberOfStillBirths(entry.numberOfStillBirths())
                                        .withNumberOfAbortions(entry.numberOfAbortions())
                                        .withYoungestChildDOB(entry.youngestChildDOB())
                                        .withLMP(entry.lmp())
                                        .withEDD(entry.edd())
                                        .withHeight(entry.height())
                                        .withBloodGroup(entry.bloodGroup())
                                        .withIsHRP(entry.isHRP());
                            }
                        });
        return new ANCRegisterDTO(ancRegisterEntryDTOs);
    }
}
