package org.ei.drishti.reporting.controller;

import org.ei.drishti.reporting.service.ANMService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PHCUserDetailsFetcherTest {

    @Mock
    private ANMService anmService;
    private PHCUserDetailsFetcher phcUserDetailsFetcher;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        phcUserDetailsFetcher = new PHCUserDetailsFetcher(anmService);
    }

    @Test
    public void shouldDelegateToANMServiceToFetchANMsFromSamePHC() {
        phcUserDetailsFetcher.fetchDetails("demo1");

        verify(anmService).anmsInTheSamePHC("demo1");
    }

}
