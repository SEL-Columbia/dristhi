package org.ei.drishti.util;

import java.util.HashMap;

public class SafeMap {
    private HashMap<String, String> data;

    public SafeMap() {
        this.data = new HashMap<>();
    }

    public String get(String key) {
        if (!data.containsKey(key)) {
            throw new RuntimeException("Key: " + key + " does not exist in: " + this);
        }
        return data.get(key);
    }

    public SafeMap put(String key, String value) {
        data.put(key, value);
        return this;
    }
}
