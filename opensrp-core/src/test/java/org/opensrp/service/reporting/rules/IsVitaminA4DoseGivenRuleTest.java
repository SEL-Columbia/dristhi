package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsVitaminA4DoseGivenRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsVitaminA4DoseGivenRuleTest {
    private IsVitaminA4DoseGivenRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsVitaminA4DoseGivenRule();
    }

    @Test
    public void shouldReturnTrueIfVitaminA4DoseIsGiven() {
        SafeMap safeMap = new SafeMap(create("vitaminADose", "4").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfVitaminA4DoseIsNotGiven() {

        boolean didRuleApply = rule.apply(new SafeMap(create("vitaminADose", "1").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("vitaminADose", "").map()));
        assertFalse(didRuleApply);
    }
}
