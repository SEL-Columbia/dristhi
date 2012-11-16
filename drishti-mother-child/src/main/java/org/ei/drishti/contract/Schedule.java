package org.ei.drishti.contract;

import java.util.Map;

public class Schedule {
    private String name;
    private final Map<String, String> mileStonesNameMapping;

    public Schedule(String name, Map<String, String> mileStonesNameMapping) {
        this.name = name;
        this.mileStonesNameMapping = mileStonesNameMapping;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getMileStonesNameMapping() {
        return mileStonesNameMapping;
    }
}
