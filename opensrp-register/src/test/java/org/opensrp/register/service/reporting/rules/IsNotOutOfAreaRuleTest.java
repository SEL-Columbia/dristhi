package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsNotOutOfAreaRule;

import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class IsNotOutOfAreaRuleTest {

    IsNotOutOfAreaRule rule;

    @Before
    public void setUp() {
        rule = new IsNotOutOfAreaRule();
    }

    @Test
    public void shouldReturnFalseWhenTheEntityIsOutOfArea() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("isOutOfArea", "true")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenTheEntityNotIsOutOfArea() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("isOutOfArea", "false")));

        assertTrue(didRuleSucceed);
    }
}
