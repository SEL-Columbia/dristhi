package org.opensrp.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class PNCRegister {

    private final List<PNCRegisterEntry> pncRegisterEntries;

    public PNCRegister(List<PNCRegisterEntry> pncRegisterEntries) {
        this.pncRegisterEntries = pncRegisterEntries;
    }

    public List<PNCRegisterEntry> pncRegisterEntries() {
        return pncRegisterEntries;
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
