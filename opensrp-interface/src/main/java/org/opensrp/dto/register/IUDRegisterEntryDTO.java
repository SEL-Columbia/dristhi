package org.opensrp.dto.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class IUDRegisterEntryDTO {
    @JsonProperty
    private String ecNumber;
    @JsonProperty
    private String wifeName;
    @JsonProperty
    private String husbandName;
    @JsonProperty
    private String village;
    @JsonProperty
    private String subCenter;
    @JsonProperty
    private String wifeAge;
    @JsonProperty
    private String husbandAge;
    @JsonProperty
    private String caste;
    @JsonProperty
    private String religion;
    @JsonProperty
    private String numberOfLivingMaleChildren;
    @JsonProperty
    private String numberOfLivingFemaleChildren;
    @JsonProperty
    private String lmpDate;
    @JsonProperty
    private String uptResult;
    @JsonProperty
    private String wifeEducationLevel;
    @JsonProperty
    private String husbandEducationLevel;
    @JsonProperty
    private IUDFPDetailsDTO fpDetails;

    public String ecNumber() {
        return ecNumber;
    }

    public String wifeName() {
        return wifeName;
    }

    public String husbandName() {
        return husbandName;
    }

    public String village() {
        return village;
    }

    public String subCenter() {
        return subCenter;
    }

    public String wifeAge() {
        return wifeAge;
    }

    public String husbandAge() {
        return husbandAge;
    }

    public String caste() {
        return caste;
    }

    public String religion() {
        return religion;
    }

    public String numberOfLivingMaleChildren() {
        return numberOfLivingMaleChildren;
    }

    public String numberOfLivingFemaleChildren() {
        return numberOfLivingFemaleChildren;
    }

    public String lmpDate() {
        return lmpDate;
    }

    public String uptResult() {
        return uptResult;
    }

    public String wifeEducationLevel() {
        return wifeEducationLevel;
    }

    public String husbandEducationLevel() {
        return husbandEducationLevel;
    }

    public IUDFPDetailsDTO fpDetails() {
        return fpDetails;
    }

    public IUDRegisterEntryDTO withEcNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public IUDRegisterEntryDTO withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public IUDRegisterEntryDTO withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public IUDRegisterEntryDTO withVillage(String village) {
        this.village = village;
        return this;
    }

    public IUDRegisterEntryDTO withSubCenter(String subCenter) {
        this.subCenter = subCenter;
        return this;
    }

    public IUDRegisterEntryDTO withWifeAge(String wifeAge) {
        this.wifeAge = wifeAge;
        return this;
    }

    public IUDRegisterEntryDTO withHusbandAge(String husbandAge) {
        this.husbandAge = husbandAge;
        return this;
    }

    public IUDRegisterEntryDTO withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public IUDRegisterEntryDTO withReligion(String religion) {
        this.religion = religion;
        return this;
    }

    public IUDRegisterEntryDTO withNumberOfLivingMaleChildren(String numberOfLivingMaleChildren) {
        this.numberOfLivingMaleChildren = numberOfLivingMaleChildren;
        return this;
    }

    public IUDRegisterEntryDTO withNumberOfLivingFemaleChildren(String numberOfLivingFemaleChildren) {
        this.numberOfLivingFemaleChildren = numberOfLivingFemaleChildren;
        return this;
    }

    public IUDRegisterEntryDTO withLmpDate(String lmpDate) {
        this.lmpDate = lmpDate;
        return this;
    }

    public IUDRegisterEntryDTO withUptResult(String uptResult) {
        this.uptResult = uptResult;
        return this;
    }

    public IUDRegisterEntryDTO withWifeEducationLevel(String wifeEducationLevel) {
        this.wifeEducationLevel = wifeEducationLevel;
        return this;
    }

    public IUDRegisterEntryDTO withHusbandEducationLevel(String husbandEducationLevel) {
        this.husbandEducationLevel = husbandEducationLevel;
        return this;
    }

    public IUDRegisterEntryDTO withFpDetails(IUDFPDetailsDTO fpDetails) {
        this.fpDetails = fpDetails;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
