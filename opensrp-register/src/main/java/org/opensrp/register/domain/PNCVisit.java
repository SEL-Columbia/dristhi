package org.opensrp.register.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class PNCVisit {
    @JsonProperty
    private String date;
    @JsonProperty
    private String person;
    @JsonProperty
    private String place;
    @JsonProperty
    private String difficulties;
    @JsonProperty
    private String abdominalProblems;
    @JsonProperty
    private String vaginalProblems;
    @JsonProperty
    private String urinalProblems;
    @JsonProperty
    private String breastProblems;
    @JsonProperty
    private List<Map<String, String>> childrenDetails;

    public PNCVisit() {

    }

    public PNCVisit withDate(String date) {
        this.date = date;
        return this;
    }

    public PNCVisit withPerson(String person) {
        this.person = person;
        return this;
    }

    public PNCVisit withPlace(String place) {
        this.place = place;
        return this;
    }

    public PNCVisit withDifficulties(String difficulties) {
        this.difficulties = difficulties;
        return this;
    }

    public PNCVisit withAbdominalProblems(String abdominalProblems) {
        this.abdominalProblems = abdominalProblems;
        return this;
    }

    public PNCVisit withVaginalProblems(String vaginalProblems) {
        this.vaginalProblems = vaginalProblems;
        return this;
    }

    public PNCVisit withUrinalProblems(String urinalProblems) {
        this.urinalProblems = urinalProblems;
        return this;
    }

    public PNCVisit withBreastProblems(String breastProblems) {
        this.breastProblems = breastProblems;
        return this;
    }

    public PNCVisit withChildrenDetails(List<Map<String, String>> childrenDetails) {
        this.childrenDetails = childrenDetails;
        return this;
    }

    public String date() {
        return date;
    }

    public String person() {
        return person;
    }

    public String place() {
        return place;
    }

    public String difficulties() {
        return difficulties;
    }

    public String abdominalProblems() {
        return abdominalProblems;
    }

    public String vaginalProblems() {
        return vaginalProblems;
    }

    public String urinalProblems() {
        return urinalProblems;
    }

    public String breastProblems() {
        return breastProblems;
    }

    public List<Map<String, String>> childrenDetails() {
        return childrenDetails;
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
