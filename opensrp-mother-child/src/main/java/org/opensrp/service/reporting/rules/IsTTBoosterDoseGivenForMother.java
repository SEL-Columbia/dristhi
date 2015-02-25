package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCFormFields.TT_BOOSTER_DOSE_VALUE;
import static org.opensrp.common.AllConstants.ANCFormFields.TT_DOSE_FIELD;

@Component
public class IsTTBoosterDoseGivenForMother implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return TT_BOOSTER_DOSE_VALUE.equals(reportFields.get(TT_DOSE_FIELD));
    }
}

