package org.ei.drishti.integration.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;

public class SameItems {
    public static <T> Matcher<List<T>> hasSameItemsAs(final List<T> expectedItems) {
        return new TypeSafeMatcher<List<T>>() {
            @Override
            public boolean matchesSafely(List<T> actual) {
                return actual.size() == expectedItems.size() && itemsAreEqual(actual, expectedItems);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(expectedItems);
            }

            private boolean itemsAreEqual(List<T> actualItems, List<T> expectedItems) {
                for (int i = 0; i < actualItems.size(); i++) {
                    T actual = actualItems.get(i);
                    T expected = expectedItems.get(i);
                    if (!reflectionEquals(actual, expected)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
