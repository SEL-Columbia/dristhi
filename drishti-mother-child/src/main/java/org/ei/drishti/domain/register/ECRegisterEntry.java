package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ECRegisterEntry {
    private String registrationDate;
    private String ecNumber;
    private String wifeName;
    private String husbandName;
    private String householdAddress;
    private String householdNumber;
    private String headOfHousehold;
    private String village;
    private String subCenter;
    private String phc;
    private String wifeAge;
    private String husbandAge;
    private String wifeEducationLevel;
    private String husbandEducationLevel;
    private String caste;
    private String religion;
    private String economicStatus;
    private String gravida;
    private String parity;
    private String numberOfLivingChildren;
    private String numberOfStillBirths;
    private String numberOfAbortions;
    private String numberOfLivingMaleChildren;
    private String numberOfLivingFemaleChildren;
    private String youngestChildAge;
    private String currentFPMethod;
    private String currentFPMethodStartDate;
    private String isPregnant;

    public ECRegisterEntry withRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    public ECRegisterEntry withECNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public ECRegisterEntry withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public ECRegisterEntry withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public ECRegisterEntry withWifeAge(String wifeAge) {
        this.wifeAge = wifeAge;
        return this;
    }

    public ECRegisterEntry withHusbandAge(String husbandAge) {
        this.husbandAge = husbandAge;
        return this;
    }

    public ECRegisterEntry withHouseholdAddress(String householdAddress) {
        this.householdAddress = householdAddress;
        return this;
    }

    public ECRegisterEntry withWifeEducationLevel(String wifeEducationLevel) {
        this.wifeEducationLevel = wifeEducationLevel;
        return this;
    }

    public ECRegisterEntry withHouseholdNumber(String householdNumber) {
        this.householdNumber = householdNumber;
        return this;
    }

    public ECRegisterEntry withHeadOfHousehold(String headOfHousehold) {
        this.headOfHousehold = headOfHousehold;
        return this;
    }

    public ECRegisterEntry withSubCenter(String subCenter) {
        this.subCenter = subCenter;
        return this;
    }

    public ECRegisterEntry withPHC(String phc) {
        this.phc = phc;
        return this;
    }

    public ECRegisterEntry withVillage(String village) {
        this.village = village;
        return this;
    }

    public ECRegisterEntry withHusbandEducationLevel(String husbandEducationLevel) {
        this.husbandEducationLevel = husbandEducationLevel;
        return this;
    }

    public ECRegisterEntry withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public ECRegisterEntry withReligion(String religion) {
        this.religion = religion;
        return this;
    }

    public ECRegisterEntry withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public ECRegisterEntry withGravida(String gravida) {
        this.gravida = gravida;
        return this;
    }

    public ECRegisterEntry withParity(String parity) {
        this.parity = parity;
        return this;
    }

    public ECRegisterEntry withNumberOfLivingChildren(String numberOfLivingChildren) {
        this.numberOfLivingChildren = numberOfLivingChildren;
        return this;
    }

    public ECRegisterEntry withNumberOfStillBirths(String numberOfStillBirths) {
        this.numberOfStillBirths = numberOfStillBirths;
        return this;
    }

    public ECRegisterEntry withNumberOfAbortions(String numberOfAbortions) {
        this.numberOfAbortions = numberOfAbortions;
        return this;
    }

    public ECRegisterEntry withNumberOfLivingMaleChildren(String numberOfLivingMaleChildren) {
        this.numberOfLivingMaleChildren = numberOfLivingMaleChildren;
        return this;
    }

    public ECRegisterEntry withNumberOfLivingFemaleChildren(String numberOfLivingFemaleChildren) {
        this.numberOfLivingFemaleChildren = numberOfLivingFemaleChildren;
        return this;
    }

    public ECRegisterEntry withYoungestChildAge(String youngestChildAge) {
        this.youngestChildAge = youngestChildAge;
        return this;
    }

    public ECRegisterEntry withCurrentFPMethod(String fpMethod) {
        this.currentFPMethod = fpMethod;
        return this;
    }

    public ECRegisterEntry withCurrentFPMethodStartDate(String startDate) {
        this.currentFPMethodStartDate = startDate;
        return this;
    }

    public ECRegisterEntry withPregnancyStatus(boolean isPregnant) {
        this.isPregnant = isPregnant ? "yes" : "no";
        return this;
    }

    public String registrationDate() {
        return registrationDate;
    }

    public String ecNumber() {
        return ecNumber;
    }

    public String wifeName() {
        return wifeName;
    }

    public String husbandName() {
        return husbandName;
    }

    public String househouldAddress() {
        return householdAddress;
    }

    public String wifeAge() {
        return wifeAge;
    }

    public String husbandAge() {
        return husbandAge;
    }

    public String wifeEducationLevel() {
        return wifeEducationLevel;
    }

    public String husbandEducationLevel() {
        return husbandEducationLevel;
    }

    public String caste() {
        return caste;
    }

    public String religion() {
        return religion;
    }

    public String economicStatus() {
        return economicStatus;
    }

    public String gravida() {
        return gravida;
    }

    public String parity() {
        return parity;
    }

    public String numberOfLivingChildren() {
        return numberOfLivingChildren;
    }

    public String numberOfStillBirths() {
        return numberOfStillBirths;
    }

    public String numberOfAbortions() {
        return numberOfAbortions;
    }

    public String numberOfLivingMaleChildren() {
        return numberOfLivingMaleChildren;
    }

    public String numberOfLivingFemaleChildren() {
        return numberOfLivingFemaleChildren;
    }

    public String youngestChildAge() {
        return youngestChildAge;
    }

    public String currentFPMethod() {
        return currentFPMethod;
    }

    public String currentFPMethodStartDate() {
        return currentFPMethodStartDate;
    }

    public String isPregnant() {
        return isPregnant;
    }

    public String householdNumber() {
        return householdNumber;
    }

    public String headOfHousehold() {
        return headOfHousehold;
    }

    public String village() {
        return village;
    }

    public String subCenter() {
        return subCenter;
    }

    public String phc() {
        return phc;
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
