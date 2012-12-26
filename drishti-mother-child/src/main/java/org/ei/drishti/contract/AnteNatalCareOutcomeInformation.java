package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AnteNatalCareOutcomeInformation {
    private String motherCaseId;
    private String numberOfChildrenBorn;
    private String anmIdentifier;
    private String caseId_0;
    private String caseId_1;
    private String caseId_2;
    private String caseId_3;
    private String caseId_4;
    private String childName_0;
    private String childName_1;
    private String childName_2;
    private String childName_3;
    private String childName_4;
    private String gender_0;
    private String gender_1;
    private String gender_2;
    private String gender_3;
    private String gender_4;
    private String childWeight_0;
    private String childWeight_1;
    private String childWeight_2;
    private String childWeight_3;
    private String childWeight_4;
    private String childBloodGroup_0;
    private String childBloodGroup_1;
    private String childBloodGroup_2;
    private String childBloodGroup_3;
    private String childBloodGroup_4;
    private String immunizationsProvided_0;
    private String immunizationsProvided_1;
    private String immunizationsProvided_2;
    private String immunizationsProvided_3;
    private String immunizationsProvided_4;
    private String pregnancyOutcome;
    private String dateOfDelivery;
    private String bfPostBirth;

    public AnteNatalCareOutcomeInformation(String motherCaseId, String anmIdentifier, String pregnancyOutcome, String dateOfDelivery, String bfPostBirth, String numberOfChildrenBorn) {
        this.motherCaseId = motherCaseId;
        this.anmIdentifier = anmIdentifier;
        this.pregnancyOutcome = pregnancyOutcome;
        this.dateOfDelivery = dateOfDelivery;
        this.bfPostBirth = bfPostBirth;
        this.numberOfChildrenBorn = numberOfChildrenBorn;
    }

    public AnteNatalCareOutcomeInformation withChild(String caseId, String childNumber, String name, String gender, String weight, String bloodGroup, String immunizationsProvided) {
        setField("caseId_", childNumber, caseId);
        setField("childName_", childNumber, name);
        setField("gender_", childNumber, gender);
        setField("childWeight_", childNumber, weight);
        setField("childBloodGroup_", childNumber, bloodGroup);
        setField("immunizationsProvided_", childNumber, immunizationsProvided);
        return this;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String motherCaseId() {
        return motherCaseId;
    }

    public String childWeight(String childNumber) {
        return (String) getFieldValue("childWeight_", childNumber);
    }

    public String childBloodGroup(String childNumber) {
        return (String) getFieldValue("childBloodGroup_", childNumber);

    }

    public String caseId(String childNumber) {
        return (String) getFieldValue("caseId_", childNumber);
    }

    public String childName(String childNumber) {
        return (String) getFieldValue("childName_", childNumber);
    }

    public String gender(String childNumber) {
        return (String) getFieldValue("gender_", childNumber);
    }

    public String immunizationsProvided(String childNumber) {
        return (String) getFieldValue("immunizationsProvided_", childNumber);
    }

    public int numberOfChildrenBorn() {
        try {
            return Integer.parseInt(numberOfChildrenBorn);
        }catch (NumberFormatException e){
            return 0;
        }
    }

    public String deliveryOutcomeDate() {
        return dateOfDelivery;
    }

    public String bfPostBirth() {
        return bfPostBirth;
    }

    private void setField(String fieldPrefix, String childNumber, String value) {
        try {
            getClass().getDeclaredField(fieldPrefix + childNumber).set(this, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getFieldValue(String fieldPrefix, String childNumber) {
        try {
            return getClass().getDeclaredField(fieldPrefix + childNumber).get(this);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
