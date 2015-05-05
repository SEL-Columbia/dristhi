package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsChildBelowFiveYearsRule;

import java.util.Map;

import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class IsChildBelowFiveYearsRuleTest {

    IsChildBelowFiveYearsRule rule;

    @Before
    public void setUp() {
        rule = new IsChildBelowFiveYearsRule();
    }

    @Test
    public void shouldReturnFalseWhenChildIsNotBelowFiveYears() {
        Map<String, String> reportData = create("reportChildDiseaseDate", "2012-01-01")
                .put("dateOfBirth", "2002-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);

        reportData = create("reportChildDiseaseDate", "2012-01-01")
                .put("dateOfBirth", "2007-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildIsBelowFiveYears() {
        Map<String, String> reportData = create("reportChildDiseaseDate", "2011-12-30")
                .put("dateOfBirth", "2007-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);
    }
}
