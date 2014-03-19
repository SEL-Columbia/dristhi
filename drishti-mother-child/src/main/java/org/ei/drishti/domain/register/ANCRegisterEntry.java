package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

public class ANCRegisterEntry {
    private String ancNumber;
    private String registrationDate;
    private String ecNumber;
    private String thayiCardNumber;
    private String aadharCardNumber;
    private String wifeName;
    private String husbandName;
    private String address;
    private LocalDate wifeDOB;
    private String phoneNumber;
    private String wifeEducationLevel;
    private String husbandEducationLevel;
    private String caste;
    private String religion;
    private String economicStatus;
    private String bplCardNumber;
    private String jsyBeneficiary;
    private String gravida;
    private String parity;
    private String numberOfLivingChildren;
    private String numberOfStillBirths;
    private String numberOfAbortions;
    private String youngestChildDOB;
    private String lmp;
    private String edd;
    private String height;
    private String bloodGroup;
    private String isHRP;
    private List<Map<String, String>> ancVisits;
    private List<Map<String, String>> ifaTablets;
    private List<Map<String, String>> ttDoses;
    private List<Map<String, String>> hbTests;

    public ANCRegisterEntry withANCNumber(String ancNumber) {
        this.ancNumber = ancNumber;
        return this;
    }

    public ANCRegisterEntry withRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    public ANCRegisterEntry withECNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }


    public ANCRegisterEntry withThayiCardNumber(String thayiCardNumber) {
        this.thayiCardNumber = thayiCardNumber;
        return this;
    }


    public ANCRegisterEntry withAadharCardNumber(String aadharCardNumber) {
        this.aadharCardNumber = aadharCardNumber;
        return this;
    }


    public ANCRegisterEntry withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public ANCRegisterEntry withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public ANCRegisterEntry withAddress(String address) {
        this.address = address;
        return this;
    }

    public ANCRegisterEntry withWifeDOB(LocalDate wifeDOB) {
        this.wifeDOB = wifeDOB;
        return this;
    }

    public ANCRegisterEntry withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public ANCRegisterEntry withWifeEducationLevel(String wifeEducationLevel) {
        this.wifeEducationLevel = wifeEducationLevel;
        return this;
    }


    public ANCRegisterEntry withHusbandEducationLevel(String husbandEducationLevel) {
        this.husbandEducationLevel = husbandEducationLevel;
        return this;
    }

    public ANCRegisterEntry withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public ANCRegisterEntry withReligion(String religion) {
        this.religion = religion;
        return this;
    }

    public ANCRegisterEntry withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public ANCRegisterEntry withBPLCardNumber(String bplCardNumber) {
        this.bplCardNumber = bplCardNumber;
        return this;
    }

    public ANCRegisterEntry withJSYBeneficiary(String jsyBeneficiary) {
        this.jsyBeneficiary = jsyBeneficiary;
        return this;
    }

    public ANCRegisterEntry withGravida(String gravida) {
        this.gravida = gravida;
        return this;
    }

    public ANCRegisterEntry withParity(String parity) {
        this.parity = parity;
        return this;
    }

    public ANCRegisterEntry withNumberOfLivingChildren(String numberOfLivingChildren) {
        this.numberOfLivingChildren = numberOfLivingChildren;
        return this;
    }

    public ANCRegisterEntry withNumberOfStillBirths(String numberOfStillBirths) {
        this.numberOfStillBirths = numberOfStillBirths;
        return this;
    }

    public ANCRegisterEntry withNumberOfAbortions(String numberOfAbortions) {
        this.numberOfAbortions = numberOfAbortions;
        return this;
    }

    public ANCRegisterEntry withYoungestChildDOB(String youngestChildAge) {
        this.youngestChildDOB = youngestChildAge;
        return this;
    }

    public ANCRegisterEntry withLMP(String lmp) {
        this.lmp = lmp;
        return this;
    }

    public ANCRegisterEntry withEDD(String edd) {
        this.edd = edd;
        return this;
    }

    public ANCRegisterEntry withHeight(String height) {
        this.height = height;
        return this;
    }

    public ANCRegisterEntry withBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
        return this;
    }

    public ANCRegisterEntry withIsHRP(String isHRP) {
        this.isHRP = isHRP;
        return this;
    }

    public ANCRegisterEntry withANCVisits(List<Map<String, String>> ancVisits) {
        this.ancVisits = ancVisits;
        return this;
    }

    public ANCRegisterEntry withIFATablets(List<Map<String, String>> ifaTablets) {
        this.ifaTablets = ifaTablets;
        return this;
    }

    public ANCRegisterEntry withTTDoses(List<Map<String, String>> ttDoses) {
        this.ttDoses = ttDoses;
        return this;
    }

    public ANCRegisterEntry withHBTests(List<Map<String, String>> hbTests) {
        this.hbTests = hbTests;
        return this;
    }

    public String ancNumber() {
        return ancNumber;
    }

    public String registrationDate() {
        return registrationDate;
    }

    public String ecNumber() {
        return ecNumber;
    }

    public String thayiCardNumber() {
        return thayiCardNumber;
    }

    public String aadharCardNumber() {
        return aadharCardNumber;
    }

    public String wifeName() {
        return wifeName;
    }

    public String husbandName() {
        return husbandName;
    }

    public String address() {
        return address;
    }

    public LocalDate wifeDOB() {
        return wifeDOB;
    }

    public String phoneNumber() {
        return phoneNumber;
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

    public String bplCardNumber() {
        return bplCardNumber;
    }

    public String jsyBeneficiary() {
        return jsyBeneficiary;
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

    public String youngestChildDOB() {
        return youngestChildDOB;
    }

    public String lmp() {
        return lmp;
    }

    public String edd() {
        return edd;
    }

    public String height() {
        return height;
    }

    public String bloodGroup() {
        return bloodGroup;
    }

    public String isHRP() {
        return isHRP;
    }

    public List<Map<String, String>> ancVisits() {
        return ancVisits;
    }

    public List<Map<String, String>> ifaTablets() {
        return ifaTablets;
    }

    public List<Map<String, String>> ttDoses() {
        return ttDoses;
    }

    public List<Map<String, String>> hbTests() {
        return hbTests;
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
