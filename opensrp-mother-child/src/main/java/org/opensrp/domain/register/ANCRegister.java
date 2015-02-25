package org.opensrp.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ANCRegister {

    private final List<ANCRegisterEntry> ancRegisterEntries;

    public ANCRegister(List<ANCRegisterEntry> ancRegisterEntries) {
        this.ancRegisterEntries = ancRegisterEntries;
    }

    public List<ANCRegisterEntry> ancRegisterEntries() {
        return ancRegisterEntries;
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
