package org.ei.drishti.service.reporting;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ReportBindTypes.CHILD_BIND_TYPE;
import static org.ei.drishti.common.AllConstants.ReportBindTypes.ELIGIBLE_COUPLE_BIND_TYPE;
import static org.ei.drishti.common.AllConstants.ReportBindTypes.MOTHER_BIND_TYPE;

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
        if(bindType.equalsIgnoreCase(CHILD_BIND_TYPE))
            return allChildren.findByCaseId(caseId).location();
        if(bindType.equalsIgnoreCase(MOTHER_BIND_TYPE))
            return allMothers.findByCaseId(caseId).location();
        if(bindType.equalsIgnoreCase(ELIGIBLE_COUPLE_BIND_TYPE))
            return allEligibleCouples.findByCaseId(caseId).location();
        return null;
    }
}
