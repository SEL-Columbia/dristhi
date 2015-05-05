package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsChildBelowThreeYearsRule;

import java.util.Map;

import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class IsChildBelowThreeYearsRuleTest {

    IsChildBelowThreeYearsRule rule;

    @Before
    public void setUp() {
        rule = new IsChildBelowThreeYearsRule();
    }

    @Test
    public void shouldReturnFalseWhenChildIsNotBelowThreeYears() {
        Map<String, String> reportData = create("vitaminADate", "2012-01-01")
                .put("dateOfBirth", "2002-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);

        reportData = create("vitaminADate", "2012-01-01")
                .put("dateOfBirth", "2007-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildIsBelowThreeYears() {
        Map<String, String> reportData = create("vitaminADate", "2010-12-30")
                .put("dateOfBirth", "2008-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);
    }
}
