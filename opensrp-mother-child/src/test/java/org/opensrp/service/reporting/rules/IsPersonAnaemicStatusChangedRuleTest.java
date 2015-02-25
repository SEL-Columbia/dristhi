package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsPersonAnaemicStatusChangedRule;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;

public class IsPersonAnaemicStatusChangedRuleTest {

    private IsPersonAnaemicStatusChangedRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPersonAnaemicStatusChangedRule();
    }

    @Test
    public void shouldReturnTrueWhenThePersonAnaemicStatusChanged() throws Exception {
        SafeMap reportFields = new SafeMap(create("previousAnaemicStatus", "Anaemia")
                .put("anaemicStatus", "Severe_Anaemia")
                .map());

        boolean rulePassed = rule.apply(reportFields);

        assertTrue(rulePassed);

        reportFields = new SafeMap(create("previousAnaemicStatus", "")
                .put("anaemicStatus", "Severe_Anaemia")
                .map());

        rulePassed = rule.apply(reportFields);

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenThePersonAnaemicStatusIsNotChanges() throws Exception {
        SafeMap reportFields = new SafeMap(create("previousAnaemicStatus", "Severe_Anaemia")
                .put("anaemicStatus", "Severe_Anaemia")
                .map());

        boolean rulePassed = rule.apply(reportFields);

        assertFalse(rulePassed);
    }
}
