package org.opensrp.contract;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;
import org.opensrp.scheduler.Schedule;

public class ScheduleTest {

    @Test
    public void shouldTestScheduleDependency(){
        Schedule schedule1 = new Schedule("schedule1", new ArrayList<String>());
        Schedule schedule2 = new Schedule("schedule3", new ArrayList<String>()).withDependencyOn(schedule1);
        assertFalse(schedule1.hasDependency());
        assertTrue(schedule2.hasDependency());
        assertTrue(schedule2.dependsOn(schedule1));
        assertFalse(schedule1.dependsOn(schedule2));
    }
}
