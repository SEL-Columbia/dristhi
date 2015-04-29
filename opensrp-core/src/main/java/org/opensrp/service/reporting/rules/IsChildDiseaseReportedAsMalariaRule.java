package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildIllnessFields.MALARIA_VALUE;
import static org.opensrp.common.AllConstants.ChildIllnessFields.REPORT_CHILD_DISEASE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsChildDiseaseReportedAsMalariaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return MALARIA_VALUE.equalsIgnoreCase(reportFields.get(REPORT_CHILD_DISEASE));
    }
}