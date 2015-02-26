package org.opensrp.service.reporting;

import org.opensrp.domain.Location;

public interface ILocationLoader {
    public Location loadLocationFor(String bindType, String entityId);
}