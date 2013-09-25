package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.CommonFormFields.SERVICE_PROVIDED_PLACE;
import static org.ei.drishti.common.AllConstants.Report.SUB_CENTER_SERVICE_PROVIDED_PLACE;

@Component
public class ServiceProvidedAtSubCenterRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        return reportFields.has(SERVICE_PROVIDED_PLACE)
                && SUB_CENTER_SERVICE_PROVIDED_PLACE.equalsIgnoreCase(reportFields.get(SERVICE_PROVIDED_PLACE));
    }
}
