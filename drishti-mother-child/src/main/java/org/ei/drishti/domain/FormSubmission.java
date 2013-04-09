package org.ei.drishti.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'FormSubmission'")
public class FormSubmission extends MotechBaseDataObject {
    @JsonProperty
    private String instanceId;
    @JsonProperty
    private String formName;
    @JsonProperty
    private String anmId;
    @JsonProperty
    private String timeStamp;
    @JsonProperty
    private String entityId;
    @JsonProperty
    private String data;

    public FormSubmission() {
    }

    public FormSubmission(String instanceId, String formName, String anmId, String timeStamp, String entityId, String data) {
        this.instanceId = instanceId;
        this.formName = formName;
        this.anmId = anmId;
        this.timeStamp = timeStamp;
        this.entityId = entityId;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this, false, getClass());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(17, 37, this, false, getClass());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
