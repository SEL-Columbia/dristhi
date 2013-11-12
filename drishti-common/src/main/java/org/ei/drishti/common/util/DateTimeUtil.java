package org.ei.drishti.common.util;

import org.joda.time.LocalDateTime;

public class DateTimeUtil {
    private static DateTimeUtility dateUtility = new RealDateTime();

    public static void fakeIt(LocalDateTime fakeDateTime) {
        dateUtility = new MockDateTime(fakeDateTime);
    }

    public static LocalDateTime now() {
        return dateUtility.now();
    }
}

interface DateTimeUtility {
    LocalDateTime now();
}

class RealDateTime implements DateTimeUtility {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}

class MockDateTime implements DateTimeUtility {
    private LocalDateTime fakeDateTime;

    MockDateTime(LocalDateTime fakeDateTime) {
        this.fakeDateTime = fakeDateTime;
    }

    @Override
    public LocalDateTime now() {
        return fakeDateTime;
    }
}