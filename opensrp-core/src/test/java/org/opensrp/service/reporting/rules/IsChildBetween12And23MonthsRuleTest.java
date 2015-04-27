package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class IsChildBetween12And23MonthsRuleTest {

    private IsChildBetween12And23MonthsRule rule;

    @Before
    public void setUp() {
        rule = new IsChildBetween12And23MonthsRule();
    }

    @Test
    public void shouldReturnFalseWhenChildIsNotBetween12To23Months() {
        Map<String, String> reportData = create("serviceProvidedDate", "2012-10-01")
                .put("dateOfBirth", "2012-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2012-12-31")
                .put("dateOfBirth", "2012-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildIsBetween12To23Months() {
        Map<String, String> reportData = create("serviceProvidedDate", "2013-01-02")
                .put("dateOfBirth", "2012-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2013-12-31")
                .put("dateOfBirth", "2012-01-01")
                .map();

        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);
    }
}

