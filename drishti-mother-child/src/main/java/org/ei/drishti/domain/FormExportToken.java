package org.ei.drishti.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'FormExportToken'")
public class FormExportToken extends MotechBaseDataObject {
    @JsonProperty
    private long version;

    protected FormExportToken() {
    }

    public FormExportToken(long version) {
        this.version = version;
    }

    public FormExportToken withVersion(long version) {
        this.version = version;
        return this;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
