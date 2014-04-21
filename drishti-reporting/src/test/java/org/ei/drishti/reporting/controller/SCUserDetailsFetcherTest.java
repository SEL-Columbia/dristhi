package org.ei.drishti.reporting.controller;

import org.ei.drishti.reporting.service.ANMService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SCUserDetailsFetcherTest {

    @Mock
    private ANMService anmService;
    private SCUserDetailsFetcher scUserDetailsFetcher;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        scUserDetailsFetcher = new SCUserDetailsFetcher(anmService);
    }

    @Test
    public void shouldDelegateToANMServiceToFetchANMsFromSameSC() {
        scUserDetailsFetcher.fetchDetails("demo1");

        verify(anmService).anmsInTheSameSC("demo1");
    }

}
