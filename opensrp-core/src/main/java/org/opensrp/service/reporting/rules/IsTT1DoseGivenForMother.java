package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCFormFields.TT1_DOSE_VALUE;
import static org.opensrp.common.AllConstants.ANCFormFields.TT_DOSE_FIELD;

@Component
public class IsTT1DoseGivenForMother implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return TT1_DOSE_VALUE.equals(reportFields.get(TT_DOSE_FIELD));
    }
}

