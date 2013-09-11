package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.reporting.ReferenceData;

import java.util.List;

public interface IRule {
    public boolean apply(FormSubmission submission, List<String> formFields, ReferenceData referenceData);
}

