package org.opensrp.service.reporting.rules;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.service.reporting.ReferenceData;
import org.opensrp.util.SafeMap;

public interface IReferenceDataRepository {
    SafeMap getReferenceData(FormSubmission submission, ReferenceData referenceData);
}

