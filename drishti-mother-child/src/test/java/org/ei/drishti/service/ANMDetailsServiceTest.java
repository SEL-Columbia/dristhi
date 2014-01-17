package org.ei.drishti.service;

import org.ei.drishti.domain.ANMDetail;
import org.ei.drishti.domain.ANMDetails;
import org.ei.drishti.dto.ANMDTO;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANMDetailsServiceTest {

    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllChildren allChildren;
    private ANMDetailsService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ANMDetailsService(allEligibleCouples, allMothers, allChildren);
    }

    @Test
    public void shouldGetBeneficiariesCountForANM() {
        when(allEligibleCouples.ecCountForANM("demo1")).thenReturn(1);
        when(allEligibleCouples.fpCountForANM("demo1")).thenReturn(2);
        when(allMothers.ancCountForANM("demo1")).thenReturn(3);
        when(allMothers.pncCountForANM("demo1")).thenReturn(4);
        when(allChildren.childCountForANM("demo1")).thenReturn(5);

        ANMDetails anmDetails = service.anmDetails(asList(new ANMDTO("demo1", "demo1", null)));

        assertEquals(new ANMDetails(asList(new ANMDetail("demo1", "demo1", null, 1, 2, 3, 4, 5))), anmDetails);
    }
}
