package org.ei.drishti.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.common.util.IntegerUtil;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;

import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.ECRegistrationFields.WIFE_AGE;
import static org.ei.drishti.common.AllConstants.ECRegistrationFields.WIFE_DOB;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;

@TypeDiscriminator("doc.type === 'EligibleCouple'")
public class EligibleCouple extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String ecNumber;
    @JsonProperty
    private String wifeName;
    @JsonProperty
    private String husbandName;
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String village;
    @JsonProperty
    private String subCenter;
    @JsonProperty
    private String phc;
    @JsonProperty
    private String isOutOfArea;
    @JsonProperty
    private String isClosed;
    @JsonProperty
    private Map<String, String> details;

    public EligibleCouple() {
    }

    public EligibleCouple(String caseId, String ecNumber) {
        this.caseId = caseId;
        this.ecNumber = ecNumber;
        this.isOutOfArea = "false";
        this.setIsClosed(false);
    }

    public EligibleCouple withCouple(String wifeName, String husbandName) {
        this.wifeName = wifeName;
        this.husbandName = husbandName;
        return this;
    }

    public EligibleCouple withANMIdentifier(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
        return this;
    }

    public EligibleCouple withLocation(String village, String subCenter, String phc) {
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        return this;
    }

    public EligibleCouple withDetails(Map<String, String> details) {
        this.details = new HashMap<>(details);
        return this;
    }

    public EligibleCouple asOutOfArea() {
        this.isOutOfArea = "true";
        return this;
    }

    public String wifeName() {
        return wifeName;
    }

    public String husbandName() {
        return husbandName;
    }

    public String caseId() {
        return caseId;
    }

    public String ecNumber() {
        return ecNumber;
    }

    public String village() {
        return village;
    }

    public String subCenter() {
        return subCenter;
    }

    public String phc() {
        return phc;
    }

    public Location location() {
        return new Location(village, subCenter, phc);
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public Map<String, String> details() {
        return details;
    }

    public EligibleCouple setIsClosed(boolean isClosed) {
        this.isClosed = Boolean.toString(isClosed);
        return this;
    }

    public String currentMethod() {
        return details.get(CURRENT_FP_METHOD_FIELD_NAME);
    }

    private String getCaseId() {
        return caseId;
    }

    public String getDetail(String name) {
        return details.get(name);
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

    public LocalDate wifeDOB() {
        String wifeDOB = details.get(WIFE_DOB);
        if (wifeDOB != null) {
            return LocalDate.parse(wifeDOB);
        }
        int wifeAge = IntegerUtil.tryParse(details.get(WIFE_AGE), 0);
        return (wifeAge != 0) ? DateUtil.today().minusYears(wifeAge) : null;
    }
}
