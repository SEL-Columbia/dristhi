package org.opensrp.common.util;

public class DoubleUtil {

    private DoubleUtil() {

    }

    public static double tryParse(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}