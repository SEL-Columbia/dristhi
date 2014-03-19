package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.LocalDate;

import java.util.Map;

public class ChildRegisterEntry {

    private String thayiCardNumber;
    private String wifeName;
    private String husbandName;
    private String village;
    private String subCenter;
    private LocalDate wifeDOB;
    private LocalDate DOB;
    private Map<String, LocalDate> immunizations;

    public String thayiCardNumber() {
        return thayiCardNumber;
    }

    public ChildRegisterEntry withThayiCardNumber(String thayiCardNumber) {
        this.thayiCardNumber = thayiCardNumber;
        return this;
    }

    public String wifeName() {
        return wifeName;
    }

    public ChildRegisterEntry withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public String husbandName() {
        return husbandName;
    }

    public ChildRegisterEntry withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public String village() {
        return village;
    }

    public ChildRegisterEntry withVillage(String village) {
        this.village = village;
        return this;
    }

    public String subCenter() {
        return subCenter;
    }

    public ChildRegisterEntry withSubCenter(String subCenter) {
        this.subCenter = subCenter;
        return this;
    }

    public LocalDate wifeDOB() {
        return wifeDOB;
    }

    public ChildRegisterEntry withWifeDOB(LocalDate wifeDOB) {
        this.wifeDOB = wifeDOB;
        return this;
    }

    public LocalDate dob() {
        return DOB;
    }

    public ChildRegisterEntry withDob(LocalDate dob) {
        this.DOB = dob;
        return this;
    }

    public Map<String, LocalDate> immunizations() {
        return immunizations;
    }

    public ChildRegisterEntry withImmunizations(Map<String, LocalDate> immunizations) {
        this.immunizations = immunizations;
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
