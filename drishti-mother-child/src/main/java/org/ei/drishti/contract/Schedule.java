package org.ei.drishti.contract;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class Schedule {
    private String name;
    private final List<String> mileStones;
    private Schedule dependsOn;

    public Schedule(String name, List<String> mileStones) {
        this.name = name;
        this.mileStones = mileStones;
        this.dependsOn = null;
    }

    public Schedule withDependencyOn(Schedule dependsOn){
        this.dependsOn = dependsOn;
        return this;
    }

    public String getName() {
        return name;
    }

    public List<String> getMileStones() {
        return mileStones;
    }

    public boolean hasDependency(){
        return dependsOn != null;
    }

    public boolean dependsOn(Schedule schedule){
        if(dependsOn == null)
            return false;
        return dependsOn.equals(schedule);
    }

    public Schedule getDependencySchedule(){
        return dependsOn;
    }

    public String getLastMilestone(){
        return mileStones.get(mileStones.size()-1);
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
