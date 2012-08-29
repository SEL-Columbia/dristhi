package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.util.EasyMap.mapOf;

public class AnteNatalCareInformation {
    private String caseId;
    private String anmIdentifier;
    private int ancVisitNumber;
    private int numberOfIFATabletsGiven;

    public AnteNatalCareInformation(String caseId) {
        this.caseId = caseId;
    }

    public String caseId() {
        return caseId;
    }

    public AnteNatalCareInformation withNumberOfIFATabletsProvided(int numberOfTablets) {
        this.numberOfIFATabletsGiven = numberOfTablets;
        return this;
    }

    public AnteNatalCareInformation withAncVisit(int visitNumber) {
        this.ancVisitNumber = visitNumber;
        return this;
    }

    public Map<Integer, LocalDate> ancVisits() {
        if (ancVisitNumber == 0) {
            return new HashMap<>();
        }
        return mapOf(ancVisitNumber, DateUtil.today());
    }

    public boolean areIFATabletsProvided() {
        return numberOfIFATabletsGiven > 0;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

