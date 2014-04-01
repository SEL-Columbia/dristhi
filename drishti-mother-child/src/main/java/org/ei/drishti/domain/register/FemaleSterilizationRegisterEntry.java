package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FemaleSterilizationRegisterEntry {
    private String ecNumber;
    private String wifeName;
    private String husbandName;
    private String village;
    private String subCenter;
    private String wifeAge;
    private String husbandAge;
    private String caste;
    private String religion;
    private String numberOfLivingMaleChildren;
    private String numberOfLivingFemaleChildren;
    private String wifeEducationLevel;
    private String husbandEducationLevel;
    private SterilizationFPDetails fpDetails;

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

    public String wifeEducationLevel() {
        return wifeEducationLevel;
    }

    public String husbandEducationLevel() {
        return husbandEducationLevel;
    }

    public SterilizationFPDetails fpDetails() {
        return fpDetails;
    }

    public FemaleSterilizationRegisterEntry withEcNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public FemaleSterilizationRegisterEntry withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public FemaleSterilizationRegisterEntry withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public FemaleSterilizationRegisterEntry withVillage(String village) {
        this.village = village;
        return this;
    }

    public FemaleSterilizationRegisterEntry withSubCenter(String subCenter) {
        this.subCenter = subCenter;
        return this;
    }

    public FemaleSterilizationRegisterEntry withWifeAge(String wifeAge) {
        this.wifeAge = wifeAge;
        return this;
    }

    public FemaleSterilizationRegisterEntry withHusbandAge(String husbandAge) {
        this.husbandAge = husbandAge;
        return this;
    }

    public FemaleSterilizationRegisterEntry withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public FemaleSterilizationRegisterEntry withReligion(String religion) {
        this.religion = religion;
        return this;
    }

    public FemaleSterilizationRegisterEntry withNumberOfLivingMaleChildren(String numberOfLivingMaleChildren) {
        this.numberOfLivingMaleChildren = numberOfLivingMaleChildren;
        return this;
    }

    public FemaleSterilizationRegisterEntry withNumberOfLivingFemaleChildren(String numberOfLivingFemaleChildren) {
        this.numberOfLivingFemaleChildren = numberOfLivingFemaleChildren;
        return this;
    }

    public FemaleSterilizationRegisterEntry withWifeEducationLevel(String wifeEducationLevel) {
        this.wifeEducationLevel = wifeEducationLevel;
        return this;
    }

    public FemaleSterilizationRegisterEntry withHusbandEducationLevel(String husbandEducationLevel) {
        this.husbandEducationLevel = husbandEducationLevel;
        return this;
    }

    public FemaleSterilizationRegisterEntry withFpDetails(SterilizationFPDetails fpDetails) {
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
