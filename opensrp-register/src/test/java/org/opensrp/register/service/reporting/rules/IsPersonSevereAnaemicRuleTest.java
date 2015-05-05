package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsPersonSevereAnaemicRule;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;

public class IsPersonSevereAnaemicRuleTest {

    private IsPersonSevereAnaemicRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPersonSevereAnaemicRule();
    }

    @Test
    public void shouldReturnTrueWhenHBLevelIsLessThanSeven() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(create("hbLevel", "6.9").map()));

        assertTrue(rulePassed);

        rulePassed = rule.apply(new SafeMap(create("hbLevel", "2").map()));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenHBLevelIsGreaterThanOrEqualToSeven() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(create("hbLevel", "7").map()));

        assertFalse(rulePassed);

        rulePassed = rule.apply(new SafeMap(create("hbLevel", "11").map()));

        assertFalse(rulePassed);
    }
}
