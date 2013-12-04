package org.ei.drishti.web.controller;

import org.ei.drishti.domain.DrishtiUser;
import org.ei.drishti.dto.ANMDTO;
import org.ei.drishti.service.ANMService;
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
                        new DrishtiUser("username1", "password1", "salt1", asList("ROLE_USER"), true),
                        new DrishtiUser("username2", "password2", "salt2", asList("ROLE_USER"), true)
                ));

        ResponseEntity<List<ANMDTO>> response = controller.all();

        assertEquals(asList(new ANMDTO("username1"), new ANMDTO("username2")), response.getBody());
    }
}
