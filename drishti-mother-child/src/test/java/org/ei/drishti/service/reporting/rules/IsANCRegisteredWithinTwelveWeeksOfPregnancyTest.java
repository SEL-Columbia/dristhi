package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsANCRegisteredWithinTwelveWeeksOfPregnancyTest {

    private IsANCRegisteredWithinTwelveWeeksOfPregnancy rule;

    @Before
    public void setUp() {
        rule = new IsANCRegisteredWithinTwelveWeeksOfPregnancy();
    }

    @Test
    public void shouldReturnTrueWhenANCIsRegisteredWithin12Weeks() {
        Map<String, String> reportData = create("serviceProvidedDate", "2012-10-23")
                .put("referenceDate", "2012-10-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2013-03-26")
                .put("referenceDate", "2013-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);
    }

    @Test
    public void shouldReturnFalseWhenANCIsRegisteredAfter12Weeks() {
        Map<String, String> reportData = create("serviceProvidedDate", "2013-10-23")
                .put("referenceDate", "2012-10-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2013-03-27")
                .put("referenceDate", "2013-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);
    }


}

