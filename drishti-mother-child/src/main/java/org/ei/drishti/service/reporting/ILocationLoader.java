package org.ei.drishti.service.reporting;

import org.ei.drishti.domain.Location;

public interface ILocationLoader {
    public Location loadLocationFor(String bindType, String entityId);
}