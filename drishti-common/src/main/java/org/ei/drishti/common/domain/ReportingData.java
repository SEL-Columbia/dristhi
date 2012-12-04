package org.ei.drishti.common.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.drishti.domain.Location;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.Report.ANM_REPORT_DATA_TYPE;
import static org.ei.drishti.common.AllConstants.Report.SERVICE_PROVIDED_DATA_TYPE;

public class ReportingData implements Serializable {
    private static final long serialVersionUID = 454645765753L;

    @JsonProperty
    private String type;
    @JsonProperty
    private Map<String, String> data;

    public static ReportingData serviceProvidedData(String anmIdentifier, String externalId, Indicator indicator, String date, Location location) {
        return new ReportingData(SERVICE_PROVIDED_DATA_TYPE).with("anmIdentifier", anmIdentifier).with("indicator", indicator.value())
                .with("externalId", externalId).with("village", location.village()).with("subCenter", location.subCenter()).with("phc", location.phc()).with("date", date).with("serviceProviderType", "ANM");
    }

    public static ReportingData anmReportData(String anmIdentifier, String externalId, Indicator indicator, String date) {
        return new ReportingData(ANM_REPORT_DATA_TYPE).with("anmIdentifier", anmIdentifier).with("indicator", indicator.value())
                .with("externalId", externalId).with("date", date);
    }

    private ReportingData() {
    }

    public ReportingData(String type) {
        this.type = type;
        data = new HashMap<>();
    }

    public ReportingData(String type, Map<String, String> data) {
        this.type = type;
        this.data = data;
    }

    public ReportingData with(String key, String value) {
        data.put(key, value);
        return this;
    }

    public String get(String key) {
        return data.get(key);
    }

    public String type() {
        return type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
