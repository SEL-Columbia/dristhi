package org.ei.drishti.dto.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.drishti.dto.LocationDTO;

public class EntityDetailDTO {
    @JsonProperty
    private String entityId;
    @JsonProperty
    private String entityType;
    @JsonProperty
    private String thayiCardNumber;
    @JsonProperty
    private String ecNumber;
    @JsonProperty
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

    public EntityDetailDTO withThayiCardNumber(String thayiCardNumber) {
        this.thayiCardNumber = thayiCardNumber;
        return this;
    }

    public EntityDetailDTO withECNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public EntityDetailDTO withEntityID(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public EntityDetailDTO withANMIdentifier(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
        return this;
    }

    public EntityDetailDTO withEntityType(String type) {
        this.entityType = type;
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
