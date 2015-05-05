package org.opensrp.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class FPRegister {

    private final List<IUDRegisterEntry> iudRegisterEntries;
    private final List<CondomRegisterEntry> condomRegisterEntries;
    private final List<OCPRegisterEntry> ocpRegisterEntries;
    private final List<MaleSterilizationRegisterEntry> maleSterilizationRegisterEntries;
    private final List<FemaleSterilizationRegisterEntry> femaleSterilizationRegisterEntries;
    private final Integer reportingYear;

    public FPRegister(List<IUDRegisterEntry> iudRegisterEntries,
                      List<CondomRegisterEntry> condomRegisterEntries,
                      List<OCPRegisterEntry> ocpRegisterEntries,
                      List<MaleSterilizationRegisterEntry> maleSterilizationRegisterEntries,
                      List<FemaleSterilizationRegisterEntry> femaleSterilizationRegisterEntries, Integer reportingYear) {
        this.iudRegisterEntries = iudRegisterEntries;
        this.condomRegisterEntries = condomRegisterEntries;
        this.ocpRegisterEntries = ocpRegisterEntries;
        this.maleSterilizationRegisterEntries = maleSterilizationRegisterEntries;
        this.femaleSterilizationRegisterEntries = femaleSterilizationRegisterEntries;
        this.reportingYear = reportingYear;
    }

    public List<IUDRegisterEntry> iudRegisterEntries() {
        return iudRegisterEntries;
    }

    public List<CondomRegisterEntry> condomRegisterEntries() {
        return condomRegisterEntries;
    }

    public List<OCPRegisterEntry> ocpRegisterEntries() {
        return ocpRegisterEntries;
    }

    public List<MaleSterilizationRegisterEntry> maleSterilizationRegisterEntries() {
        return maleSterilizationRegisterEntries;
    }

    public List<FemaleSterilizationRegisterEntry> femaleSterilizationRegisterEntries() {
        return femaleSterilizationRegisterEntries;
    }

    public Integer getReportingYear() {
        return reportingYear;
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
