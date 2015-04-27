package org.opensrp.service.reporting;

import static org.opensrp.common.AllConstants.FormEntityTypes.CHILD_TYPE;
import static org.opensrp.common.AllConstants.FormEntityTypes.ELIGIBLE_COUPLE_TYPE;
import static org.opensrp.common.AllConstants.FormEntityTypes.MOTHER_TYPE;

import org.opensrp.domain.Child;
import org.opensrp.domain.EligibleCouple;
import org.opensrp.domain.Location;
import org.opensrp.domain.Mother;
import org.opensrp.repository.AllChildren;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationLoader implements ILocationLoader {

    private AllChildren allChildren;
    private AllEligibleCouples allEligibleCouples;
    private AllMothers allMothers;

    @Autowired
    public LocationLoader(AllEligibleCouples allEligibleCouples, AllMothers allMothers, AllChildren allChildren) {
        this.allChildren = allChildren;
        this.allEligibleCouples = allEligibleCouples;
        this.allMothers = allMothers;
    }

    @Override
    public Location loadLocationFor(String bindType, String caseId) {
        if (bindType.equalsIgnoreCase(CHILD_TYPE))
            return loadLocationForChild(caseId);
        if (bindType.equalsIgnoreCase(MOTHER_TYPE))
            return loadLocationForMother(caseId);
        if (bindType.equalsIgnoreCase(ELIGIBLE_COUPLE_TYPE))
            return loadLocationForEC(caseId);
        return null;
    }

    private Location loadLocationForEC(String caseId) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(caseId);
        return couple.location();
    }

    private Location loadLocationForMother(String caseId) {
        Mother mother = allMothers.findByCaseId(caseId);
        return loadLocationForEC(mother.ecCaseId());
    }

    private Location loadLocationForChild(String caseId) {
        Child child = allChildren.findByCaseId(caseId);
        return loadLocationForMother(child.motherCaseId());
    }
}
