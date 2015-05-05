package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.CasteIsSCRule;

import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class CasteIsSCRuleTest {

    private CasteIsSCRule rule;

    @Before
    public void setUp() {
        rule = new CasteIsSCRule();
    }

    @Test
    public void shouldReturnFalseWhenCasteIsNotSC() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "st")));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "c_others")));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenCasteIsSC() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "sc")));

        assertTrue(didRuleSucceed);
    }
}
