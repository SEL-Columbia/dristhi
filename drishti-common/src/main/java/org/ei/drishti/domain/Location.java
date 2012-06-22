package org.ei.drishti.domain;

public class Location {
    private final String village;
    private final String subCenter;
    private final String phc;

    public Location(String village, String subCenter, String phc) {
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
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
}
