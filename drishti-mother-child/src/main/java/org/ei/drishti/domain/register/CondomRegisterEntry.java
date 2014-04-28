package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CondomRegisterEntry {
    private String ecNumber;
    private String wifeName;
    private String husbandName;
    private String village;
    private String subCenter;
    private String wifeAge;
    private String caste;
    private String religion;
    private String numberOfLivingMaleChildren;
    private String numberOfLivingFemaleChildren;
    private String wifeEducationLevel;
    private String husbandEducationLevel;
    private CondomFPDetails condomFPDetails;

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

    public CondomFPDetails fpDetails() {
        return condomFPDetails;
    }

    public CondomRegisterEntry withEcNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public CondomRegisterEntry withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public CondomRegisterEntry withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public CondomRegisterEntry withVillage(String village) {
        this.village = village;
        return this;
    }

    public CondomRegisterEntry withSubCenter(String subCenter) {
        this.subCenter = subCenter;
        return this;
    }

    public CondomRegisterEntry withWifeAge(String wifeAge) {
        this.wifeAge = wifeAge;
        return this;
    }

    public CondomRegisterEntry withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public CondomRegisterEntry withReligion(String religion) {
        this.religion = religion;
        return this;
    }

    public CondomRegisterEntry withNumberOfLivingMaleChildren(String numberOfLivingMaleChildren) {
        this.numberOfLivingMaleChildren = numberOfLivingMaleChildren;
        return this;
    }

    public CondomRegisterEntry withNumberOfLivingFemaleChildren(String numberOfLivingFemaleChildren) {
        this.numberOfLivingFemaleChildren = numberOfLivingFemaleChildren;
        return this;
    }

    public CondomRegisterEntry withWifeEducationLevel(String wifeEducationLevel) {
        this.wifeEducationLevel = wifeEducationLevel;
        return this;
    }

    public CondomRegisterEntry withHusbandEducationLevel(String husbandEducationLevel) {
        this.husbandEducationLevel = husbandEducationLevel;
        return this;
    }

    public CondomRegisterEntry withFpDetails(CondomFPDetails condomFPDetails) {
        this.condomFPDetails = condomFPDetails;
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
