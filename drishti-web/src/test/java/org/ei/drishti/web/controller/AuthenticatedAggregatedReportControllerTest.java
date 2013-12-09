package org.ei.drishti.web.controller;

import com.google.gson.Gson;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.report.AggregatedReportsDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuthenticatedAggregatedReportControllerTest {

    @Mock
    private HttpAgent httpAgent;

    private AuthenticatedAggregatedReportController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new AuthenticatedAggregatedReportController(httpAgent, "http://report_url", "http://site_url");
    }

    @Test
    public void shouldReturnAggregatedReportForGivenMonthForAGivenANM() throws Exception {
        AggregatedReportsDTO expectedAggregatedReports = new AggregatedReportsDTO(mapOf("indicator1", 2));
        when(httpAgent.get("http://report_url/aggregated-reports?anm-id=demo1&month=12&year=2013"))
                .thenReturn(new HttpResponse(true, new Gson().toJson(expectedAggregatedReports)));

        ResponseEntity<AggregatedReportsDTO> response = controller.get("demo1", 12, 2013);

        assertEquals(expectedAggregatedReports, response.getBody());
    }
}
