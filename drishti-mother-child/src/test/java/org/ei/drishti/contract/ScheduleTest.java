package org.ei.drishti.contract;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ScheduleTest {

    @Test
    public void shouldTestScheduleDependency(){
        Schedule schedule1 = new Schedule("schedule1", new HashMap<String,String>(), null);
        Schedule schedule2 = new Schedule("schedule2", new HashMap<String,String>(), null);
        Schedule schedule3 = new Schedule("schedule3", new HashMap<String,String>(), schedule1);
        assertFalse(schedule1.hasDependency());
        assertTrue(schedule3.hasDependency());
        assertTrue(schedule3.dependsOn(schedule1));
        assertFalse(schedule3.dependsOn(schedule2));
    }
}
