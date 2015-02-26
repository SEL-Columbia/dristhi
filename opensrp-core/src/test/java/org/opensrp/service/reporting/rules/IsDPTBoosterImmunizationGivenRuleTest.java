package org.opensrp.service.reporting.rules;

import junit.framework.Assert;
import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsDPTBoosterImmunizationGivenRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsDPTBoosterImmunizationGivenRuleTest {
    private IsDPTBoosterImmunizationGivenRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDPTBoosterImmunizationGivenRule();
    }

    @Test
    public void shouldReturnTrueIfImmunizationsGivenContainsDPT() {
        SafeMap safeMap = new SafeMap(create("immunizationsGiven", "bcg opv_0 tt_1 dptbooster_1").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "bcg opv_0 tt_1 dptbooster_1").put("previousImmunizations", "bcg opv_0 tt_1").map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "bcg opv_0 tt_1 dptbooster_1").put("previousImmunizations", "").map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "bcg opv_0 tt_1 dptbooster_1").put("previousImmunizations", null).map()));
        Assert.assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfImmunizationsGivenDoesNotContainDPT() {
        boolean didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap().put("immunizationsGiven", null));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "opv_0 tt_1").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "dptbooster_1 opv_0 tt_1").put("previousImmunizations", "dptbooster_1").map()));
        Assert.assertFalse(didRuleApply);
    }
}
