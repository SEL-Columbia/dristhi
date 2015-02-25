package org.opensrp.mapper;

import ch.lambdaj.function.convert.Converter;

import org.opensrp.domain.register.ChildRegister;
import org.opensrp.domain.register.ChildRegisterEntry;
import org.opensrp.dto.register.ChildRegisterDTO;
import org.opensrp.dto.register.ChildRegisterEntryDTO;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

@Component
public class ChildRegisterMapper {
    public ChildRegisterDTO mapToDTO(ChildRegister childRegister) {
        List<ChildRegisterEntryDTO> childRegisterEntryDTOs =
                with(childRegister.childRegisterEntries())
                        .convert(new Converter<ChildRegisterEntry, ChildRegisterEntryDTO>() {
                            @Override
                            public ChildRegisterEntryDTO convert(ChildRegisterEntry entry) {
                                return new ChildRegisterEntryDTO()
                                        .withThayiCardNumber(entry.thayiCardNumber())
                                        .withWifeName(entry.wifeName())
                                        .withHusbandName(entry.husbandName())
                                        .withDOB(entry.dob())
                                        .withVillage(entry.village())
                                        .withSubCenter(entry.subCenter())
                                        .withWifeDOB(entry.wifeDOB())
                                        .withImmunizations(entry.immunizations())
                                        .withVitaminADoses(entry.vitaminADoses());
                            }
                        });
        return new ChildRegisterDTO(childRegisterEntryDTOs);
    }
}
