package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsChildDiseaseReportedAsMeaslesRule;

import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class IsChildDiseaseReportedAsMeaslesRuleTest {

    IsChildDiseaseReportedAsMeaslesRule rule;

    @Before
    public void setUp() {
        rule = new IsChildDiseaseReportedAsMeaslesRule();
    }

    @Test
    public void shouldReturnFalseWhenChildDiseaseReportedIsNotMeasles() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("reportChildDisease", "diarrhea_dehydration")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildDiseaseReportedIsMeasles() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("reportChildDisease", "measles")));

        assertTrue(didRuleSucceed);
    }
}
