package org.opensrp.integration;

import org.junit.Test;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;

public class ScheduleTrackingSchedulesJSONTest {
    @Test
    public void shouldNotFailWhileConvertingEveryScheduleFileToValidScheduleObject() throws Exception {
        TrackedSchedulesJsonReaderImpl schedulesReader = new TrackedSchedulesJsonReaderImpl("/schedules");
        schedulesReader.records();
    }

}
