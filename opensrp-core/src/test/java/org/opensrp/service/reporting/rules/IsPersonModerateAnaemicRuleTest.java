package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsPersonModerateAnaemicRule;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;

public class IsPersonModerateAnaemicRuleTest {

    private IsPersonModerateAnaemicRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPersonModerateAnaemicRule();
    }

    @Test
    public void shouldReturnTrueWhenHBLevelIsGreaterThanOrEqualToSevenAndLessThanEleven() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(create("hbLevel", "7").map()));

        assertTrue(rulePassed);

        rulePassed = rule.apply(new SafeMap(create("hbLevel", "10.9").map()));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenHBLevelIsLessThanSeven() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(create("hbLevel", "6").map()));

        assertFalse(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenHBLevelIsGreaterThanEleven() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(create("hbLevel", "12").map()));

        assertFalse(rulePassed);
    }
}
