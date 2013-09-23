package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.PERMANENT_RELOCATION_VALUE;

@Component
public class RelocationIsPermanentRule implements IRule {

    @Override
    public boolean apply(SafeMap safeMap) {
        String closeReason = safeMap.get(CLOSE_REASON_FIELD_NAME);

        return closeReason.equalsIgnoreCase(PERMANENT_RELOCATION_VALUE);
    }
}
