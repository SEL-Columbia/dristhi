package org.ei.drishti.service.reporting;

import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.FormEntityTypes.*;

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
