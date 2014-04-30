package org.ei.drishti.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'MCTSReport'")
public class MCTSReport extends MotechBaseDataObject {
    @JsonProperty
    private String entityId;
    @JsonProperty
    private String serviceProvidedDate;
    @JsonProperty
    private String sendDate;
    @JsonProperty
    private String entityRegistrationDate;
    @JsonProperty
    private String reportText;
    @JsonProperty
    private boolean reportSent;

    private MCTSReport() {
    }

    public MCTSReport(String entityId, String reportText, String entityRegistrationDate,
                      String serviceProvidedDate, String sendDate) {
        this(entityId, reportText, entityRegistrationDate, serviceProvidedDate, sendDate, false);
    }

    public MCTSReport(String entityId, String reportText, String entityRegistrationDate,
                      String serviceProvidedDate, String sendDate, boolean reportSent) {
        this.entityId = entityId;
        this.reportText = reportText;
        this.entityRegistrationDate = entityRegistrationDate;
        this.serviceProvidedDate = serviceProvidedDate;
        this.sendDate = sendDate;
        this.reportSent = reportSent;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "id", "revision");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id", "revision");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}