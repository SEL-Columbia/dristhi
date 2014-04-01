package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class OCPRegisterEntry {
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
    private String lmpDate;
    private String uptResult;
    private String wifeEducationLevel;
    private String husbandEducationLevel;
    private FPDetails FPDetails;

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

    public FPDetails fpDetails() {
        return FPDetails;
    }

    public OCPRegisterEntry withEcNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public OCPRegisterEntry withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public OCPRegisterEntry withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public OCPRegisterEntry withVillage(String village) {
        this.village = village;
        return this;
    }

    public OCPRegisterEntry withSubCenter(String subCenter) {
        this.subCenter = subCenter;
        return this;
    }

    public OCPRegisterEntry withWifeAge(String wifeAge) {
        this.wifeAge = wifeAge;
        return this;
    }

    public OCPRegisterEntry withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public OCPRegisterEntry withReligion(String religion) {
        this.religion = religion;
        return this;
    }

    public OCPRegisterEntry withNumberOfLivingMaleChildren(String numberOfLivingMaleChildren) {
        this.numberOfLivingMaleChildren = numberOfLivingMaleChildren;
        return this;
    }

    public OCPRegisterEntry withNumberOfLivingFemaleChildren(String numberOfLivingFemaleChildren) {
        this.numberOfLivingFemaleChildren = numberOfLivingFemaleChildren;
        return this;
    }

    public OCPRegisterEntry withLmpDate(String lmpDate) {
        this.lmpDate = lmpDate;
        return this;
    }

    public OCPRegisterEntry withUptResult(String uptResult) {
        this.uptResult = uptResult;
        return this;
    }

    public OCPRegisterEntry withWifeEducationLevel(String wifeEducationLevel) {
        this.wifeEducationLevel = wifeEducationLevel;
        return this;
    }

    public OCPRegisterEntry withHusbandEducationLevel(String husbandEducationLevel) {
        this.husbandEducationLevel = husbandEducationLevel;
        return this;
    }

    public OCPRegisterEntry withFpDetails(FPDetails FPDetails) {
        this.FPDetails = FPDetails;
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
