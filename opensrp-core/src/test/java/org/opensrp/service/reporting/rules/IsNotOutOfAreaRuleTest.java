package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



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
