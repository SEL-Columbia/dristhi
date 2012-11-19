package org.ei.drishti.contract;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

public class Schedule {
    private String name;
    private final Map<String, String> mileStonesNameMapping;
    private final Schedule dependsOn;

    public Schedule(String name, Map<String, String> mileStonesNameMapping, Schedule dependsOn) {
        this.name = name;
        this.mileStonesNameMapping = mileStonesNameMapping;
        this.dependsOn = dependsOn;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getMileStonesNameMapping() {
        return mileStonesNameMapping;
    }

    public boolean hasDependency(){
        return dependsOn != null;
    }

    public boolean dependsOn(Schedule schedule){
        return dependsOn.equals(schedule);
    }

    public Schedule getDependencySchedule(){
        return dependsOn;
    }

    public String getLastMilestone(){
        Object[] values = mileStonesNameMapping.values().toArray();
        return (String)values[values.length-1];
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this, false, getClass());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(17, 37, this, false, getClass());
    }

}
