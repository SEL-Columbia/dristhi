package org.ei.drishti.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Map;

@TypeDiscriminator("doc.type === 'Mother'")
public class Mother extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String thaayiCardNumber;
    @JsonProperty
    private String name;
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String anmPhoneNumber;
    @JsonProperty
    private LocalDate lmp;
    @JsonProperty
    private String ecNumber;
    @JsonProperty
    private String village;
    @JsonProperty
    private String subCenter;
    @JsonProperty
    private String phc;
    @JsonProperty
    private boolean isHighRisk;
    @JsonProperty
    private String deliveryPlace;
    @JsonProperty
    private Map<String, String> details;

    private Mother() {
    }

    public Mother(String caseId, String thaayiCardNumber, String name) {
        this.caseId = caseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public Mother withAnm(String identifier, String phoneNumber) {
        anmIdentifier = identifier;
        this.anmPhoneNumber = phoneNumber;
        return this;
    }

    public Mother withLMP(LocalDate lmp) {
        this.lmp = lmp;
        return this;
    }

    public Mother withECNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public Mother withLocation(String village, String subCenter, String phc) {
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        return this;
    }

    public Mother withFacility(String deliveryPlace) {
        this.deliveryPlace = deliveryPlace;
        return this;
    }

    public Mother withDetails(Map<String, String> details) {
        this.details = details;
        return this;
    }

    public Mother isHighRisk(boolean value) {
        this.isHighRisk = value;
        return this;
    }

    public String caseId() {
        return caseId;
    }

    public String thaayiCardNo() {
        return thaayiCardNumber;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String anmPhoneNo() {
        return anmPhoneNumber;
    }

    public String village() {
        return village;
    }

    public String ecNumber() {
        return ecNumber;
    }

    public String phc() {
        return phc;
    }

    public String subCenter() {
        return subCenter;
    }

    private String getCaseId() {
        return caseId;
    }

    private String getThaayiCardNumber() {
        return thaayiCardNumber;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(0, this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
