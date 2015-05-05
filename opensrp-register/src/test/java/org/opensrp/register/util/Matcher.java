package org.opensrp.register.util;

import static org.mockito.Matchers.argThat;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

public class Matcher {
    public static <T> T objectWithSameFieldsAs(final T other) {
        return argThat(hasSameFieldsAs(other));
    }

    public static <T> ArgumentMatcher<T> hasSameFieldsAs(final T other) {
        return new ArgumentMatcher<T>() {
            @Override
            public boolean matches(Object o) {
                return EqualsBuilder.reflectionEquals(other, o);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(other);
            }
        };
    }
}
