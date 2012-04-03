package org.ei.drishti.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Reminder'")
public class Reminder extends MotechBaseDataObject {
    @JsonProperty
    private String motherName;
    @JsonProperty
    private String thaayiCardNo;
    @JsonProperty
    private String anmPhoneNo;
    @JsonProperty
    private String caseID;
    @JsonProperty
    private String visitCode;
    @JsonProperty
    private String latenessStatus;

    private Reminder() {
    }

    public Reminder(String motherName, String thaayiCardNo, String anmPhoneNo, String caseID, String visitCode, String latenessStatus) {
        this.motherName = motherName;
        this.thaayiCardNo = thaayiCardNo;
        this.anmPhoneNo = anmPhoneNo;
        this.caseID = caseID;
        this.visitCode = visitCode;
        this.latenessStatus = latenessStatus;
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
