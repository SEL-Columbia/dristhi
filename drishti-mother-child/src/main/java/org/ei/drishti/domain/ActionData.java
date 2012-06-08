package org.ei.drishti.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.Map;

public class ActionData {
    private final HashMap<String, String> data;
    private String target;
    private String type;

    public static ActionData createAlert(String beneficiaryName, String village, String thaayiCardNumber, String visitCode, String latenessStatus, DateTime dueDate) {
        return new ActionData("alert", "createAlert").with("beneficiaryName", beneficiaryName).with("village", village).with("thaayiCardNumber", thaayiCardNumber)
                .with("visitCode", visitCode).with("latenessStatus", latenessStatus).with("dueDate", dueDate.toLocalDate().toString());
    }

    public static ActionData deleteAlert(String visitCode) {
        return new ActionData("alert", "deleteAlert").with("visitCode", visitCode);
    }

    public static ActionData deleteAllAlerts() {
        return new ActionData("alert", "deleteAllAlerts");
    }

    public static ActionData createEligibleCouple(String wife, String husband, String ecNumber, String village, String subCenter) {
        return new ActionData("eligibleCouple", "createEC").with("wife", wife).with("husband", husband).with("ecNumber", ecNumber).with("village", village).with("subcenter", subCenter);
    }

    public static ActionData deleteEligibleCouple() {
        return new ActionData("eligibleCouple", "deleteEC");
    }

    public static ActionData createBeneficiary(String ecCaseId, String thaayiCardNumber, LocalDate lmpDate) {
        return new ActionData("child", "createBeneficiary").with("ecCaseId", ecCaseId).with("thaayiCardNumber", thaayiCardNumber).with("status", "pregnant").with("referenceDate", lmpDate.toString());
    }

    public static ActionData updateBeneficiary(String status) {
        return new ActionData("child", "updateBeneficiary").with("status", status);
    }

    public static ActionData registerChildBirth(String motherCaseId, LocalDate dateOfBirth) {
        return new ActionData("child", "createChildBeneficiary").with("motherCaseId", motherCaseId).with("referenceDate", dateOfBirth.toString());
    }

    public static ActionData from(String actionType, String actionTarget, Map<String, String> data) {
        ActionData actionData = new ActionData(actionTarget, actionType);
        actionData.data.putAll(data);
        return actionData;
    }

    private ActionData(String target, String type) {
        this.target = target;
        this.type = type;
        data = new HashMap<String, String>();
    }

    public ActionData with(String key, String value) {
        data.put(key, value);
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

    public Map<String, String> data() {
        return data;
    }

    public String target() {
        return target;
    }

    public String type() {
        return type;
    }
}
