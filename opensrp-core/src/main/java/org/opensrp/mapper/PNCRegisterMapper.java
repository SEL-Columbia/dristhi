package org.opensrp.mapper;

import ch.lambdaj.function.convert.Converter;
import org.opensrp.domain.PNCVisit;
import org.opensrp.domain.register.PNCRegister;
import org.opensrp.domain.register.PNCRegisterEntry;
import org.opensrp.dto.register.PNCRegisterDTO;
import org.opensrp.dto.register.PNCRegisterEntryDTO;
import org.opensrp.dto.register.PNCVisitDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

@Component
public class PNCRegisterMapper {
    public PNCRegisterDTO mapToDTO(PNCRegister pncRegister) {
        List<PNCRegisterEntryDTO> pncRegisterEntryDTOs =
                with(pncRegister.pncRegisterEntries())
                        .convert(new Converter<PNCRegisterEntry, PNCRegisterEntryDTO>() {
                            @Override
                            public PNCRegisterEntryDTO convert(PNCRegisterEntry entry) {
                                List<PNCVisitDTO> pncVisitDTOs = new ArrayList<>();
                                for (PNCVisit pncVisitDetail : entry.pncVisitDetails()) {
                                    pncVisitDTOs.add(new PNCVisitDTO().withDate(pncVisitDetail.date())
                                            .withPerson(pncVisitDetail.person())
                                            .withPlace(pncVisitDetail.place())
                                            .withDifficulties(pncVisitDetail.difficulties())
                                            .withAbdominalProblems(pncVisitDetail.vaginalProblems())
                                            .withVaginalProblems(pncVisitDetail.vaginalProblems())
                                            .withUrinalProblems(pncVisitDetail.urinalProblems())
                                            .withBreastProblems(pncVisitDetail.breastProblems())
                                            .withChildrenDetails(pncVisitDetail.childrenDetails()));
                                }
                                return new PNCRegisterEntryDTO()
                                        .withRegistrationDate(entry.registrationDate())
                                        .withThayiCardNumber(entry.thayiCardNumber())
                                        .withWifeName(entry.wifeName())
                                        .withHusbandName(entry.husbandName())
                                        .withWifeDOB(entry.wifeDOB())
                                        .withAddress(entry.address())
                                        .withDateOfDelivery(entry.dateOfDelivery())
                                        .withPlaceOfDelivery(entry.placeOfDelivery())
                                        .withTypeOfDelivery(entry.typeOfDelivery())
                                        .withDischargeDate(entry.dischargeDate())
                                        .withFPMethodName(entry.fpMethodName())
                                        .withFPMethodDate(entry.fpMethodDate())
                                        .withChildrenDetails(entry.childrenDetails())
                                        .withPNCVisits(pncVisitDTOs)
                                        .withDeliveryComplications(entry.deliveryComplications());
                            }
                        });
        return new PNCRegisterDTO(pncRegisterEntryDTOs);
    }
}
