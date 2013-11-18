package org.ei.drishti.service.reporting.rules;

import junit.framework.Assert;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;

public class IsMMRImmunizationGivenRuleTest {
    private IsMMRImmunizationGivenRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsMMRImmunizationGivenRule();
    }

    @Test
    public void shouldReturnTrueIfImmunizationsGivenContainsMMR() {
        SafeMap safeMap = new SafeMap(create("immunizationsGiven", "bcg tt_1 dptbooster_1 mmr").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "bcg tt_1 dptbooster_1 mmr").put("previousImmunizations", "bcg opv_0 tt_1").map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "bcg tt_1 dptbooster_1 mmr").put("previousImmunizations", "").map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "bcg tt_1 dptbooster_1 mmr").put("previousImmunizations", null).map()));
        Assert.assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfImmunizationsGivenDoesNotContainMMR() {
        boolean didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap().put("immunizationsGiven", null));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "opv_0 tt_1").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "opv_0 tt_1 mmr").put("previousImmunizations", "mmr").map()));
        Assert.assertFalse(didRuleApply);
    }
}
