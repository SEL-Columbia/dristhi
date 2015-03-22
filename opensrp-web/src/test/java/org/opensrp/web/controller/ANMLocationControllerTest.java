package org.opensrp.web.controller;

import com.google.gson.Gson;
import org.opensrp.common.util.HttpAgent;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.domain.DrishtiUser;
import org.opensrp.service.ANMDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.dto.VillagesDTO;
import org.opensrp.web.controller.ANMLocationController;
import org.opensrp.web.controller.UserController;
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
    @Mock
    private UserController userController;
    @Mock
    private DrishtiUser user;
    private ANMLocationController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new ANMLocationController("http://dristhi_reporting_url/villages", userController, httpAgent);
    }
/*
    @Test
    public void shouldGetANCDetailsForAllANMs() throws Exception {

        when(httpAgent.get("http://dristhi_reporting_url/villages?anm-id=demo1")).
                thenReturn(new HttpResponse(true,
                        new Gson().toJson(new VillagesDTO("district", "PHC X", "phc1", "Sub Center 1", asList("village1", "village2", "village3")))));
       // when(userController.currentUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("demo1");
        when(user.getRoles()).thenReturn(asList("User"));

        ResponseEntity<VillagesDTO> response = controller.villagesForANM();

        assertEquals(new VillagesDTO("district", "PHC X", "phc1", "Sub Center 1", asList("village1", "village2", "village3")), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }*/
}
