package org.opensrp.scheduler;

import java.util.Arrays;
import java.util.List;

public abstract class Matcher {
    public abstract boolean matches(String actualValue);

    public static Matcher eq(String value) {
        return new EqMatcher(value);
    }

    public static Matcher any() {
        return new AnyMatcher();
    }

    public static Matcher anyOf(String... values) {
        return new AnyOfMatcher(values);
    }

    private static class AnyMatcher extends Matcher {
        @Override
        public boolean matches(String actualValue) {
            return true;
        }
    }

    private static class EqMatcher extends Matcher {
        private final String expectedValue;

        public EqMatcher(String expectedValue) {
            this.expectedValue = expectedValue;
        }

        @Override
        public boolean matches(String actualValue) {
            return expectedValue.equals(actualValue);
        }
    }

    private static class AnyOfMatcher extends Matcher {
        private final List<String> expectedValues;

        public AnyOfMatcher(String... expectedValues) {
            this.expectedValues = Arrays.asList(expectedValues);
        }

        @Override
        public boolean matches(String actualValue) {
            return expectedValues.contains(actualValue);
        }
    }
}
