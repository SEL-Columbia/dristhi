package org.opensrp.service;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.opensrp.common.AllConstants.ANCFormFields.REGISTRATION_DATE;
import static org.opensrp.common.AllConstants.ANCRegistrationFormFields.ANC_NUMBER;
import static org.opensrp.common.AllConstants.ANCRegistrationFormFields.BLOOD_GROUP;
import static org.opensrp.common.AllConstants.ANCRegistrationFormFields.EDD;
import static org.opensrp.common.AllConstants.ANCRegistrationFormFields.HEIGHT;
import static org.opensrp.common.AllConstants.ANCRegistrationFormFields.JSY_BENEFICIARY;
import static org.opensrp.common.AllConstants.CommonFormFields.IS_HIGH_RISK;
import static org.opensrp.common.AllConstants.ECRegistrationFields.AADHAR_NUMBER;
import static org.opensrp.common.AllConstants.ECRegistrationFields.BPL_CARD_NUMBER;
import static org.opensrp.common.AllConstants.ECRegistrationFields.CASTE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.ECONOMIC_STATUS;
import static org.opensrp.common.AllConstants.ECRegistrationFields.HEAD_OF_HOUSEHOLD;
import static org.opensrp.common.AllConstants.ECRegistrationFields.HOUSEHOLD_ADDRESS;
import static org.opensrp.common.AllConstants.ECRegistrationFields.HOUSEHOLD_NUMBER;
import static org.opensrp.common.AllConstants.ECRegistrationFields.HUSBAND_AGE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.HUSBAND_EDUCATION_LEVEL;
import static org.opensrp.common.AllConstants.ECRegistrationFields.NUMBER_OF_ABORTIONS;
import static org.opensrp.common.AllConstants.ECRegistrationFields.NUMBER_OF_LIVING_CHILDREN;
import static org.opensrp.common.AllConstants.ECRegistrationFields.NUMBER_OF_LIVING_FEMALE_CHILDREN;
import static org.opensrp.common.AllConstants.ECRegistrationFields.NUMBER_OF_LIVING_MALE_CHILDREN;
import static org.opensrp.common.AllConstants.ECRegistrationFields.NUMBER_OF_PREGNANCIES;
import static org.opensrp.common.AllConstants.ECRegistrationFields.NUMBER_OF_STILL_BIRTHS;
import static org.opensrp.common.AllConstants.ECRegistrationFields.PARITY;
import static org.opensrp.common.AllConstants.ECRegistrationFields.PHONE_NUMBER;
import static org.opensrp.common.AllConstants.ECRegistrationFields.RELIGION;
import static org.opensrp.common.AllConstants.ECRegistrationFields.WIFE_AGE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.WIFE_EDUCATIONAL_LEVEL;
import static org.opensrp.common.AllConstants.ECRegistrationFields.YOUNGEST_CHILD_AGE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.YOUNGEST_CHILD_DOB;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FP_METHOD_CHANGE_DATE_FIELD_NAME;

import java.util.ArrayList;
import java.util.List;

import org.opensrp.domain.EligibleCouple;
import org.opensrp.domain.Mother;
import org.opensrp.domain.register.ANCRegister;
import org.opensrp.domain.register.ANCRegisterEntry;
import org.opensrp.domain.register.ECRegister;
import org.opensrp.domain.register.ECRegisterEntry;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllMothers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    private static Logger logger = LoggerFactory.getLogger(RegisterService.class.toString());
    private final AllMothers allMothers;
    private final AllEligibleCouples allEligibleCouples;

    @Autowired
    public RegisterService(AllMothers allMothers,
                           AllEligibleCouples allEligibleCouples) {
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
    }

    public ANCRegister getANCRegister(String anmIdentifier) {
        ArrayList<ANCRegisterEntry> ancRegisterEntries = new ArrayList<>();
        List<Mother> mothers = allMothers.findAllOpenMothersForANM(anmIdentifier);
        List<String> ecIDs = collect(mothers, on(Mother.class).ecCaseId());
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
                    .withIsHRP(mother.getDetail(IS_HIGH_RISK));
            ancRegisterEntries.add(entry);
        }
        return new ANCRegister(ancRegisterEntries);
    }

    public ECRegister getECRegister(String anmIdentifier) {
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
