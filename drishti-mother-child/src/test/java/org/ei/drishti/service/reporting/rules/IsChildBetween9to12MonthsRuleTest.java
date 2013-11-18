package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsChildBetween9to12MonthsRuleTest {

    private IsChildBetween9to12MonthsRule rule;

    @Before
    public void setUp() {
        rule = new IsChildBetween9to12MonthsRule();
    }

    @Test
    public void shouldReturnFalseWhenChildIsNotBetween9To12Months() {
        Map<String, String> reportData = create("serviceProvidedDate", "2012-10-01")
                .put("dateOfBirth", "2012-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2013-01-01")
                .put("dateOfBirth", "2012-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildIsBetween9To12Months() {
        Map<String, String> reportData = create("serviceProvidedDate", "2012-10-02")
                .put("dateOfBirth", "2012-01-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2012-12-31")
                .put("dateOfBirth", "2012-01-01")
                .map();

        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);
    }
}
