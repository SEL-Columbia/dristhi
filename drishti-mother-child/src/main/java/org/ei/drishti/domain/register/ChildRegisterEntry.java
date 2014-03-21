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
    private Map<String, LocalDate> vitaminADoses;

    public String thayiCardNumber() {
        return thayiCardNumber;
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

    public LocalDate wifeDOB() {
        return wifeDOB;
    }

    public LocalDate DOB() {
        return DOB;
    }

    public Map<String, LocalDate> immunizations() {
        return immunizations;
    }

    public Map<String, LocalDate> vitaminADoses() {
        return vitaminADoses;
    }

    public ChildRegisterEntry withThayiCardNumber(String thayiCardNumber) {
        this.thayiCardNumber = thayiCardNumber;
        return this;
    }

    public ChildRegisterEntry withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public ChildRegisterEntry withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public ChildRegisterEntry withVillage(String village) {
        this.village = village;
        return this;
    }

    public ChildRegisterEntry withSubCenter(String subCenter) {
        this.subCenter = subCenter;
        return this;
    }

    public ChildRegisterEntry withWifeDOB(LocalDate wifeDOB) {
        this.wifeDOB = wifeDOB;
        return this;
    }

    public ChildRegisterEntry withDOB(LocalDate dob) {
        this.DOB = dob;
        return this;
    }

    public ChildRegisterEntry withImmunizations(Map<String, LocalDate> immunizations) {
        this.immunizations = immunizations;
        return this;
    }

    public ChildRegisterEntry withVitaminADoses(Map<String, LocalDate> vitaminADoses) {
        this.vitaminADoses = vitaminADoses;
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
