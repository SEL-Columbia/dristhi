package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.PERMANENT_RELOCATION_VALUE;

@Component
public class RelocationIsPermanentRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return PERMANENT_RELOCATION_VALUE.equalsIgnoreCase(reportFields.get(CLOSE_REASON_FIELD_NAME));
    }
}
