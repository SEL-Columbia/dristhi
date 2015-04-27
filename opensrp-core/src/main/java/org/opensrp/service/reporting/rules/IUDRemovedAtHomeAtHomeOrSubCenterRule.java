package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.CommonFormFields.HOME_FIELD_VALUE;
import static org.opensrp.common.AllConstants.CommonFormFields.SUB_CENTER_SERVICE_PROVIDED_PLACE_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.IUD_REMOVAL_PLACE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IUDRemovedAtHomeAtHomeOrSubCenterRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return SUB_CENTER_SERVICE_PROVIDED_PLACE_VALUE.equalsIgnoreCase(reportFields.get(IUD_REMOVAL_PLACE)) ||
                HOME_FIELD_VALUE.equalsIgnoreCase(reportFields.get(IUD_REMOVAL_PLACE));
    }
}

