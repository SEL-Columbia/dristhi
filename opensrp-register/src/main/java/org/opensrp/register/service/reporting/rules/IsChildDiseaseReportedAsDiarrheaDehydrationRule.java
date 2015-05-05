package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildIllnessFields.DIARRHEA_DEHYDRATION_VALUE;
import static org.opensrp.common.AllConstants.ChildIllnessFields.REPORT_CHILD_DISEASE;

@Component
public class IsChildDiseaseReportedAsDiarrheaDehydrationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return DIARRHEA_DEHYDRATION_VALUE.equalsIgnoreCase(reportFields.get(REPORT_CHILD_DISEASE));
    }
}