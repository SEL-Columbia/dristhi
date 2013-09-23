package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class RelocationIsPermanentRuleTest {

    private RelocationIsPermanentRule relocationIsPermanentRule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        relocationIsPermanentRule = new RelocationIsPermanentRule();
    }

    @Test
    public void shouldReturnTrueIfRelocationIsPermanent() throws Exception {
        boolean didRuleApply = relocationIsPermanentRule.apply(new SafeMap().put("closeReason", "permanent_relocation"));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseForAnyOtherCloseReason() throws Exception {
        boolean didRuleApply = relocationIsPermanentRule.apply(new SafeMap().put("closeReason", "child_death"));

        assertFalse(didRuleApply);
    }
}
