package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.opensrp.common.util.EasyMap.create;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;


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
