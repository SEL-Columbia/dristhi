package org.ei.drishti.service.reporting;

import org.ei.drishti.domain.Location;
import org.ei.drishti.form.domain.FormSubmission;

public interface IReporter {
    public void report(FormSubmission submission, String reportIndicator, Location location);
}

