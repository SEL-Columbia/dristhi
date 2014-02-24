package org.ei.drishti.reporting.controller;

import org.ei.drishti.dto.VillagesDTO;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.domain.PHC;
import org.ei.drishti.reporting.service.ANMService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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
                        new Location("village1", "sc", phc, "taluk", "district", "state"),
                        new Location("village2", "sc", phc, "taluk", "district", "state")
                        ));

        when(anmService.getLocation("demo1")).thenReturn(location);
        when(location.phc()).thenReturn(phc);
        when(location.subCenter()).thenReturn("sc");
        when(phc.phcIdentifier()).thenReturn("phc1");
        when(location.phcName()).thenReturn("PHC X");

        ResponseEntity<VillagesDTO> response = controller.villagesForANM("demo1");
        VillagesDTO villagesDTO = new VillagesDTO("PHC X", "phc1", "sc", asList("village1", "village2"));
        assertEquals(villagesDTO, response.getBody());
    }
}
