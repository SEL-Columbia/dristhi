package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsChildDiseaseReportedAsMalariaRuleTest {

    IsChildDiseaseReportedAsMalariaRule rule;

    @Before
    public void setUp() {
        rule = new IsChildDiseaseReportedAsMalariaRule();
    }

    @Test
    public void shouldReturnFalseWhenChildDiseaseReportedIsNotMalaria() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("reportChildDisease", "diarrhea_dehydration")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildDiseaseReportedIsMalaria() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("reportChildDisease", "malaria")));

        assertTrue(didRuleSucceed);
    }
}
