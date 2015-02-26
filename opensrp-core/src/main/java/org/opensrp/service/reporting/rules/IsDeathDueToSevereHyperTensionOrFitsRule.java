package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ECCloseFields.HYPERTENSION_FITS_VALUE;
import static org.opensrp.common.AllConstants.ECCloseFields.MATERNAL_DEATH_CAUSE_FIELD_NAME;

@Component
public class IsDeathDueToSevereHyperTensionOrFitsRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return HYPERTENSION_FITS_VALUE.equalsIgnoreCase(reportFields.get(MATERNAL_DEATH_CAUSE_FIELD_NAME));
    }
}

