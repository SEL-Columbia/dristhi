package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildIllnessFields.MALARIA_VALUE;
import static org.ei.drishti.common.AllConstants.ChildIllnessFields.REPORT_CHILD_DISEASE;

@Component
public class IsChildDiseaseReportedAsMalariaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return MALARIA_VALUE.equalsIgnoreCase(reportFields.get(REPORT_CHILD_DISEASE));
    }
}