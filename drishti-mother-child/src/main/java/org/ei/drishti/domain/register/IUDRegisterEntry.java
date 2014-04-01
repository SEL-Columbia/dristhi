package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

public class IUDRegisterEntry {
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
    private String lmpDate;
    private String uptResult;
    private String wifeEducationLevel;
    private String husbandEducationLevel;
    private Map<String, String> fpDetails;

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

    public Map<String, String> fpDetails() {
        return fpDetails;
    }

    public IUDRegisterEntry withEcNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public IUDRegisterEntry withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public IUDRegisterEntry withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public IUDRegisterEntry withVillage(String village) {
        this.village = village;
        return this;
    }

    public IUDRegisterEntry withSubCenter(String subCenter) {
        this.subCenter = subCenter;
        return this;
    }

    public IUDRegisterEntry withWifeAge(String wifeAge) {
        this.wifeAge = wifeAge;
        return this;
    }

    public IUDRegisterEntry withHusbandAge(String husbandAge) {
        this.husbandAge = husbandAge;
        return this;
    }

    public IUDRegisterEntry withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public IUDRegisterEntry withReligion(String religion) {
        this.religion = religion;
        return this;
    }

    public IUDRegisterEntry withNumberOfLivingMaleChildren(String numberOfLivingMaleChildren) {
        this.numberOfLivingMaleChildren = numberOfLivingMaleChildren;
        return this;
    }

    public IUDRegisterEntry withNumberOfLivingFemaleChildren(String numberOfLivingFemaleChildren) {
        this.numberOfLivingFemaleChildren = numberOfLivingFemaleChildren;
        return this;
    }

    public IUDRegisterEntry withLmpDate(String lmpDate) {
        this.lmpDate = lmpDate;
        return this;
    }

    public IUDRegisterEntry withUptResult(String uptResult) {
        this.uptResult = uptResult;
        return this;
    }

    public IUDRegisterEntry withWifeEducationLevel(String wifeEducationLevel) {
        this.wifeEducationLevel = wifeEducationLevel;
        return this;
    }

    public IUDRegisterEntry withHusbandEducationLevel(String husbandEducationLevel) {
        this.husbandEducationLevel = husbandEducationLevel;
        return this;
    }

    public IUDRegisterEntry withFpDetails(Map<String, String> fpDetails) {
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
