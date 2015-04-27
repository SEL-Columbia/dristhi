package org.opensrp.domain;

import static org.opensrp.common.AllConstants.BOOLEAN_FALSE_VALUE;
import static org.opensrp.common.AllConstants.BOOLEAN_TRUE_VALUE;

import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'MCTSReport'")
public class MCTSReport extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
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
    private String reportSent;

    public MCTSReport() {
    }

    public MCTSReport(String entityId, String reportText, String entityRegistrationDate,
                      String serviceProvidedDate, String sendDate) {

        this(entityId, reportText, entityRegistrationDate, serviceProvidedDate, sendDate, BOOLEAN_FALSE_VALUE);
    }

    public MCTSReport(String entityId, String reportText, String entityRegistrationDate,
                      String serviceProvidedDate, String sendDate, String reportSent) {
        this.entityId = entityId;
        this.reportText = reportText;
        this.entityRegistrationDate = entityRegistrationDate;
        this.serviceProvidedDate = serviceProvidedDate;
        this.sendDate = sendDate;
        this.reportSent = reportSent;
        this.caseId = UUID.randomUUID().toString();
    }

    public MCTSReport withEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public MCTSReport withCaseId(String caseId) {
        this.caseId = caseId;
        return this;
    }

    public MCTSReport withServiceProvidedDate(String serviceProvidedDate) {
        this.serviceProvidedDate = serviceProvidedDate;
        return this;
    }

    public MCTSReport withSendDate(String sendDate) {
        this.sendDate = sendDate;
        return this;
    }

    public MCTSReport withEntityRegistrationDate(String entityRegistrationDate) {
        this.entityRegistrationDate = entityRegistrationDate;
        return this;
    }

    public MCTSReport withReportText(String reportText) {
        this.reportText = reportText;
        return this;
    }

    public MCTSReport withReportSent(String reportSent) {
        this.reportSent = reportSent;
        return this;
    }

    public MCTSReport markReportAsSent() {
        this.reportSent = BOOLEAN_TRUE_VALUE;
        return this;
    }

    public String entityId() {
        return this.entityId;
    }

    public String entityRegistrationDate() {
        return this.entityRegistrationDate;
    }

    public String serviceProvidedDate() {
        return this.serviceProvidedDate;
    }

    public String reportText() {
        return this.reportText;
    }

    public String reportSent() {
        return this.reportSent;
    }

    public String sendDate() {
        return this.sendDate;
    }

    public String getCaseId() {
        return caseId;
    }

    public String caseId() {
        return this.caseId;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "id", "revision", "caseId");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id", "revision", "caseId");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}