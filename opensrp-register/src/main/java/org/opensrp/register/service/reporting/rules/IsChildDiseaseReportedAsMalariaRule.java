package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildIllnessFields.MALARIA_VALUE;
import static org.opensrp.common.AllConstants.ChildIllnessFields.REPORT_CHILD_DISEASE;

@Component
public class IsChildDiseaseReportedAsMalariaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return MALARIA_VALUE.equalsIgnoreCase(reportFields.get(REPORT_CHILD_DISEASE));
    }
}