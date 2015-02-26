package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.RelocationIsPermanentRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class RelocationIsPermanentRuleTest {

    private RelocationIsPermanentRule relocationIsPermanentRule;

    @Before
    public void setUp() throws Exception {
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
