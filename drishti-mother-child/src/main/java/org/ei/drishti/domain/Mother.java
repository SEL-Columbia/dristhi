package org.ei.drishti.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;

import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'Mother'")
public class Mother extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String ecCaseId;
    @JsonProperty
    private String thayiCardNumber;
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String referenceDate;
    @JsonProperty
    private String village;
    @JsonProperty
    private String subCenter;
    @JsonProperty
    private String phc;
    @JsonProperty
    private boolean isClosed;
    @JsonProperty
    private Map<String, String> details;

    private Mother() {
    }

    public Mother(String caseId, String ecCaseId, String thayiCardNumber) {
        this.caseId = caseId;
        this.ecCaseId = ecCaseId;
        this.thayiCardNumber = thayiCardNumber;
        this.details = new HashMap<>();
    }

    public Mother withAnm(String identifier) {
        anmIdentifier = identifier;
        return this;
    }

    public Mother withLMP(LocalDate lmp) {
        this.referenceDate = lmp.toString();
        return this;
    }

    public Mother withLocation(String village, String subCenter, String phc) {
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        return this;
    }

    public Mother withDeliveryOutCome(String dateOfDelivery) {
        this.referenceDate = dateOfDelivery;
        return this;
    }

    public Mother withDetails(Map<String, String> details) {
        this.details = details;
        return this;
    }

    public String caseId() {
        return caseId;
    }

    public String ecCaseId() {
        return ecCaseId;
    }

    public String thayiCardNo() {
        return thayiCardNumber;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String village() {
        return village;
    }

    public String phc() {
        return phc;
    }

    public String subCenter() {
        return subCenter;
    }

    public Map<String, String> details() {
        return details;
    }

    public LocalDate dateOfDelivery() {
        return LocalDate.parse(referenceDate);
    }

    public LocalDate lmp() {
        return LocalDate.parse(referenceDate);
    }

    public Mother setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
        return this;
    }

    private String getCaseId() {
        return caseId;
    }

    private String getEcCaseId() {
        return ecCaseId;
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
