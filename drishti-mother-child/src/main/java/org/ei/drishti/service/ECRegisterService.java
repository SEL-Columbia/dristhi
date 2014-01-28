package org.ei.drishti.service;

import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.register.ECRegister;
import org.ei.drishti.domain.register.ECRegisterEntry;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ANCFormFields.REGISTRATION_DATE;
import static org.ei.drishti.common.AllConstants.ECRegistrationFields.*;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.FP_METHOD_CHANGE_DATE_FIELD_NAME;

@Service
public class ECRegisterService {
    private static Logger logger = LoggerFactory.getLogger(ECRegisterService.class.toString());
    private final AllMothers allMothers;
    private final AllEligibleCouples allEligibleCouples;

    @Autowired
    public ECRegisterService(AllMothers allMothers,
                             AllEligibleCouples allEligibleCouples) {
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
    }

    public ECRegister getRegisterForANM(String anmIdentifier) {
        ArrayList<ECRegisterEntry> ecRegisterEntries = new ArrayList<>();
        List<EligibleCouple> ecs = allEligibleCouples.allOpenECsForANM(anmIdentifier);
        for (EligibleCouple ec : ecs) {
            Integer gravida = Integer.parseInt(ec.getDetail(NUMBER_OF_LIVING_CHILDREN)) +
                    Integer.parseInt(ec.getDetail(NUMBER_OF_STILL_BIRTHS)) +
                    Integer.parseInt(ec.getDetail(NUMBER_OF_ABORTIONS));
            List<Mother> mothers = allMothers.findAllOpenMothersByECCaseId(asList(ec.caseId()));
            boolean isPregnant = mothers.size() > 0;
            ECRegisterEntry ecRegisterEntry = new ECRegisterEntry()
                    .withECNumber(ec.ecNumber())
                    .withWifeName(ec.wifeName())
                    .withHusbandName(ec.husbandName())
                    .withRegistrationDate(ec.getDetail(REGISTRATION_DATE))
                    .withVillage(ec.village())
                    .withSubCenter(ec.subCenter())
                    .withPHC(ec.phc())
                    .withWifeAge(ec.getDetail(WIFE_AGE))
                    .withHusbandAge(ec.getDetail(HUSBAND_AGE))
                    .withHouseholdNumber(ec.getDetail(HOUSEHOLD_NUMBER))
                    .withHouseholdAddress(ec.getDetail(HOUSEHOLD_ADDRESS))
                    .withHeadOfHousehold(ec.getDetail(HEAD_OF_HOUSEHOLD))
                    .withReligion(ec.getDetail(RELIGION))
                    .withCaste(ec.getDetail(CASTE))
                    .withEconomicStatus(ec.getDetail(ECONOMIC_STATUS))
                    .withWifeEducationLevel(ec.getDetail(WIFE_EDUCATIONAL_LEVEL))
                    .withHusbandEducationLevel(ec.getDetail(HUSBAND_EDUCATION_LEVEL))
                    .withNumberOfLivingChildren(ec.getDetail(NUMBER_OF_LIVING_CHILDREN))
                    .withNumberOfStillBirths(ec.getDetail(NUMBER_OF_STILL_BIRTHS))
                    .withNumberOfAbortions(ec.getDetail(NUMBER_OF_ABORTIONS))
                    .withParity(ec.getDetail(PARITY))
                    .withGravida(gravida.toString())
                    .withNumberOfLivingMaleChildren(ec.getDetail(NUMBER_OF_LIVING_MALE_CHILDREN))
                    .withNumberOfLivingFemaleChildren(ec.getDetail(NUMBER_OF_LIVING_FEMALE_CHILDREN))
                    .withYoungestChildAge(ec.getDetail(YOUNGEST_CHILD_AGE))
                    .withCurrentFPMethod(ec.getDetail(CURRENT_FP_METHOD_FIELD_NAME))
                    .withCurrentFPMethodStartDate(ec.getDetail(FP_METHOD_CHANGE_DATE_FIELD_NAME))
                    .withPregnancyStatus(isPregnant);
            ecRegisterEntries.add(ecRegisterEntry);
        }
        return new ECRegister(ecRegisterEntries);
    }
}
