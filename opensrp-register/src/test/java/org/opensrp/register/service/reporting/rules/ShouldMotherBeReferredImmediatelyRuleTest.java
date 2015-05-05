package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.ShouldMotherBeReferredImmediatelyRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class ShouldMotherBeReferredImmediatelyRuleTest {
    private ShouldMotherBeReferredImmediatelyRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new ShouldMotherBeReferredImmediatelyRule();
    }

    @Test
    public void shouldReturnTrueIfMotherHasToBeRefferedImmediately() {
        SafeMap safeMap = new SafeMap(create("immediateReferral", "yes").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfMotherDoesNotNeedToBeReferredImmediately() {

        boolean didRuleApply = rule.apply(new SafeMap(create("immediateReferral", "no").map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("immediateReferral", "").map()));
        assertFalse(didRuleApply);
    }
}
