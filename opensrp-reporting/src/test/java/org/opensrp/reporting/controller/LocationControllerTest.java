package org.opensrp.reporting.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.dto.VillagesDTO;
import org.opensrp.reporting.controller.LocationController;
import org.opensrp.reporting.domain.Location;
import org.opensrp.reporting.domain.PHC;
import org.opensrp.reporting.service.ANMService;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationControllerTest {

    @Mock
    private ANMService anmService;
    @Mock
    private Location location;
    @Mock
    private PHC phc;
    private LocationController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new LocationController(anmService);
    }

    @Test
    public void shouldReturnListOfVillagesForANM() throws Exception {
        when(anmService.getVillagesForANM("demo1")).thenReturn(
                asList(
                        new Location("village1", "sc", phc, "taluk", "District", "state"),
                        new Location("village2", "sc", phc, "taluk", "District", "state")
                ));

        when(anmService.getLocation("demo1")).thenReturn(location);
        when(location.phc()).thenReturn(phc);
        when(location.subCenter()).thenReturn("sc");
        when(phc.phcIdentifier()).thenReturn("phc1");
        when(phc.name()).thenReturn("PHC X");
        when(location.phcName()).thenReturn("PHC X");

        ResponseEntity<VillagesDTO> response = controller.villagesForANM("demo1");

        VillagesDTO villagesDTO = new VillagesDTO("district", "PHC X", "phc1", "sc", asList("village1", "village2"));
        assertEquals(villagesDTO, response.getBody());
    }
}
