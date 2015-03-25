package org.opensrp.register.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.opensrp.common.AllConstants;
import org.opensrp.common.domain.Location;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.*;

import static org.opensrp.common.AllConstants.ChildImmunizationFields.IMMUNIZATION_DATE_FIELD_NAME;

@TypeDiscriminator("doc.type === 'Child'")
public class Child extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String motherCaseId;
    @JsonProperty
    private String name;
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String dateOfBirth;
    @JsonProperty
    private String weight;
    @JsonProperty
    private String gender;
    @JsonProperty
    private String bloodGroup;
    @JsonProperty
    private String immunizationsGiven;
    @JsonProperty
    private String village;
    @JsonProperty
    private String subCenter;
    @JsonProperty
    private String phc;
    @JsonProperty
    private String isClosed;
    @JsonProperty
    private String thayiCard;
    @JsonProperty
    private Map<String, String> details;
    @JsonProperty
    private Map<String, String> immunizations;
    @JsonProperty
    private Map<String, String> vitaminADoses;

    private Child() {
    }

    public Child(String id, String motherCaseId, String immunizationsGiven, String weight, String gender) {
        this.caseId = id;
        this.motherCaseId = motherCaseId;
        this.immunizationsGiven = immunizationsGiven;
        this.weight = weight;
        this.gender = gender;
        this.setIsClosed(false);
        this.immunizations = new HashMap<>();
        this.vitaminADoses = new HashMap<>();
    }

    public Child withAnm(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
        return this;
    }

    public Child withLocation(String village, String subCenter, String phc) {
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        return this;
    }

    public Child withDateOfBirth(String dob) {
        this.dateOfBirth = dob;
        return this;
    }

    public Child withThayiCard(String thayiCard) {
        this.thayiCard = thayiCard;
        return this;
    }

    public Child withImmunizations(Map<String, String> immunizations) {
        this.immunizations = immunizations;
        return this;
    }

    public Child withVitaminADoses(Map<String, String> vitaminADoses) {
        this.vitaminADoses = vitaminADoses;
        return this;
    }

    public Child withDetails(Map<String, String> details) {
        this.details = details;
        return this;
    }

    public Child setIsClosed(boolean isClosed) {
        this.isClosed = Boolean.toString(isClosed);
        return this;
    }

    public Child withName(String name) {
        this.name = name;
        return this;
    }

    public String caseId() {
        return caseId;
    }

    public String motherCaseId() {
        return motherCaseId;
    }

    public String thayiCardNumber() {
        return thayiCard;
    }

    public String name() {
        return name;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public List<String> immunizationsGiven() {
        if (!StringUtils.isEmpty(immunizationsGiven)) {
            return new ArrayList<>(Arrays.asList(immunizationsGiven.split(" ")));
        }
        return new ArrayList<String>();
    }

    public Map<String, String> details() {
        return details;
    }

    public String dateOfBirth() {
        return dateOfBirth;
    }

    public String weight() {
        return weight;
    }

    public Location location() {
        return new Location(village, subCenter, phc);
    }

    public Map<String, String> immunizations() {
        return immunizations;
    }

    public Map<String, String> vitaminADoses() {
        return vitaminADoses;
    }

    @JsonIgnore
    public boolean isClosed() {
        return Boolean.parseBoolean(this.isClosed);
    }

    private String getCaseId() {
        return caseId;
    }

    private String getMotherCaseId() {
        return motherCaseId;
    }

    public String immunizationDate() {
        return details().get(IMMUNIZATION_DATE_FIELD_NAME);
    }

    @JsonIgnore
    public boolean isFemale() {
        return AllConstants.CommonChildFormFields.FEMALE_VALUE.equalsIgnoreCase(gender);
    }

    @JsonIgnore
    public boolean isMale() {
        return AllConstants.CommonChildFormFields.MALE_VALUE.equalsIgnoreCase(gender);
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
