package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.VitaminAFields.VITAMIN_A_DOSE;
import static org.opensrp.common.AllConstants.VitaminAFields.VITAMIN_A_DOSE_4_VALUE;

@Component
public class IsVitaminA4DoseGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return VITAMIN_A_DOSE_4_VALUE.equalsIgnoreCase(reportFields.get(VITAMIN_A_DOSE));
    }
}
