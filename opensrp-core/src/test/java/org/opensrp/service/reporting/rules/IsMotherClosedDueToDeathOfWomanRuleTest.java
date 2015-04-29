package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.opensrp.common.util.EasyMap.create;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;


public class IsMotherClosedDueToDeathOfWomanRuleTest {
    private IsMotherClosedDueToDeathOfWomanRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsMotherClosedDueToDeathOfWomanRule();
    }

    @Test
    public void shouldReturnTrueIfCloseReasonIsDeathOfWoman() {
        SafeMap safeMap = new SafeMap(create("closeReason", "death_of_woman").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathReasonIsNotDeathOfWoman() {

        boolean didRuleApply = rule.apply(new SafeMap(create("closeReason", "permanent_relocation").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("closeReason", "death_of_mother").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap().put("closeReason", null));
        assertFalse(didRuleApply);
    }
}
