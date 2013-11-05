package org.ei.drishti.common.domain;

import org.ei.drishti.domain.Location;

public class ServiceProvidedReportDTO {

    private String id;
    private String serviceProviderType;
    private String indicator;
    private String date;
    private Location location;

    public ServiceProvidedReportDTO(String id, String serviceProviderType, String indicator, String date, Location location) {
        this.id = id;
        this.serviceProviderType = serviceProviderType;
        this.indicator = indicator;
        this.date = date;
        this.location = location;
    }
}
