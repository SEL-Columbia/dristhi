package org.ei.drishti.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Child'")
public class Child extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String thaayiCardNumber;
    @JsonProperty
    private String name;
    @JsonProperty
    private String village;
    @JsonProperty
    private String anmIdentifier;

    private Child() {
    }

    public Child(String caseId, String thaayiCardNumber, String name, String village) {
        this.caseId = caseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.name = name;
        this.village = village;
    }

    public Child withAnm(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
        return this;
    }

    private String getCaseId() {
        return caseId;
    }

    public String caseId() {
        return caseId;
    }

    public String thaayiCardNumber() {
        return thaayiCardNumber;
    }

    public String name() {
        return name;
    }

    public String village() {
        return village;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Child child = (Child) o;

        if (!caseId.equals(child.caseId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return caseId.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
