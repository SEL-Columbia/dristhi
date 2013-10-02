package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.ei.drishti.util.EasyMap.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PNCVisitHappenedAtSubCenterOrHomeRuleTest {

    private PNCVisitHappenedAtSubCenterOrHomeRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new PNCVisitHappenedAtSubCenterOrHomeRule();
    }

    @Test
    public void shouldReturnTrueWhenPNCVisitHappenedInSubCenterOrHome() {
        Map<String, String> reportFields =
                create("pncVisitPlace", "sub_center")
                        .map();

        boolean didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertTrue(didRuleApplied);

        reportFields =
                create("pncVisitPlace", "home")
                        .map();

        didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertTrue(didRuleApplied);
    }

    @Test
    public void shouldReturnFalseWhenPNCVisitDidNotHappenedInSubCenterOrHome() {
        Map<String, String> reportFields =
                create("pncVisitPlace", "phc")
                        .map();

        boolean didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertFalse(didRuleApplied);

        reportFields =
                create("pncVisitPlace", "elsewhere")
                        .map();

        didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertFalse(didRuleApplied);
    }
}
