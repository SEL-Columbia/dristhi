package org.ei.drishti.listener;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;

import static org.mockito.Mockito.*;

public class ReportingEventListenerTest {

    private HttpAgent agent;
    private ReportingEventListener listener;

    @Before
    public void setUp() throws Exception {
        agent = mock(HttpAgent.class);
        listener = new ReportingEventListener(agent, "http://drishti");
    }

    @Test
    public void shouldSubmitReportingData() throws Exception {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("data", new ReportingData("Boo").with("abc", "def"));
        when(agent.post(eq("http://drishti"), any(String.class), eq("application/json"))).thenReturn(new HttpResponse(true, null));

        listener.submitReportingData(new MotechEvent("SUBJECT", data));

        verify(agent).post("http://drishti", "{\"type\":\"Boo\",\"data\":{\"abc\":\"def\"}}", "application/json");
    }
}
