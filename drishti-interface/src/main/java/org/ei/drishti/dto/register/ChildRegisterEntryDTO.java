package org.ei.drishti.dto.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.LocalDate;

import java.util.Map;

public class ChildRegisterEntryDTO {
    @JsonProperty
    private String thayiCardNumber;
    @JsonProperty
    private String wifeName;
    @JsonProperty
    private String husbandName;
    @JsonProperty
    private String village;
    @JsonProperty
    private String subCenter;
    @JsonProperty
    private LocalDate wifeDOB;
    @JsonProperty
    private LocalDate DOB;
    @JsonProperty
    private Map<String, LocalDate> immunizations;

    public ChildRegisterEntryDTO withThayiCardNumber(String thayiCardNumber) {
        this.thayiCardNumber = thayiCardNumber;
        return this;
    }

    public ChildRegisterEntryDTO withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public ChildRegisterEntryDTO withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public ChildRegisterEntryDTO withVillage(String village) {
        this.village = village;
        return this;
    }

    public ChildRegisterEntryDTO withSubCenter(String subCenter) {
        this.subCenter = subCenter;
        return this;
    }

    public ChildRegisterEntryDTO withWifeDOB(LocalDate wifeDOB) {
        this.wifeDOB = wifeDOB;
        return this;
    }

    public ChildRegisterEntryDTO withDOB(LocalDate DOB) {
        this.DOB = DOB;
        return this;
    }

    public ChildRegisterEntryDTO withImmunizations(Map<String, LocalDate> immunizations) {
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
