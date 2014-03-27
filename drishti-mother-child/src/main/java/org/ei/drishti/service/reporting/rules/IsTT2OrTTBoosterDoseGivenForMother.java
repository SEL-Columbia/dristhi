package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCFormFields.TT2_DOSE_VALUE;
import static org.ei.drishti.common.AllConstants.ANCFormFields.TT_BOOSTER_DOSE_VALUE;
import static org.ei.drishti.common.AllConstants.ANCFormFields.TT_DOSE_FIELD;

@Component
public class IsTT2OrTTBoosterDoseGivenForMother implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return TT2_DOSE_VALUE.equals(reportFields.get(TT_DOSE_FIELD))
                || TT_BOOSTER_DOSE_VALUE.equals(reportFields.get(TT_DOSE_FIELD));
    }
}

