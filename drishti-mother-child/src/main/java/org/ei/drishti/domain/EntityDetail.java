package org.ei.drishti.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class EntityDetail {

    private String entityId;
    private String entityType;
    private String thayiCardNumber;
    private String ecNumber;
    private String anmIdentifier;

    public String anmIdentifier() {
        return this.anmIdentifier;
    }
    public String entityID() {
        return this.entityId;
    }

    public String entityType() {
        return this.entityType;
    }

    public String thayiCardNumber() {
        return this.thayiCardNumber;
    }

    public String ecNumber() {
        return this.ecNumber;
    }

    public EntityDetail withThayiCardNumber(String thayiCardNumber) {
        this.thayiCardNumber = thayiCardNumber;
        return this;
    }

    public EntityDetail withECNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public EntityDetail withEntityID(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public EntityDetail withANMIdentifier(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
        return this;
    }

    public EntityDetail withEntityType(String type) {
        this.entityType = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "id", "revision");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id", "revision");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
