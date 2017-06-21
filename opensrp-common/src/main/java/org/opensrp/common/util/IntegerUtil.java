package org.opensrp.common.util;

import static java.lang.Integer.parseInt;

public class IntegerUtil {

    private IntegerUtil() {

    }

    public static int tryParse(String value, int defaultValue) {
        try {
            return parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String parseValidIntegersAndDefaultInvalidOnesToEmptyString(String value) {
        return !isInteger(value) ? "" : String.valueOf(tryParse(value, 0));
    }
}