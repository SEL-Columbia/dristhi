package org.opensrp.register.service;

import org.opensrp.common.AllConstants;
import org.opensrp.register.domain.Child;
import org.opensrp.register.domain.EligibleCouple;
import org.opensrp.domain.EntityDetail;
import org.opensrp.register.domain.Mother;
import org.opensrp.register.repository.AllChildren;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.register.repository.AllMothers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EntitiesService {
    private static Logger logger = LoggerFactory.getLogger(EntitiesService.class.toString());
    private final AllChildren allChildren;
    private final AllMothers allMothers;
    private final AllEligibleCouples allEligibleCouples;

    @Autowired
    public EntitiesService(AllEligibleCouples allEligibleCouples, AllMothers allMothers, AllChildren allChildren) {
        this.allChildren = allChildren;
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
    }

    public List<EntityDetail> entities(String anmIdentifier) {
        List<EntityDetail> entityDetails = new ArrayList<>();
        updateWithECDetails(entityDetails, anmIdentifier);
        updateWithMotherDetails(entityDetails, anmIdentifier);
        updateWithChildDetails(entityDetails, anmIdentifier);
        return entityDetails;
    }

    private void updateWithChildDetails(List<EntityDetail> entityDetails, String anmIdentifier) {
        List<Child> children = allChildren.all(anmIdentifier);
        for (Child child : children) {
            EntityDetail entity = new EntityDetail()
                    .withEntityID(child.caseId())
                    .withECNumber(ecNumber(child))
                    .withANMIdentifier(child.anmIdentifier())
                    .withEntityType(AllConstants.FormEntityTypes.CHILD_TYPE)
                    .withThayiCardNumber(child.thayiCardNumber());
            entityDetails.add(entity);
        }
    }

    private String ecNumber(Child child) {
        return ecNumber(allMothers.findByCaseId(child.motherCaseId()));
    }

    private String ecNumber(Mother mother) {
        return allEligibleCouples.findByCaseId(mother.ecCaseId()).ecNumber();
    }

    private void updateWithMotherDetails(List<EntityDetail> entityDetails, String anmIdentifier) {
        List<Mother> mothers = allMothers.all(anmIdentifier);
        for (Mother mother : mothers) {
            EntityDetail entity = new EntityDetail()
                    .withEntityID(mother.caseId())
                    .withECNumber(ecNumber(mother))
                    .withANMIdentifier(mother.anmIdentifier())
                    .withEntityType(AllConstants.FormEntityTypes.MOTHER_TYPE)
                    .withThayiCardNumber(mother.thayiCardNumber());
            entityDetails.add(entity);
        }
    }

    private void updateWithECDetails(List<EntityDetail> entityDetails, String anmIdentifier) {
        List<EligibleCouple> allECs = allEligibleCouples.all(anmIdentifier);
        for (EligibleCouple ec : allECs) {
            EntityDetail entity = new EntityDetail()
                    .withEntityID(ec.caseId())
                    .withECNumber(ec.ecNumber())
                    .withANMIdentifier(ec.anmIdentifier())
                    .withEntityType(AllConstants.FormEntityTypes.ELIGIBLE_COUPLE_TYPE)
                    .withThayiCardNumber("");
            entityDetails.add(entity);
        }
    }
}
