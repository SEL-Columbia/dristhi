package org.ei.drishti.util;

import java.util.Map;

public class SafeMap {
    private final Map<String, String> data;

    public SafeMap(Map<String, String> data) {
        this.data = data;
    }

    public String get(String key) {
        if (!data.containsKey(key)) {
            throw new RuntimeException("Key: " + key + " does not exist in: " + data);
        }
        return data.get(key);
    }
}
