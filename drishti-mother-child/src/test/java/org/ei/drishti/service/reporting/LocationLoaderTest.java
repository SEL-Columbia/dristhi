package org.ei.drishti.service.reporting;

import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationLoaderTest {
    @Mock
    private AllChildren allChildren;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private Child child;
    @Mock
    private Mother mother;
    @Mock
    private EligibleCouple eligibleCouple;

    private ILocationLoader locationLoader;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        locationLoader = new LocationLoader(allEligibleCouples, allMothers, allChildren);
    }

    @Test
    public void shouldReturnLocationFromChildEntityIfBindTypeIsChild() {
        when(allChildren.findByCaseId("child id 1")).thenReturn(child);

        locationLoader.loadLocationFor("child", "child id 1");

        verify(child).location();
    }

    @Test
    public void shouldReturnLocationFromMotherEntityIfBindTypeIsMother() {
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);

        locationLoader.loadLocationFor("mother", "mother id 1");

        verify(mother).location();
    }

    @Test
    public void shouldReturnLocationFromEligibleCoupleEntityIfBindTypeIsEligibleCouple() {
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);

        locationLoader.loadLocationFor("eligible_couple", "ec id 1");

        verify(eligibleCouple).location();
    }
}
