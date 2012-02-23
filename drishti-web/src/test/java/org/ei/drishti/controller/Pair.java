package org.ei.drishti.controller;

public class Pair {
    private final String milestoneName;
    private final String windowName;

    public Pair(String milestoneName, String windowName) {
        this.milestoneName = milestoneName;
        this.windowName = windowName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (milestoneName != null ? !milestoneName.equals(pair.milestoneName) : pair.milestoneName != null)
            return false;
        if (windowName != null ? !windowName.equals(pair.windowName) : pair.windowName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = milestoneName != null ? milestoneName.hashCode() : 0;
        result = 31 * result + (windowName != null ? windowName.hashCode() : 0);
        return result;
    }
}
