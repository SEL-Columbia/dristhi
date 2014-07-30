package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;

public class IsMotherDidNotSurvivedOnDeliveryRuleTest {
    private IsMotherDidNotSurvivedOnDeliveryRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsMotherDidNotSurvivedOnDeliveryRule();
    }

    @Test
    public void shouldReturnTrueIfMotherDidNotSurviveOnDelivery() {
        SafeMap safeMap = new SafeMap(create("didWomanSurvive", "no")
                .put("didMotherSurvive", "no").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);

        safeMap = new SafeMap(create("didWomanSurvive", "yes")
                .put("didMotherSurvive", "no").map());

        didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);

        safeMap = new SafeMap(create("didWomanSurvive", "no")
                .put("didMotherSurvive", "yes").map());

        didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);

        safeMap = new SafeMap().put("didWomanSurvive", "no")
                .put("didMotherSurvive", null);

        didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);

        safeMap = new SafeMap().put("didWomanSurvive", null)
                .put("didMotherSurvive", "no");

        didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfMotherSurvivedOnDelivery() {

        SafeMap safeMap = new SafeMap(create("didWomanSurvive", "yes")
                .put("didMotherSurvive", "yes").map());

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);

        safeMap = new SafeMap().put("didWomanSurvive", null)
                .put("didMotherSurvive", "yes");

        didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);

        safeMap = new SafeMap().put("didWomanSurvive", "yes")
                .put("didMotherSurvive", null);

        didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
