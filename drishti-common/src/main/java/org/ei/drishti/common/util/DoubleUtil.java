package org.ei.drishti.common.util;

public class DoubleUtil {
    public static double tryParse(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}