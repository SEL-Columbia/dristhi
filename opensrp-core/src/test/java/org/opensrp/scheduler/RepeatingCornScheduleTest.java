package org.opensrp.scheduler;


import org.junit.Test;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class RepeatingCornScheduleTest {

    public static final String CRON_EXPRESSION = "cornexpression";
    public static final int START_DELAY = 100;
    public static final String SUBJECT = "Subject";
    RepeatingCronSchedule repeatingCronSchedule = new RepeatingCronSchedule(SUBJECT, START_DELAY, TimeUnit.HOURS, CRON_EXPRESSION);

    @Test
    public void testMillisConverter() {
        assertEquals(1/1000000l, repeatingCronSchedule.millisConverter(TimeUnit.NANOSECONDS));
        assertEquals(1/1000l, repeatingCronSchedule.millisConverter(TimeUnit.MICROSECONDS));
        assertEquals(1l, repeatingCronSchedule.millisConverter(TimeUnit.MILLISECONDS));
        assertEquals(1000l, repeatingCronSchedule.millisConverter(TimeUnit.SECONDS));
        assertEquals(1000*60, repeatingCronSchedule.millisConverter(TimeUnit.MINUTES));
        assertEquals(1000*60*60l, repeatingCronSchedule.millisConverter(TimeUnit.HOURS));
        assertEquals(1000*60*60*24, repeatingCronSchedule.millisConverter(TimeUnit.DAYS));
    }

    @Test
    public void testConstructor() {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("key1", "value1");
        RepeatingCronSchedule repeatingCronSchedule = new RepeatingCronSchedule(SUBJECT, START_DELAY, TimeUnit.MILLISECONDS, CRON_EXPRESSION, new Date(0l), extraData);

        assertEquals(extraData, repeatingCronSchedule.getData());
        assertEquals(new Date(0l), repeatingCronSchedule.getEndTime());
        assertEquals(START_DELAY, repeatingCronSchedule.getStartDelayMilis());

        repeatingCronSchedule.addData("key2", "value2");
        extraData.put("key2", "value2");
        assertEquals(extraData, repeatingCronSchedule.getData());

        repeatingCronSchedule.withData(Collections.EMPTY_MAP);
        assertEquals(Collections.EMPTY_MAP, repeatingCronSchedule.getData());

        repeatingCronSchedule.withEndTime(new Date(1l));
        assertEquals(new Date(1l), repeatingCronSchedule.getEndTime());

    }
}
