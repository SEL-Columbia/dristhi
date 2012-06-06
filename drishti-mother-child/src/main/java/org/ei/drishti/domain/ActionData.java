package org.ei.drishti.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class ActionData {
    private final HashMap<String, String> data;
    private String type;

    public static ActionData createAlert(String beneficiaryName, String village, String thaayiCardNumber, String visitCode, String latenessStatus, DateTime dueDate) {
        return new ActionData("createAlert").with("beneficiaryName", beneficiaryName).with("village", village).with("thaayiCardNumber", thaayiCardNumber)
                .with("visitCode", visitCode).with("latenessStatus", latenessStatus).with("dueDate", dueDate.toLocalDate().toString());
    }

    public static ActionData deleteAlert(String visitCode) {
        return new ActionData("deleteAlert").with("visitCode", visitCode);
    }

    public static ActionData deleteAllAlerts() {
        return new ActionData("deleteAllAlerts");
    }

    public static ActionData createEligibleCouple(String wife, String husband, String ecNumber, String village, String subCenter) {
        return new ActionData("createEC").with("wife", wife).with("husband", husband).with("ecNumber", ecNumber).with("village", village).with("subcenter", subCenter);
    }

    public static ActionData deleteEligibleCouple() {
        return new ActionData("deleteEC");
    }

    public static ActionData createPregnancy(String ecCaseId, String thaayiCardNumber, String motherName) {
        return new ActionData("createPregnancy").with("ecCaseId", ecCaseId).with("thaayiCardNumber", thaayiCardNumber).with("motherName", motherName);
    }

    public static ActionData from(String actionType, Map<String, String> data) {
        ActionData actionData = new ActionData(actionType);
        actionData.data.putAll(data);
        return actionData;
    }

    private ActionData(String type) {
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

    public String type() {
        return type;
    }
}
