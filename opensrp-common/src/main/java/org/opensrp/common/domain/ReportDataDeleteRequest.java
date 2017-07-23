package org.opensrp.common.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.http.annotation.Immutable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.opensrp.common.AllConstants;

import java.io.Serializable;

import static org.opensrp.common.AllConstants.ReportDataParameters.ANM_REPORT_DATA_TYPE;
import static org.opensrp.common.AllConstants.ReportDataParameters.SERVICE_PROVIDED_DATA_TYPE;

@Immutable
public class ReportDataDeleteRequest implements Serializable {

    @JsonProperty
    private String type;
    @JsonProperty
    private String dristhiEntityId;

    public ReportDataDeleteRequest() {
    }

    public ReportDataDeleteRequest(String type) {
        this.type = type;
    }

    private static ReportDataDeleteRequest buildReportDataRequest(String type, String dristhiEntityId) {
        return new ReportDataDeleteRequest()
                .withType(type)
                .withDristhiEntityId(dristhiEntityId);
    }

    public static ReportDataDeleteRequest serviceProvidedDataDeleteRequest(String dristhiEntityId) {
        return buildReportDataRequest(SERVICE_PROVIDED_DATA_TYPE, dristhiEntityId);
    }

    public static ReportDataDeleteRequest anmReportDataDeleteRequest(String dristhiEntityId) {
        return buildReportDataRequest(ANM_REPORT_DATA_TYPE, dristhiEntityId);
    }

    public String type() {
        return this.type;
    }

    public String dristhiEntityId() {
        return this.dristhiEntityId;
    }

    public ReportDataDeleteRequest withType(String type) {
        this.type = type;
        return this;
    }


    public ReportDataDeleteRequest withDristhiEntityId(String dristhiEntityId) {
        this.dristhiEntityId = dristhiEntityId;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public final boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public final int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
