package org.ei.drishti.service.reporting.rules;

import junit.framework.Assert;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;

public class IsOPV2ImmunizationGivenRuleTest {
    private IsOPV2ImmunizationGivenRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsOPV2ImmunizationGivenRule();
    }

    @Test
    public void shouldReturnTrueIfImmunizationsGivenContainsOPV_2() {
        SafeMap safeMap = new SafeMap(create("immunizationsGiven", "pentavalent_1 opv_2 tt_1").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "pentavalent_1 opv_2 tt_1").put("previousImmunizations", "pentavalent_1 tt_1").map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "pentavalent_1 opv_2 tt_1").put("previousImmunizations", "").map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "pentavalent_1 opv_2 tt_1").put("previousImmunizations", null).map()));
        Assert.assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfImmunizationsGivenDoesNotContainOPV_2() {
        boolean didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap().put("immunizationsGiven", null));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "pentavalent_1 tt_1").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "pentavalent_1 opv_2 tt_1").put("previousImmunizations", "opv_2").map()));
        Assert.assertFalse(didRuleApply);
    }
}
