package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.motechproject.util.DateUtil;

import java.util.Date;

public class ChildRegistrationRequest {
    private String childName;
    private String anmIdentifier;
    private String thaayiCardNumber = "1234567";
    private String immunizationsProvided;
    private Date dateOfBirth = DateUtil.today().toDate();

    public ChildRegistrationRequest(String childName, String thaayiCardNumber, Date dateOfBirth, String anmIdentifier, String immunizationsProvided) {
        this.childName = childName;
        this.thaayiCardNumber = thaayiCardNumber;
        this.dateOfBirth = dateOfBirth;
        this.anmIdentifier = anmIdentifier;
        this.immunizationsProvided = immunizationsProvided;
    }

    public String name() {
        return childName;
    }

    public String thaayiCardNumber() {
        return thaayiCardNumber;
    }

    public Date dateOfBirth() {
        return dateOfBirth;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String immunizationsProvided() {
        return immunizationsProvided;
    }
}
