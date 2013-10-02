package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsChildDiseaseReportedAsDiarrheaDehydrationRuleTest {

    IsChildDiseaseReportedAsDiarrheaDehydrationRule rule;

    @Before
    public void setUp() {
        rule = new IsChildDiseaseReportedAsDiarrheaDehydrationRule();
    }

    @Test
    public void shouldReturnFalseWhenChildDiseaseReportedIsNotDiarrheaDehydration() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("reportChildDisease", "measles")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildDiseaseReportedIsDiarrheaDehydration() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("reportChildDisease", "diarrhea_dehydration")));

        assertTrue(didRuleSucceed);
    }
}
