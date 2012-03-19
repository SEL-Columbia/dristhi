package org.ei.drishti.scheduler.router;

public abstract class Matcher {
    public abstract boolean matches(String actualValue);

    public static Matcher eq(String value) {
        return new EqMatcher(value);
    }

    public static Matcher any() {
        return new AnyMatcher();
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
}
