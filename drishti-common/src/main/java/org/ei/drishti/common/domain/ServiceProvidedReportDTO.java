package org.ei.drishti.common.domain;

public class ServiceProvidedReportDTO {

    private String id;
    private String serviceProviderType;
    private String indicator;
    private String date;
    private final String village;
    private final String subCenter;
    private final String phc;

    public ServiceProvidedReportDTO(String id, String serviceProviderType, String indicator, String date, String village, String subCenter, String phc) {
        this.id = id;
        this.serviceProviderType = serviceProviderType;
        this.indicator = indicator;
        this.date = date;
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
    }
}
