package org.ei.drishti.web.controller;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.domain.register.ANCRegister;
import org.ei.drishti.domain.register.ANCRegisterEntry;
import org.ei.drishti.dto.register.ANCRegisterDTO;
import org.ei.drishti.dto.register.ANCRegisterEntryDTO;
import org.ei.drishti.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.ei.drishti.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class RegisterController {
    private static Logger logger = LoggerFactory.getLogger(RegisterController.class.toString());
    private RegisterService registerService;
    private final String drishtiSiteUrl;

    @Autowired
    public RegisterController(RegisterService registerService,
                              @Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl) {
        this.registerService = registerService;
        this.drishtiSiteUrl = drishtiSiteUrl;
    }

    @RequestMapping(method = GET, value = "/registers/anc")
    @ResponseBody
    public ResponseEntity<ANCRegisterDTO> getANCRegister(@RequestParam("anm-id") String anmIdentifier) {
        ANCRegister ancRegister = registerService.getANCRegister(anmIdentifier);
        return new ResponseEntity<>(mapToDTO(ancRegister), allowOrigin(drishtiSiteUrl), HttpStatus.OK);
    }

    private ANCRegisterDTO mapToDTO(ANCRegister ancRegister) {
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
