package org.ei.drishti.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.text.MessageFormat;

@TypeDiscriminator("doc.type === 'Mother'")
public class Mother extends MotechBaseDataObject {
    @JsonProperty
    private String thaayiCardNumber;
    @JsonProperty
    private String name;

    private Mother() {
    }

    public Mother(String thaayiCardNumber, String name) {
        this.thaayiCardNumber = thaayiCardNumber;
        this.name = name;
    }

    public String thaayiCardNumber() {
        throw new RuntimeException("Unsupported.");
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

        if (!thaayiCardNumber.equals(mother.thaayiCardNumber)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return thaayiCardNumber.hashCode();
    }

    private String getThaayiCardNumber() {
        return thaayiCardNumber;
    }
}
