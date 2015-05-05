package org.opensrp.register.service.reporting.rules;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.PNCVisitFormFields.BAD_SMELL_LOCHEA_VALUE;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VAGINAL_PROBLEMS_FIELD_NAME;

@Component
public class IsMotherHasBadSmellLocheaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return StringUtils.contains(reportFields.get(VAGINAL_PROBLEMS_FIELD_NAME), BAD_SMELL_LOCHEA_VALUE);
    }
}
