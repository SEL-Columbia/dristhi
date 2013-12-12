package org.ei.drishti.reporting.controller;

import org.ei.drishti.dto.ANMDTO;
import org.ei.drishti.reporting.domain.SP_ANM;
import org.ei.drishti.reporting.service.ANMService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANMControllerTest {

    @Mock
    private ANMService anmService;
    private ANMController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new ANMController("url", anmService);
    }

    @Test
    public void shouldReturnListOfANMs() throws Exception {
        when(anmService.all()).thenReturn(
                asList(
                        new SP_ANM("username1", "Sub Center 1", 0),
                        new SP_ANM("username2", "Sub Center 1", 0)
                ));

        ResponseEntity<List<ANMDTO>> response = controller.all();

        assertEquals(asList(new ANMDTO("username1"), new ANMDTO("username2")), response.getBody());
    }
}
