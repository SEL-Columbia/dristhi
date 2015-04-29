package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



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
