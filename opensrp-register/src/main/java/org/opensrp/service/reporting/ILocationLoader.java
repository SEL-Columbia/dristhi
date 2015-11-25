package org.opensrp.service.reporting;

import org.opensrp.common.domain.Location;

public interface ILocationLoader {
    public Location loadLocationFor(String bindType, String entityId);
}