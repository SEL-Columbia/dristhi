package org.ei.drishti.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.text.MessageFormat;

@TypeDiscriminator("doc.type === 'Mother'")
public class Mother extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String thaayiCardNumber;
    @JsonProperty
    private String name;

    private Mother() {
    }

    public Mother(String caseId, String thaayiCardNumber, String name) {
        this.caseId = caseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Mother: {0} ({1})", name, thaayiCardNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mother mother = (Mother) o;

        if (!caseId.equals(mother.caseId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return caseId.hashCode();
    }

    private String getCaseId() {
        return caseId;
    }
}
