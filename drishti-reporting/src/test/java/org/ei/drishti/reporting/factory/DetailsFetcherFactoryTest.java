package org.ei.drishti.reporting.factory;

import org.ei.drishti.reporting.controller.ANMDetailsFetcher;
import org.ei.drishti.reporting.controller.PHCUserDetailsFetcher;
import org.ei.drishti.reporting.controller.SCUserDetailsFetcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class DetailsFetcherFactoryTest {

    @Mock
    private SCUserDetailsFetcher scUserDetailsFetcher;
    @Mock
    private PHCUserDetailsFetcher phcUserDetailsFetcher;
    private DetailsFetcherFactory detailsFetcherFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        detailsFetcherFactory = new DetailsFetcherFactory(scUserDetailsFetcher, phcUserDetailsFetcher);
    }

    @Test
    public void shouldReturnPHCDetailsFetcherWhenUserRoleIsPHCUser() {
        ANMDetailsFetcher anmDetailsFetcher = detailsFetcherFactory.detailsFetcher(asList("ROLE_PHC_USER", "ROLE_USER"));
        assertTrue(anmDetailsFetcher instanceof PHCUserDetailsFetcher);
    }

    @Test
    public void shouldReturnPHCDetailsFetcherWhenUserRoleIsSCUser() {
        ANMDetailsFetcher anmDetailsFetcher = detailsFetcherFactory.detailsFetcher(asList("ROLE_USER"));
        assertTrue(anmDetailsFetcher instanceof SCUserDetailsFetcher);
    }

}
