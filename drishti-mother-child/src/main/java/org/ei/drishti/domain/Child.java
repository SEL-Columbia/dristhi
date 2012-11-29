package org.ei.drishti.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.List;
import java.util.Map;

@TypeDiscriminator("doc.type === 'Child'")
public class Child extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String ecCaseId;
    @JsonProperty
    private String motherCaseId;
    @JsonProperty
    private String thaayiCardNumber;
    @JsonProperty
    private String name;
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private List<String> immunizationsProvided;
    @JsonProperty
    private String gender;
    @JsonProperty
    private String dateOfBirth;
    @JsonProperty
    private Map<String, String> details;

    private Child() {
    }

    public Child(String caseId, String ecCaseId, String motherCaseId, String thaayiCardNumber, String name, List<String> immunizationsProvided, String gender) {
        this.caseId = caseId;
        this.ecCaseId = ecCaseId;
        this.motherCaseId = motherCaseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.name = name;
        this.immunizationsProvided = immunizationsProvided;
        this.gender = gender;
    }

    public Child withAnm(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
        return this;
    }

    public String caseId() {
        return caseId;
    }

    public String ecCaseId() {
        return ecCaseId;
    }

    public String thaayiCardNumber() {
        return thaayiCardNumber;
    }

    public String name() {
        return name;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public List<String> immunizationsProvided() {
        return immunizationsProvided;
    }

    public Child withDetails(Map<String, String> details) {
        this.details = details;
        return this;
    }

    public Map<String, String> details() {
        return details;
    }

    public String dateOfBirth() {
        return dateOfBirth;
    }

    //These getters are used by CouchDB Repo
    private String getCaseId() {
        return caseId;
    }

    private String getMotherCaseId() {
        return motherCaseId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this, false, getClass());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(17, 37, this, false, getClass());
    }

    public Child withDateOfBirth(String dob) {
        this.dateOfBirth = dob;
        return this;
    }
}
