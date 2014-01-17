package org.ei.drishti.web.controller;

import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.domain.ANMDetail;
import org.ei.drishti.domain.ANMDetails;
import org.ei.drishti.dto.ANMDTO;
import org.ei.drishti.dto.LocationDTO;
import org.ei.drishti.dto.register.ANMDetailDTO;
import org.ei.drishti.dto.register.ANMDetailsDTO;
import org.ei.drishti.service.ANMDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANMDetailsControllerTest {

    @Mock
    private ANMDetailsService service;
    @Mock
    private HttpAgent httpAgent;
    private ANMDetailsController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new ANMDetailsController(service, "http://dristhi_site_url", "http://dristhi_reporting_url/anms", httpAgent);
    }

    @Test
    public void shouldGetANCDetailsForAllANMs() throws Exception {
        when(httpAgent.get("http://dristhi_reporting_url/anms")).
                thenReturn(new HttpResponse(true,
                        "[\n" +
                                "    {\n" +
                                "        \"identifier\": \"bhe1\",\n" +
                                "        \"name\": \"Elizebeth D'souza\",\n" +
                                "        \"location\": {\n" +
                                "            \"sub_center\": \"bherya_a\",\n" +
                                "            \"phc\": \"Bherya\",\n" +
                                "            \"taluka\": \"K.R. Nagar\",\n" +
                                "            \"district\": \"Mysore\",\n" +
                                "            \"state\": \"Karnataka\"\n" +
                                "        }\n" +
                                "    }\n" +
                                "]"));
        ANMDetails expectedANMDetails = new ANMDetails(asList(new ANMDetail("bhe1", "Elizebeth D'souza",
                new LocationDTO("bherya_a", "Bherya", "K.R. Nagar", "Mysore", "Karnataka"), 1, 2, 3, 4, 5)));

        when(service.anmDetails(asList(new ANMDTO("bhe1", "Elizebeth D'souza", new LocationDTO("bherya_a", "Bherya", "K.R. Nagar", "Mysore", "Karnataka")))))
                .thenReturn(expectedANMDetails);

        ResponseEntity<ANMDetailsDTO> response = controller.allANMs();

        assertEquals(new ANMDetailsDTO(asList(new ANMDetailDTO("bhe1", "Elizebeth D'souza",
                new LocationDTO("bherya_a", "Bherya", "K.R. Nagar", "Mysore", "Karnataka"), 1, 2, 3, 4, 5))), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Access-Control-Allow-Origin"));
        assertEquals("http://dristhi_site_url", response.getHeaders().getFirst("Access-Control-Allow-Origin"));
    }
}
