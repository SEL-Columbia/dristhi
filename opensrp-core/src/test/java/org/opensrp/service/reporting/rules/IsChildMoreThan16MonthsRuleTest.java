package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class IsChildMoreThan16MonthsRuleTest {

    private IsChildMoreThan16MonthsRule rule;

    @Before
    public void setUp() {
        rule = new IsChildMoreThan16MonthsRule();
    }

    @Test
    public void shouldReturnFalseWhenChildIsBelow16Months() {
        Map<String, String> reportData = create("serviceProvidedDate", "2012-12-01")
                .put("dateOfBirth", "2012-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2013-05-01")
                .put("dateOfBirth", "2012-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildIsAbove16Months() {
        Map<String, String> reportData = create("serviceProvidedDate", "2013-05-02")
                .put("dateOfBirth", "2012-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);

    }
}

