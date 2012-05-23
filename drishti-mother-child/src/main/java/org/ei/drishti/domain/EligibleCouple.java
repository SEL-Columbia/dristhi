package org.ei.drishti.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'EligibleCouple'")
public class EligibleCouple extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String ecNumber;
    @JsonProperty
    private String wife;
    @JsonProperty
    private String husband;
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String village;
    @JsonProperty
    private String subCenter;

    public EligibleCouple() {
    }

    public EligibleCouple(String caseId, String ecNumber) {
        this.caseId = caseId;
        this.ecNumber = ecNumber;
    }

    public EligibleCouple withCouple(String wife, String husband) {
        this.wife = wife;
        this.husband = husband;
        return this;
    }

    public EligibleCouple withANMIdentifier(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
        return this;
    }

    public EligibleCouple withLocation(String village, String subCenter) {
        this.village = village;
        this.subCenter = subCenter;
        return this;
    }

    public String wife() {
        return wife;
    }

    private String getCaseId() {
        return caseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EligibleCouple that = (EligibleCouple) o;

        if (caseId != null ? !caseId.equals(that.caseId) : that.caseId != null) return false;
        if (ecNumber != null ? !ecNumber.equals(that.ecNumber) : that.ecNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = caseId != null ? caseId.hashCode() : 0;
        result = 31 * result + (ecNumber != null ? ecNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
