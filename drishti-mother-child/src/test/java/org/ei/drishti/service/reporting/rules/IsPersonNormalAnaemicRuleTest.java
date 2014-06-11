package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;

public class IsPersonNormalAnaemicRuleTest {

    private IsPersonNormalAnaemicRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPersonNormalAnaemicRule();
    }

    @Test
    public void shouldReturnTrueWhenHBLevelIsGreaterThanOrEqualToEleven() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(create("hbLevel", "11").map()));

        assertTrue(rulePassed);

        rulePassed = rule.apply(new SafeMap(create("hbLevel", "12").map()));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenHBLevelIsLessThanEleven() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(create("hbLevel", "6").map()));

        assertFalse(rulePassed);
    }
}
