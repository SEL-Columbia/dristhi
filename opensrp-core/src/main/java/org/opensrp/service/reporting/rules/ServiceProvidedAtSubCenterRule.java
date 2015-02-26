package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.CommonFormFields.SERVICE_PROVIDED_PLACE;
import static org.opensrp.common.AllConstants.CommonFormFields.SUBCENTER_SERVICE_PROVIDED_PLACE_VALUE;

@Component
public class ServiceProvidedAtSubCenterRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        return reportFields.has(SERVICE_PROVIDED_PLACE)
                && SUBCENTER_SERVICE_PROVIDED_PLACE_VALUE.equalsIgnoreCase(reportFields.get(SERVICE_PROVIDED_PLACE));
    }
}
