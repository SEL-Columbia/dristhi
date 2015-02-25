package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsVitaminA9DoseGivenRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsVitaminA9DoseGivenRuleTest {
    private IsVitaminA9DoseGivenRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsVitaminA9DoseGivenRule();
    }

    @Test
    public void shouldReturnTrueIfVitaminA9DoseIsGiven() {
        SafeMap safeMap = new SafeMap(create("vitaminADose", "9").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfVitaminA9DoseIsNotGiven() {

        boolean didRuleApply = rule.apply(new SafeMap(create("vitaminADose", "1").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("vitaminADose", "").map()));
        assertFalse(didRuleApply);
    }
}
