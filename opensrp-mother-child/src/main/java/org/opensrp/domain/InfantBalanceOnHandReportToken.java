package org.opensrp.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'InfantBalanceOnHandReportToken'")
public class InfantBalanceOnHandReportToken extends MotechBaseDataObject {
    @JsonProperty
    private LocalDate lastReportedDate;

    protected InfantBalanceOnHandReportToken() {
    }

    public InfantBalanceOnHandReportToken(LocalDate lastReportedDate) {
        this.lastReportedDate = lastReportedDate;
    }

    public InfantBalanceOnHandReportToken withLastReportedDate(LocalDate lastReportedDate) {
        this.lastReportedDate = lastReportedDate;
        return this;
    }

    public LocalDate getLastReportedDate() {
        return lastReportedDate;
    }

    public InfantBalanceOnHandReportToken withId(String id) {
        setId(id);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "id");
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
