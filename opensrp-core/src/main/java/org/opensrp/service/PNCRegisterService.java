package org.opensrp.service;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectDistinct;
import static ch.lambdaj.Lambda.selectUnique;
import static org.hamcrest.Matchers.equalTo;
import static org.opensrp.common.AllConstants.ANCFormFields.REGISTRATION_DATE;
import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.DELIVERY_COMPLICATIONS;
import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.DELIVERY_PLACE;
import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.DELIVERY_TYPE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.HOUSEHOLD_ADDRESS;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FP_METHOD_CHANGE_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.DISCHARGE_DATE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opensrp.domain.EligibleCouple;
import org.opensrp.domain.Mother;
import org.opensrp.domain.register.PNCRegister;
import org.opensrp.domain.register.PNCRegisterEntry;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PNCRegisterService {
    private final AllMothers allMothers;
    private final AllEligibleCouples allEligibleCouples;

    @Autowired
    public PNCRegisterService(AllMothers allMothers,
                              AllEligibleCouples allEligibleCouples) {
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
    }

    public PNCRegister getRegisterForANM(String anmIdentifier) {
        ArrayList<PNCRegisterEntry> pncRegisterEntries = new ArrayList<>();
        List<Mother> mothers = allMothers.findAllOpenPNCsForANM(anmIdentifier);
        Collection<String> ecIDs = selectDistinct(collect(mothers, on(Mother.class).ecCaseId()));
        List<String> ecIdsList = new ArrayList<>();
        ecIdsList.addAll(ecIDs);
        List<EligibleCouple> ecs = allEligibleCouples.findAll(ecIdsList);
        for (Mother mother : mothers) {
            EligibleCouple ec = selectUnique(ecs,
                    having(on(EligibleCouple.class).caseId(), equalTo(mother.ecCaseId())));
            PNCRegisterEntry pncRegisterEntry = new PNCRegisterEntry()
                    .withRegistrationDate(mother.getDetail(REGISTRATION_DATE))
                    .withThayiCardNumber(mother.thayiCardNumber())
                    .withWifeName(ec.wifeName())
                    .withHusbandName(ec.husbandName())
                    .withWifeDOB(ec.wifeDOB())
                    .withAddress(ec.getDetail(HOUSEHOLD_ADDRESS))
                    .withDateOfDelivery(mother.dateOfDelivery().toString())
                    .withPlaceOfDelivery(mother.getDetail(DELIVERY_PLACE))
                    .withTypeOfDelivery(mother.getDetail(DELIVERY_TYPE))
                    .withDischargeDate(mother.getDetail(DISCHARGE_DATE))
                    .withFPMethodName(ec.getDetail(CURRENT_FP_METHOD_FIELD_NAME))
                    .withFPMethodDate(ec.getDetail(FP_METHOD_CHANGE_DATE_FIELD_NAME))
                    .withChildrenDetails(mother.childrenDetails())
                    .withPNCVisits(mother.pncVisits())
                    .withDeliveryComplications(mother.getDetail(DELIVERY_COMPLICATIONS));
            pncRegisterEntries.add(pncRegisterEntry);
        }
        return new PNCRegister(pncRegisterEntries);
    }
}
