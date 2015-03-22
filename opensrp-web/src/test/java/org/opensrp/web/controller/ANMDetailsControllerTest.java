package org.opensrp.web.controller;

import org.opensrp.common.util.HttpAgent;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.domain.ANMDetail;
import org.opensrp.domain.ANMDetails;
import org.opensrp.domain.DrishtiUser;
import org.opensrp.service.ANMDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.dto.ANMDTO;
import org.opensrp.dto.LocationDTO;
import org.opensrp.dto.register.ANMDetailDTO;
import org.opensrp.dto.register.ANMDetailsDTO;
import org.opensrp.web.controller.ANMDetailsController;
import org.opensrp.web.controller.UserController;
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
    @Mock
    private UserController userController;
    @Mock
    private DrishtiUser user;
    private ANMDetailsController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new ANMDetailsController(service, "http://dristhi_site_url", "http://dristhi_reporting_url/anms", userController, httpAgent);
    }

/*    @Test
    public void shouldGetANCDetailsForAllANMs() throws Exception {
        when(httpAgent.get("http://dristhi_reporting_url/anms?anm-id=bhe1")).
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
        DrishtiUser drishtiUser = new DrishtiUser("bhe1", "1", "salt", asList(""), true);
       // when(userController.currentUser()).thenReturn(drishtiUser);


        when(service.anmDetails(asList(new ANMDTO("bhe1", "Elizebeth D'souza", new LocationDTO("bherya_a", "Bherya", "K.R. Nagar", "Mysore", "Karnataka")))))
                .thenReturn(expectedANMDetails);

        ResponseEntity<ANMDetailsDTO> response = controller.allANMs();

        assertEquals(new ANMDetailsDTO(asList(new ANMDetailDTO("bhe1", "Elizebeth D'souza",
                new LocationDTO("bherya_a", "Bherya", "K.R. Nagar", "Mysore", "Karnataka"), 1, 2, 3, 4, 5))), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Access-Control-Allow-Origin"));
        assertEquals("http://dristhi_site_url", response.getHeaders().getFirst("Access-Control-Allow-Origin"));
    }*/
}
