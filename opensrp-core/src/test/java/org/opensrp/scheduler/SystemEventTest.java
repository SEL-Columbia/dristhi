package org.opensrp.scheduler;


import com.google.gson.Gson;
import org.junit.Test;
import org.motechproject.scheduler.domain.MotechEvent;
import org.opensrp.common.FormEntityConstants;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SystemEventTest {

    @Test
    public void testMotechEventCreation() {
        SystemEvent systemEvent = new SystemEvent("subject", "data");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(SystemEvent.DATA, new Gson().toJson("data"));
        MotechEvent expected = new MotechEvent("subject", parameters);

        assertEquals(expected, systemEvent.toMotechEvent());

    }

    @Test
    public void testMotechEventCreationWintEnum() {
        SystemEvent systemEvent = new SystemEvent(FormEntityConstants.Person.first_name, "data");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(SystemEvent.DATA, new Gson().toJson("data"));
        MotechEvent expected = new MotechEvent(FormEntityConstants.Person.first_name.name(), parameters);

        assertEquals(expected, systemEvent.toMotechEvent());
    }
}
