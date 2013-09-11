package org.ei.drishti.service.reporting;

import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
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
        if(bindType.equalsIgnoreCase("child"))
            return allChildren.findByCaseId(caseId).location();
        if(bindType.equalsIgnoreCase("mother"))
            return allMothers.findByCaseId(caseId).location();
        if(bindType.equalsIgnoreCase("eligible_couple"))
            return allEligibleCouples.findByCaseId(caseId).location();
        return null;
    }
}
