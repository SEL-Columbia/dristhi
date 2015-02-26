package org.opensrp.service.reporting;

import org.opensrp.domain.Location;
import org.opensrp.util.SafeMap;

public interface IReporter {
    public void report(String entityId, String reportIndicator,
                       Location location, String serviceProvidedDate,
                       SafeMap reportData);
}

