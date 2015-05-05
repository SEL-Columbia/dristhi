package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsMotherHavingDiabeticRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsMotherHavingDiabeticRuleTest {
    private IsMotherHavingDiabeticRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsMotherHavingDiabeticRule();
    }

    @Test
    public void shouldReturnTrueIfMotherHasDiabetics() {
        SafeMap safeMap = new SafeMap(create("testsResultPositive", "urine_sugar urine_albumin").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfMotherDoesNotHaveDiabetics() {
        boolean didRuleApply = rule.apply(new SafeMap(create("testsResultPositive", "").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap().put("testsResultPositive", "urine_albumin"));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap().put("testsResultPositive", null));
        assertFalse(didRuleApply);
    }
}
