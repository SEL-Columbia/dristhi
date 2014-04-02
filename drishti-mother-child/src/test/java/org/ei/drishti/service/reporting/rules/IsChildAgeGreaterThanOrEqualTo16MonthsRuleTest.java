package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsChildAgeGreaterThanOrEqualTo16MonthsRuleTest {

    private IsChildAgeGreaterThanOrEqualTo16MonthsRule rule;

    @Before
    public void setUp() {
        rule = new IsChildAgeGreaterThanOrEqualTo16MonthsRule();
    }

    @Test
    public void shouldReturnFalseWhenChildIsBelow16Months() {
        Map<String, String> reportData = create("serviceProvidedDate", "2012-12-01")
                .put("dateOfBirth", "2012-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2013-04-01")
                .put("dateOfBirth", "2012-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildAgeIsGreaterThanOrEqualTo16Months() {
        Map<String, String> reportData = create("serviceProvidedDate", "2013-04-02")
                .put("dateOfBirth", "2012-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2014-05-02")
                .put("dateOfBirth", "2012-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);

    }
}

