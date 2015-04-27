package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.opensrp.common.util.EasyMap.create;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;


public class IsPentavalent3ImmunizationGivenRuleTest {
    private IsPentavalent3ImmunizationGivenRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPentavalent3ImmunizationGivenRule();
    }

    @Test
    public void shouldReturnTrueIfImmunizationsGivenContainsPentavalent_3() {
        SafeMap safeMap = new SafeMap(create("immunizationsGiven", "pentavalent_3 opv_0 tt_1").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "pentavalent_3 opv_0 tt_1").put("previousImmunizations", "opv_0").map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "pentavalent_3 opv_0 tt_1").put("previousImmunizations", "").map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "pentavalent_3 opv_0 tt_1").put("previousImmunizations", null).map()));
        Assert.assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfImmunizationsGivenDoesNotContainPentavalent_3() {
        boolean didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap().put("immunizationsGiven", null));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "opv_0 tt_1").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immunizationsGiven", "pentavalent_3 opv_0 tt_1").put("previousImmunizations", "pentavalent_3").map()));
        Assert.assertFalse(didRuleApply);
    }
}
