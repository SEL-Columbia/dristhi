package org.ei.drishti.service.reporting;

import org.ei.drishti.domain.Location;
import org.ei.drishti.util.SafeMap;

public interface IReporter {
    public void report(String entityId, String reportIndicator, Location location, SafeMap reportData);
}

