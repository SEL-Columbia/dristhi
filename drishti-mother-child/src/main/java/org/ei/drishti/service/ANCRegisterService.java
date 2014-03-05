package org.ei.drishti.service;

import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.register.ANCRegister;
import org.ei.drishti.domain.register.ANCRegisterEntry;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.ei.drishti.common.AllConstants.ANCFormFields.REGISTRATION_DATE;
import static org.ei.drishti.common.AllConstants.ANCRegistrationFormFields.*;
import static org.ei.drishti.common.AllConstants.CommonFormFields.IS_HIGH_RISK;
import static org.ei.drishti.common.AllConstants.ECRegistrationFields.*;
import static org.hamcrest.Matchers.equalTo;

@Service
public class ANCRegisterService {
    private static Logger logger = LoggerFactory.getLogger(ANCRegisterService.class.toString());
    private final AllMothers allMothers;
    private final AllEligibleCouples allEligibleCouples;

    @Autowired
    public ANCRegisterService(AllMothers allMothers,
                              AllEligibleCouples allEligibleCouples) {
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
    }

    public ANCRegister getRegisterForANM(String anmIdentifier) {
        ArrayList<ANCRegisterEntry> ancRegisterEntries = new ArrayList<>();
        List<Mother> mothers = allMothers.findAllOpenMothersForANM(anmIdentifier);
        List<String> ecIDs = new ArrayList<>(new HashSet<>(collect(mothers, on(Mother.class).ecCaseId())));
        List<EligibleCouple> ecs = allEligibleCouples.findAll(ecIDs);
        for (Mother mother : mothers) {
            EligibleCouple ec = selectUnique(ecs,
                    having(on(EligibleCouple.class).caseId(), equalTo(mother.ecCaseId())));
            ANCRegisterEntry entry = new ANCRegisterEntry()
                    .withANCNumber(mother.getDetail(ANC_NUMBER))
                    .withRegistrationDate(mother.getDetail(REGISTRATION_DATE))
                    .withECNumber(ec.ecNumber())
                    .withThayiCardNumber(mother.thayiCardNumber())
                    .withAadharCardNumber(ec.getDetail(AADHAR_NUMBER))
                    .withWifeName(ec.wifeName())
                    .withHusbandName(ec.husbandName())
                    .withAddress(ec.getDetail(HOUSEHOLD_ADDRESS))
                    .withWifeDOB(ec.wifeDOB())
                    .withPhoneNumber(ec.getDetail(PHONE_NUMBER))
                    .withWifeEducationLevel(ec.getDetail(WIFE_EDUCATIONAL_LEVEL))
                    .withHusbandEducationLevel(ec.getDetail(HUSBAND_EDUCATION_LEVEL))
                    .withCaste(ec.getDetail(CASTE))
                    .withReligion(ec.getDetail(RELIGION))
                    .withEconomicStatus(ec.getDetail(ECONOMIC_STATUS))
                    .withBPLCardNumber(ec.getDetail(BPL_CARD_NUMBER))
                    .withJSYBeneficiary(mother.getDetail(JSY_BENEFICIARY))
                    .withGravida(ec.getDetail(NUMBER_OF_PREGNANCIES))
                    .withParity(ec.getDetail(PARITY))
                    .withNumberOfLivingChildren(ec.getDetail(NUMBER_OF_LIVING_CHILDREN))
                    .withNumberOfStillBirths(ec.getDetail(NUMBER_OF_STILL_BIRTHS))
                    .withNumberOfAbortions(ec.getDetail(NUMBER_OF_ABORTIONS))
                    .withYoungestChildDOB(ec.getDetail(YOUNGEST_CHILD_DOB))
                    .withLMP(mother.lmp().toString())
                    .withEDD(mother.getDetail(EDD))
                    .withHeight(mother.getDetail(HEIGHT))
                    .withBloodGroup(mother.getDetail(BLOOD_GROUP))
                    .withIsHRP(mother.getDetail(IS_HIGH_RISK))
                    .withHBTests(mother.hbTests())
                    .withANCVisits(mother.ancVisits())
                    .withIFATablets(mother.ifaTablets())
                    .withTTDoses(mother.ttDoses());
            ancRegisterEntries.add(entry);
        }
        return new ANCRegister(ancRegisterEntries);
    }
}
