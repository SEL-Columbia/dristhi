package org.ei.drishti.web.controller;

import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.VillagesDTO;
import org.ei.drishti.service.ANMDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANMLocationControllerTest {

    @Mock
    private ANMDetailsService service;
    @Mock
    private HttpAgent httpAgent;
    private ANMLocationController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new ANMLocationController("http://dristhi_reporting_url/villages", httpAgent);
    }

    @Test
    public void shouldGetANCDetailsForAllANMs() throws Exception {
        when(httpAgent.get("http://dristhi_reporting_url/villages?anm-id=demo1")).
                thenReturn(new HttpResponse(true, "{\n" +
                        "    \"phcName\": \"PHC X\",\n" +
                        "    \"phcIdentifier\": \"phc1\",\n" +
                        "    \"subCenter\": \"Sub Center 1\",\n" +
                        "    \"villages\": [\n" +
                        "        \"village1\",\n" +
                        "        \"village2\",\n" +
                        "        \"village3\"\n" +
                        "    ]\n" +
                        "}"));

        ResponseEntity<VillagesDTO> response = controller.villagesForANM("demo1");

        assertEquals(new VillagesDTO("PHC X", "phc1", "Sub Center 1", asList("village1", "village2", "village3")), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
