package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.reporting.ReferenceData;
import org.ei.drishti.util.SafeMap;

public interface IReferenceDataRepository {
    SafeMap getReferenceData(FormSubmission submission, ReferenceData referenceData);
}

