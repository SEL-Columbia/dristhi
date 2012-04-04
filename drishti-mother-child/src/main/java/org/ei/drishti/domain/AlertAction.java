package org.ei.drishti.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Map;

@TypeDiscriminator("doc.type === 'AlertAction'")
public class AlertAction extends MotechBaseDataObject {
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String caseID;
    @JsonProperty
    private Map<String,String> data;
    @JsonProperty
    private String alertType;
    @JsonProperty
    private long timeStamp;

    private AlertAction() {
    }

    public AlertAction(String caseId, String anmIdentifier, AlertData alertData) {
        this.anmIdentifier = anmIdentifier;
        this.caseID = caseId;
        this.data = alertData.data();
        this.alertType = alertData.type();
        this.timeStamp = DateUtil.now().getMillis();
    }

    public long timestamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, false, null, new String[] { "timeStamp" });
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
