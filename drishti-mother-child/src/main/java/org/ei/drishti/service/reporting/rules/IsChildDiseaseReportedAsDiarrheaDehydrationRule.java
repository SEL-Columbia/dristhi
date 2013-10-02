package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildIllnessFields.DIARRHEA_DEHYDRATION_VALUE;
import static org.ei.drishti.common.AllConstants.ChildIllnessFields.REPORT_CHILD_DISEASE;

@Component
public class IsChildDiseaseReportedAsDiarrheaDehydrationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return DIARRHEA_DEHYDRATION_VALUE.equalsIgnoreCase(reportFields.get(REPORT_CHILD_DISEASE));
    }
}