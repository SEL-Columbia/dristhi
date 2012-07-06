package org.ei.drishti.util;

import java.util.HashMap;

public class SafeMap extends HashMap<String, String> {
    @Override
    public String get(Object key) {
        if (!containsKey(key)) {
            throw new RuntimeException("Key: " + key + " does not exist in: " + this);
        }
        return super.get(key);
    }
}
