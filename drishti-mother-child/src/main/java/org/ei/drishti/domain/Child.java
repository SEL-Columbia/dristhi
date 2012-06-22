package org.ei.drishti.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.List;

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
    private String subCenter;
    @JsonProperty
    private String phc;
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private List<String> immunizationsProvided;

    private Child() {
    }

    public Child(String caseId, String thaayiCardNumber, String name, List<String> immunizationsProvided) {
        this.caseId = caseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.name = name;
        this.immunizationsProvided = immunizationsProvided;
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

    public String phc() {
        return phc;
    }

    public String subCenter() {
        return subCenter;
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

    public List<String> immunizationsProvided() {
        return immunizationsProvided;
    }

    public Child withLocation(String village, String subCenter, String phc) {
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        return this;
    }

    public Location location() {
        return new Location(village, subCenter, phc);
    }
}
