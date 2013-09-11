package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.domain.Child;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.service.reporting.ReferenceData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.PERMANENT_RELOCATION_VALUE;

public class RelocationIsPermanentRule implements IRule {

    @Autowired
    private AllChildren allChildren;

    public RelocationIsPermanentRule(AllChildren allChildren) {
        this.allChildren = allChildren;
    }

    @Override
    public boolean apply(FormSubmission submission, List<String> formFields, ReferenceData referenceData) {
        Child child = allChildren.findByCaseId(submission.entityId());
        String closeReason = submission.getField(CLOSE_REASON_FIELD_NAME);

        return closeReason.equalsIgnoreCase(PERMANENT_RELOCATION_VALUE);
    }
}
