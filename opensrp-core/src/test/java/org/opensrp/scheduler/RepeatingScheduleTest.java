package org.opensrp.scheduler;


import org.junit.Test;
import org.opensrp.scheduler.RepeatingCronSchedule;
import org.opensrp.scheduler.RepeatingSchedule;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.opensrp.scheduler.RepeatingCornScheduleTest.CRON_EXPRESSION;

public class RepeatingScheduleTest {
    public static final int START_DELAY = 100;
    public static final String SUBJECT = "Subject";
    public static final int REPEAT_INTERVAL = 5;
    RepeatingSchedule repeatingSchedule = new RepeatingSchedule(SUBJECT, START_DELAY, TimeUnit.HOURS, REPEAT_INTERVAL, TimeUnit.MILLISECONDS);

    @Test
    public void testMillisConverter() {
        assertEquals(1/1000000l, repeatingSchedule.millisConverter(TimeUnit.NANOSECONDS));
        assertEquals(1/1000l, repeatingSchedule.millisConverter(TimeUnit.MICROSECONDS));
        assertEquals(1l, repeatingSchedule.millisConverter(TimeUnit.MILLISECONDS));
        assertEquals(1000l, repeatingSchedule.millisConverter(TimeUnit.SECONDS));
        assertEquals(1000*60, repeatingSchedule.millisConverter(TimeUnit.MINUTES));
        assertEquals(1000*60*60l, repeatingSchedule.millisConverter(TimeUnit.HOURS));
        assertEquals(1000*60*60*24, repeatingSchedule.millisConverter(TimeUnit.DAYS));
    }

    @Test
    public void testConstructor() {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("key1", "value1");
        RepeatingSchedule repeatingSchedule = new RepeatingSchedule(SUBJECT, START_DELAY, TimeUnit.MILLISECONDS, REPEAT_INTERVAL, TimeUnit.MILLISECONDS, new Date(0l), extraData);

        assertEquals(extraData, repeatingSchedule.getData());
        assertEquals(new Date(0l), repeatingSchedule.getEndTime());
        assertEquals(START_DELAY, repeatingSchedule.getStartDelayMilis());
        assertEquals(REPEAT_INTERVAL, repeatingSchedule.getRepeatIntervalMilis());

        repeatingSchedule.addData("key2", "value2");
        extraData.put("key2", "value2");
        assertEquals(extraData, repeatingSchedule.getData());

        repeatingSchedule.withData(Collections.EMPTY_MAP);
        assertEquals(Collections.EMPTY_MAP, repeatingSchedule.getData());

        repeatingSchedule.withEndTime(new Date(1l));
        assertEquals(new Date(1l), repeatingSchedule.getEndTime());

    }

}
